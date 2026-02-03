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

import io.gravitee.common.util.KeyStoreUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.IntFunction;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PKCS7Utils {

    private PKCS7Utils() {}

    /**
     * Creates a PKCS7 bundle from a list of PEM-encoded certificates.
     *
     * @param pems a list of strings representing PEM-encoded certificates
     * @return a byte array representing the generated PKCS7 bundle
     * @throws IllegalArgumentException if the bundle creation fails due to invalid certificates or other issues
     */
    public static byte[] createBundle(List<String> pems) {
        try {
            List<Certificate> x509Certificates = new ArrayList<>();

            for (String pem : pems) {
                Certificate[] x509Cert = KeyStoreUtils.loadPemCertificates(pem);
                x509Certificates.addAll(Arrays.asList(x509Cert));
            }

            CMSSignedDataGenerator generator = new CMSSignedDataGenerator();
            generator.addCertificates(new JcaCertStore(x509Certificates));

            CMSSignedData signedData = generator.generate(new CMSProcessableByteArray(new byte[0]), false);
            return signedData.getEncoded();
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to create PKCS7 bundle from certificates", e);
        }
    }

    /**
     * Converts a PKCS7 binary data into a Java KeyStore instance, optionally allowing empty PKCS7 bundles.
     * The method attempts to extract X.509 certificates from the provided PKCS7 data,
     * assigns them aliases using the provided alias generator, and stores them in the KeyStore.
     * If the data is not in a valid PKCS7 format or no certificates are found (and empty bundles are not allowed),
     * an {@link Optional#empty()} is returned.
     *
     * @param data           the PKCS7 binary data containing the certificates
     * @param password       the password for the created KeyStore, can be null
     * @param aliasGenerator a function that generates unique aliases for certificates based on their index
     * @param allowEmpty     a flag indicating whether an empty PKCS7 bundle should be allowed
     * @return an {@link Optional} containing the created KeyStore if certificates are successfully extracted
     *         and added, or {@link Optional#empty()} if the format is invalid or no certificates
     *         are found and empty bundles are not allowed
     * @throws KeyStoreException        if there is an error in KeyStore creation or manipulation
     * @throws CertificateException     if there is an error in processing the X.509 certificates
     * @throws NoSuchAlgorithmException if the algorithm required for KeyStore operations is unavailable
     */
    public static Optional<KeyStore> pkcs7ToTruststore(
        byte[] data,
        String password,
        IntFunction<String> aliasGenerator,
        boolean allowEmpty
    ) throws KeyStoreException, CertificateException, NoSuchAlgorithmException {
        try {
            Collection<X509CertificateHolder> certHolders = new CMSSignedData(data).getCertificates().getMatches(null);

            if (!allowEmpty && certHolders.isEmpty()) {
                throw new IllegalArgumentException("No certificates found in PKCS7 bundle");
            }

            KeyStore ks = KeyStore.getInstance(KeyStoreUtils.DEFAULT_KEYSTORE_TYPE);
            ks.load(null, password != null ? password.toCharArray() : null);

            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            int index = 0;

            for (X509CertificateHolder holder : certHolders) {
                X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(
                    new ByteArrayInputStream(holder.getEncoded())
                );
                ks.setCertificateEntry(aliasGenerator.apply(index), cert);
                index++;
            }
            return Optional.of(ks);
        } catch (CMSException | IOException e) {
            // Not a valid PKCS7/CMS format
            return Optional.empty();
        }
    }
}
