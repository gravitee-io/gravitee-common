/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.common.util;

import static java.util.Arrays.asList;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.*;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemReader;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class KeyStoreUtils {

    public static final String TYPE_JKS = "JKS";
    public static final String TYPE_PEM = "PEM";
    public static final String TYPE_PKCS12 = "PKCS12";

    public static final String DEFAULT_ALIAS = "dummy-entry";
    public static final String DEFAULT_KEYSTORE_TYPE = TYPE_PKCS12;
    private static final int DNSNAME = 2;
    private static final Date DEFAULT_NOT_BEFORE = new Date(System.currentTimeMillis() - 31536000000L);
    private static final Date DEFAULT_NOT_AFTER = new Date(253402300799000L);
    private static final int DEFAULT_KEY_LENGTH_BITS = 2048;
    private static final String DEFAULT_SIGNATURE_ALGORITHM = "SHA256WithRSAEncryption";
    private static final String DEFAULT_ALGORITHM = "RSA";

    /**
     * Initializes and returns a {@link KeyStore} from a file path.
     *
     * @param format the format of the specified keystore. Could be PKCS12 or JKS.
     * @param path the path to the keystore file to initialize.
     * @param password the password required to read the keystore. <code>null</code> if not password.
     *
     * @return the initialized keystore or an {@link IllegalArgumentException} if the keystore can't be read or initialized.
     */
    public static KeyStore initFromPath(String format, String path, String password) {
        try (InputStream is = new File(path).toURI().toURL().openStream()) {
            final KeyStore keyStore = KeyStore.getInstance(format);
            keyStore.load(is, null == password ? null : password.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Unable to load keystore from path [%s]", path), e);
        }
    }

    /**
     * Initializes and returns a {@link KeyStore} from a base64 encoded content string.
     *
     * @param format the format of the specified keystore. Could be PKCS12 or JKS.
     * @param keystore the base64 encoded content representing the keystore to initialize.
     * @param password the password required to read the keystore. <code>null</code> if not password.
     *
     * @return the initialized keystore or an {@link IllegalArgumentException} if the keystore can't be read or initialized.
     */
    public static KeyStore initFromContent(String format, String keystore, String password) {
        try {
            final ByteArrayInputStream stream = new ByteArrayInputStream(Base64.getDecoder().decode(keystore));
            KeyStore keyStore = KeyStore.getInstance(format);
            keyStore.load(stream, password.toCharArray());
            return keyStore;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to get keystore from base64", e);
        }
    }

    /**
     * Initializes and returns a {@link KeyStore} containing a self-signed certificate.
     *
     * @param fqdn the fully qualified domain name to use as common name (CN) for the self signed certificate.
     * @param password the password to use to protect the keystore.
     *
     * @return the initialized keystore or an {@link IllegalArgumentException} if the keystore initialized.
     */
    public static KeyStore initSelfSigned(String fqdn, String password) {
        try {
            KeyStore keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
            keyStore.load(null, new char[0]);

            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(DEFAULT_ALGORITHM);
            final SecureRandom random = new SecureRandom();
            keyGen.initialize(DEFAULT_KEY_LENGTH_BITS, random);
            final KeyPair keypair = keyGen.generateKeyPair();

            PrivateKey privateKey = keypair.getPrivate();

            X500Name cn = new X500Name("CN=" + fqdn);
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                cn,
                new BigInteger(64, random),
                DEFAULT_NOT_BEFORE,
                DEFAULT_NOT_AFTER,
                cn,
                keypair.getPublic()
            );

            ContentSigner signer = new JcaContentSignerBuilder(DEFAULT_SIGNATURE_ALGORITHM).build(privateKey);
            X509CertificateHolder certHolder = builder.build(signer);

            final JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider());

            X509Certificate certificate = converter.getCertificate(certHolder);
            certificate.verify(keypair.getPublic());

            keyStore.setEntry(
                DEFAULT_ALIAS,
                new KeyStore.PrivateKeyEntry(privateKey, new Certificate[] { certificate }),
                new KeyStore.PasswordProtection(password.toCharArray())
            );

            return keyStore;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to get keystore from base64", e);
        }
    }

    /**
     * Initializes and returns a {@link KeyStore} from a list of paths to certificates in PEM format and a list of paths pointing to the corresponding private keys in PEM format.
     *
     * @param pemPathCertificates the list of certificate paths.
     * @param pemPathPrivateKeys the the list of private key paths.
     * @param password the password to use to protect the keystore. <code>null</code> if not password.
     *
     * @return the initialized keystore or an {@link IllegalArgumentException} if the certificates or private keys cannot be read or if the keystore can't be initialized.
     */
    public static KeyStore initFromPems(List<String> pemPathCertificates, List<String> pemPathPrivateKeys, String password) {
        if (pemPathCertificates.size() != pemPathPrivateKeys.size()) {
            throw new IllegalArgumentException(
                String.format(
                    "Mismatch between number of certificates (%s) and number of private keys (%s)",
                    pemPathCertificates.size(),
                    pemPathPrivateKeys.size()
                )
            );
        }

        try {
            final KeyStore keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
            final char[] charPassword = passwordToCharArray(password);
            keyStore.load(null, charPassword);

            for (int i = 0; i < pemPathCertificates.size(); i++) {
                try (
                    InputStream certIs = new File(pemPathCertificates.get(i)).toURI().toURL().openStream();
                    InputStream keyIs = new File(pemPathPrivateKeys.get(i)).toURI().toURL().openStream()
                ) {
                    final Certificate[] certificates = loadPemCertificates(new String(certIs.readAllBytes()));
                    final PrivateKey privateKey = loadPemPrivateKey(new String(keyIs.readAllBytes()));
                    keyStore.setEntry(
                        DEFAULT_ALIAS + "-" + i,
                        new KeyStore.PrivateKeyEntry(privateKey, certificates),
                        new KeyStore.PasswordProtection(charPassword)
                    );
                }
            }

            return keyStore;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to initialize keystore from pem certificate and private key", e);
        }
    }

    /**
     * Initializes and returns a {@link KeyStore} from a certificate content in PEM format and a the corresponding private key content in PEM format.
     *
     * @param pemCertificate the content of the certificate in PEM format.
     * @param pemPrivateKey the content of the private key in PEM format.
     * @param password the password to use to protect the keystore. <code>null</code> if not password.
     *
     * @return the initialized keystore or an {@link IllegalArgumentException} if the certificate or private key cannot be read or if the keystore can't be initialized.
     */
    public static KeyStore initFromPem(String pemCertificate, String pemPrivateKey, String password, String alias) {
        try {
            final KeyStore keyStore = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
            final Certificate[] certificates = loadPemCertificates(pemCertificate);
            final PrivateKey privateKey = loadPemPrivateKey(pemPrivateKey);

            keyStore.load(null, passwordToCharArray(password));
            keyStore.setEntry(
                alias == null ? DEFAULT_ALIAS : alias,
                new KeyStore.PrivateKeyEntry(privateKey, certificates),
                new KeyStore.PasswordProtection(passwordToCharArray(password))
            );

            return keyStore;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to initialize keystore from pem certificate and private key", e);
        }
    }

    /**
     * Returns the certificate chain from a PEM content.
     *
     * @param pem the content of the certificate chain in PEM format.
     *
     * @return the certificate chain or an {@link IllegalArgumentException} if the certificate chain cannot be read.
     */
    public static Certificate[] loadPemCertificates(String pem) throws Exception {
        JcaX509CertificateConverter converter = new JcaX509CertificateConverter().setProvider(new BouncyCastleProvider());

        final PemReader pemReader = new PemReader(new StringReader(pem));
        final List<X509Certificate> certificates = new ArrayList<>();

        try (PEMParser pemParser = new PEMParser(pemReader)) {
            Object o;

            while ((o = pemParser.readObject()) != null) {
                if (o instanceof X509CertificateHolder) {
                    X509Certificate certificate = converter.getCertificate((X509CertificateHolder) o);
                    if (certificate == null) {
                        continue;
                    }
                    certificates.add(certificate);
                }
            }
        }

        return certificates.toArray(new X509Certificate[0]);
    }

    /**
     * Returns the private key from a PEM content.
     *
     * @param pem the content of the private key in PEM format.
     *
     * @return the private key or an {@link IllegalArgumentException} if the private key cannot be read or has not been found.
     */
    public static PrivateKey loadPemPrivateKey(String pem) throws IOException {
        final JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PemReader pemReader = new PemReader(new StringReader(pem));

        try (PEMParser pemParser = new PEMParser(pemReader)) {
            Object o;

            while ((o = pemParser.readObject()) != null) {
                if (o instanceof PEMKeyPair) {
                    PrivateKey privateKey = converter.getPrivateKey(((PEMKeyPair) o).getPrivateKeyInfo());
                    if (privateKey == null) {
                        continue;
                    }
                    return privateKey;
                }
            }

            throw new IllegalArgumentException("No private key found for the specified pem content.");
        }
    }

    /**
     * Convert a password string into a char array.
     * Return an empty char array if the specified password is <code>null</code>
     *
     * @param password the password to convert to char array.
     *
     * @return the corresponding char array.
     */
    public static char[] passwordToCharArray(String password) {
        return password != null ? password.toCharArray() : new char[0];
    }

    /**
     * Find the first alias of the specified {@link KeyStore} and returns it as the default alias.
     *
     * @param keyStore the keystore from where to get the default alias.
     *
     * @return the first alias found or <code>null</code> if no alias has been found or an {@link IllegalArgumentException} if the alias can't be read from the specified {@link KeyStore}.
     */
    public static String getDefaultAlias(KeyStore keyStore) {
        try {
            if (keyStore.aliases().hasMoreElements()) {
                return keyStore.aliases().nextElement();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to get default alias from keystore.", e);
        }

        return null;
    }

    /**
     * Initialize a new {@link KeyStore} in PKCS12 format which is the combination of the 2 provided keystores.
     * Note: for convenience, we assume the password is the same for both the source keystores.
     *
     * @param source1 the first keystore to merge.
     * @param source2 the second keystore to merge.
     * @param password the password that protects the source keystores.
     *
     * @return a new {@link KeyStore} which is the combination of the 2 source keystores or an {@link IllegalArgumentException} if one of the source keystores can't be read.
     */
    public static KeyStore merge(KeyStore source1, KeyStore source2, String password) {
        return merge(asList(source1, source2), password);
    }

    /**
     * Initialize a new {@link KeyStore} in PKCS12 format which is the combination of all the provided keystores.
     * Note: for convenience, we assume the password is the same for all the source keystores.
     *
     * @param  sources the keystores to merge.
     * @param password the password that protects the source keystores.
     *
     * @return a new {@link KeyStore} which is the combination of all the source keystores or an {@link IllegalArgumentException} if one of the source keystores can't be read.
     */
    public static KeyStore merge(List<KeyStore> sources, String password) {
        try {
            final KeyStore destination = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
            destination.load(null, passwordToCharArray(password));
            sources.forEach(source -> copy(source, destination, password));
            return destination;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to merge the 2 keystores", e);
        }
    }

    /**
     * Copy all the {@link KeyStore}'s entries from source to destination {@link KeyStore}.
     * Note: for convenience, we assume the password is the same for all the source keystores.
     *
     * @param  source the source {@link KeyStore} containing the entries to copy.
     * @param destination the destination {@link KeyStore}.
     * @param password the password that protects the source keystores.
     *
     * @throws IllegalArgumentException if one of the keystores can be read or if copy to the destination {@link KeyStore} creates duplicate aliases.
     */
    public static void copy(KeyStore source, KeyStore destination, String password) {
        try {
            final KeyStore.PasswordProtection passwordProtection = new KeyStore.PasswordProtection(passwordToCharArray(password));
            final Enumeration<String> aliases = source.aliases();

            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                final KeyStore.Entry entry = source.getEntry(alias, passwordProtection);

                if (destination.containsAlias(alias)) {
                    throw new IllegalArgumentException(
                        String.format("The alias [%s] is present in both keystores. Aliases must be unique.", alias)
                    );
                }

                destination.setEntry(alias, entry, passwordProtection);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to copy source keystore into destination keystore", e);
        }
    }

    /**
     * Returns a {@link Map} with the following structure:
     * <ul>
     *     <li>key: a domain name (or wildcard domain)</li>
     *     <li>value: the matching alias</li>
     * </ul>
     *
     * The list of the domain names is extracted from certificate Common Name (CN) and Subject Alternative Names (SAN).
     * If multiple certificates matches the same CN or SAN, the last one will be kept.
     *
     * @param keyStore the {@link KeyStore} from where to extract the domain names and aliases or an {@link IllegalArgumentException} if the CN or SAN cannot be read from the specified {@link KeyStore}.
     *
     * @return the map of domain names and corresponding aliases.
     */
    public static Map<String, String> getCommonNamesByAlias(KeyStore keyStore) {
        try {
            Map<String, String> names = new HashMap<>();

            final Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                final Certificate certificate = keyStore.getCertificate(alias);

                if (certificate instanceof X509Certificate) {
                    final X509Certificate x509Certificate = (X509Certificate) certificate;
                    final X500Name x500name = new JcaX509CertificateHolder(x509Certificate).getSubject();
                    final RDN[] rdNs = x500name.getRDNs(BCStyle.CN);

                    if (rdNs.length > 0) {
                        names.put(IETFUtils.valueToString(rdNs[0].getFirst().getValue()), alias);
                    }

                    Collection<List<?>> altNames = x509Certificate.getSubjectAlternativeNames();
                    if (altNames != null) {
                        altNames
                            .stream()
                            .filter(entry -> (int) entry.get(0) == DNSNAME)
                            .forEach(entry -> names.put(entry.get(1).toString(), alias));
                    }
                }
            }

            return names;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to extract CN/SAN from keystore.", e);
        }
    }
}
