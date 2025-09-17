/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.security;

import com.nimbusds.jose.util.Base64URL;
import io.gravitee.gateway.api.http.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.net.ssl.SSLSession;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/**
 * @author Eric LELEU (eric.leleu at graviteesource.com)
 * @author GraviteeSource Team
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CertificateUtils {

    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/=]+$");

    public static boolean isBase64FlattenedCertBody(String certHeaderValue) {
        return (certHeaderValue.length() > 100 && (certHeaderValue.length() % 4 == 0) && BASE64_PATTERN.matcher(certHeaderValue).matches());
    }

    /**
     * This method gets the PeerCertificate from the HTTP Header using the header name provided as parameter.
     */
    public static Optional<X509Certificate> extractCertificate(final HttpHeaders httpHeaders, final String certHeader) {
        Optional<X509Certificate> certificate = Optional.empty();

        String certHeaderValue = StringUtils.hasText(certHeader) ? httpHeaders.get(certHeader) : null;

        if (certHeaderValue != null) {
            try {
                boolean hasFlattenedCertBody = isBase64FlattenedCertBody(certHeaderValue);

                if (!certHeaderValue.contains("\n") && !hasFlattenedCertBody) {
                    certHeaderValue = URLDecoder.decode(certHeaderValue, Charset.defaultCharset());
                }
                certHeaderValue = certHeaderValue.replaceAll("\t", "\n");
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

                byte[] byteInput = certHeaderValue.getBytes();
                if (hasFlattenedCertBody) {
                    byteInput = java.util.Base64.getDecoder().decode(certHeaderValue);
                }

                certificate =
                    Optional.ofNullable((X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(byteInput)));
            } catch (Exception e) {
                log.debug("Unable to retrieve peer certificate from request header '{}'", certHeader, e);
            }
        } else {
            log.debug("Header '{}' missing, unable to retrieve client certificate", certHeader);
        }

        return certificate;
    }

    /**
     * This method gets the PeerCertificate from the sslSession.
     */
    public static Optional<X509Certificate> extractPeerCertificate(final SSLSession sslSession) {
        Optional<X509Certificate> certificate = Optional.empty();

        if (sslSession != null) {
            try {
                Certificate[] peerCertificates = sslSession.getPeerCertificates();
                certificate = Optional.ofNullable((X509Certificate) peerCertificates[0]);
            } catch (Exception e) {
                log.debug("Unable to retrieve peer certificate from request", e);
            }
        } else {
            log.debug("No SSL session available to retrieve peer certificate");
        }

        return certificate;
    }

    /**
     * Generate a thumbprint of the given cert using the algorithm.
     */
    public static String generateThumbprint(final X509Certificate cert, final String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] der = cert.getEncoded();
            md.update(der);
            byte[] digest = md.digest();
            return Base64URL.encode(digest).toString();
        } catch (Exception e) {
            log.debug("Unable to generate thumbprint with given algorithm '{}'", algorithm, e);
            return null;
        }
    }
}
