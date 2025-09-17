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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.gravitee.gateway.api.http.HttpHeaders;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Optional;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
class CertificateUtilsTest {

    private static final String CLIENT_CERT_HEADER = "X-ClientCert";
    private static final String CLIENT_CERT_THUMBPRINT = "2oHrNOqScxD8EHkb7_GYmnNvWqGj5M31Dqsrk3Jl2Yk";
    private String clientCertificate;
    private X509Certificate clientX509Certificate;
    private String rawBase64Certificate;

    @BeforeEach
    public void beforeEach() throws URISyntaxException, IOException, CertificateException {
        clientCertificate = Files.readString(Paths.get(CertificateUtilsTest.class.getResource("/client.crt").toURI()));

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        clientX509Certificate =
            (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(clientCertificate.getBytes()));

        rawBase64Certificate =
            Files.readString(Paths.get(CertificateUtilsTest.class.getResource("/expected_flatten_b64_body.txt").toURI()));
    }

    @Test
    void should_return_empty_certificate_without_header() {
        HttpHeaders httpHeaders = HttpHeaders.create();
        Optional<X509Certificate> certificateOptional = CertificateUtils.extractCertificate(httpHeaders, CLIENT_CERT_HEADER);

        assertThat(certificateOptional).isEmpty();
    }

    @Test
    void should_extract_encoded_certificate_from_header() {
        HttpHeaders httpHeaders = HttpHeaders.create();
        httpHeaders.set(CLIENT_CERT_HEADER, URLEncoder.encode(clientCertificate, StandardCharsets.UTF_8));
        Optional<X509Certificate> certificateOptional = CertificateUtils.extractCertificate(httpHeaders, CLIENT_CERT_HEADER);

        assertThat(certificateOptional).isNotEmpty();
        X509Certificate certificate = certificateOptional.get();
        assertThat(certificate).isEqualTo(clientX509Certificate);
    }

    @Test
    void should_extract_non_encoded_certificate_from_header() {
        HttpHeaders httpHeaders = HttpHeaders.create();
        httpHeaders.set(CLIENT_CERT_HEADER, clientCertificate);
        Optional<X509Certificate> certificateOptional = CertificateUtils.extractCertificate(httpHeaders, CLIENT_CERT_HEADER);

        assertThat(certificateOptional).isNotEmpty();
        X509Certificate certificate = certificateOptional.get();
        assertThat(certificate).isEqualTo(clientX509Certificate);
    }

    @Test
    void should_extract_raw_base64_certificate_from_header() {
        HttpHeaders httpHeaders = HttpHeaders.create();
        httpHeaders.set(CLIENT_CERT_HEADER, rawBase64Certificate);
        Optional<X509Certificate> certificateOptional = CertificateUtils.extractCertificate(httpHeaders, CLIENT_CERT_HEADER);

        assertThat(certificateOptional).isNotEmpty();
        X509Certificate certificate = certificateOptional.get();
        assertThat(certificate).isEqualTo(clientX509Certificate);
    }

    @Test
    void should_handle_invalid_certificate() {
        String invalidCertificate = "This is not a valid certificate";

        HttpHeaders httpHeaders = HttpHeaders.create();
        httpHeaders.set(CLIENT_CERT_HEADER, invalidCertificate);
        Optional<X509Certificate> certificateOptional = CertificateUtils.extractCertificate(httpHeaders, CLIENT_CERT_HEADER);

        assertThat(certificateOptional).isEmpty();
    }

    @Test
    void should_return_empty_certificate_without_ssl_session() {
        Optional<X509Certificate> certificateOptional = CertificateUtils.extractPeerCertificate(null);
        assertThat(certificateOptional).isEmpty();
    }

    @Test
    void should_extract_certificate_from_ssl_session() throws SSLPeerUnverifiedException {
        SSLSession sslSession = mock(SSLSession.class);
        when(sslSession.getPeerCertificates()).thenReturn(new Certificate[] { clientX509Certificate });
        Optional<X509Certificate> certificateOptional = CertificateUtils.extractPeerCertificate(sslSession);

        assertThat(certificateOptional).isNotEmpty();
        X509Certificate certificate = certificateOptional.get();
        assertThat(certificate).isEqualTo(clientX509Certificate);
    }

    @Test
    void should_return_null_when_generating_thumbprint_with_wrong_certificate() {
        String thumbprint = CertificateUtils.generateThumbprint(null, "SHA-256");
        assertThat(thumbprint).isNull();
    }

    @Test
    void should_return_null_when_generating_thumbprint_with_wrong_algo() {
        String thumbprint = CertificateUtils.generateThumbprint(clientX509Certificate, "wrong");
        assertThat(thumbprint).isNull();
    }

    @Test
    void should_generate_valid_thumbprint() {
        String thumbprint = CertificateUtils.generateThumbprint(clientX509Certificate, "SHA-256");
        assertThat(thumbprint).isEqualTo(CLIENT_CERT_THUMBPRINT);
    }
}
