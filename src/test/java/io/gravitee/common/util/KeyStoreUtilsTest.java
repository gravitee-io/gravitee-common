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

import static io.gravitee.common.util.KeyStoreUtils.DEFAULT_ALIAS;
import static io.gravitee.common.util.KeyStoreUtils.TYPE_JKS;
import static io.gravitee.common.util.KeyStoreUtils.TYPE_PKCS12;
import static io.gravitee.common.util.KeyStoreUtils.getCommonNamesByAlias;
import static io.gravitee.common.util.KeyStoreUtils.merge;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class KeyStoreUtilsTest {

    @Nested
    class InitFromPath {

        @Test
        public void should_init_pkcs12_keystore_from_path() throws KeyStoreException {
            final KeyStore keyStore = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("all-in-one.p12"), "secret");

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(4);
        }

        @Test
        public void should_init_jks_keystore_from_path() throws KeyStoreException {
            final KeyStore keyStore = KeyStoreUtils.initFromPath(TYPE_JKS, getPath("all-in-one.jks"), "secret");

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(4);
        }
    }

    @Nested
    class InitFromContent {

        @Test
        public void should_init_pkcs12_from_content() throws IOException, KeyStoreException {
            final KeyStore keyStore = KeyStoreUtils.initFromContent(TYPE_PKCS12, readContent("all-in-one.p12"), "secret");

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(4);
        }

        @Test
        public void should_init_jks_from_content() throws IOException, KeyStoreException {
            final KeyStore keyStore = KeyStoreUtils.initFromContent(TYPE_JKS, readContent("all-in-one.jks"), "secret");

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(4);
        }
    }

    @Nested
    class LoadPemPrivateKey {

        @Test
        public void should_load_pkcs8_key() throws IOException {
            final PrivateKey privateKey = KeyStoreUtils.loadPemPrivateKey(readContent("rsakey.pem", false));

            assertThat(privateKey).isNotNull();
        }
    }

    @Nested
    class InitFromPem {

        @Test
        public void should_init_from_certificate_and_key_content() throws IOException, KeyStoreException {
            final String certPem = readContent("localhost.cer", false);
            final String pkPem = readContent("localhost.key", false);
            final KeyStore keyStore = KeyStoreUtils.initFromPem(certPem, pkPem, "secret", null);

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(1).contains(DEFAULT_ALIAS);
        }

        @Test
        public void should_init_from_certificate_and_key_content_with_custom_alias() throws IOException, KeyStoreException {
            final String certPem = readContent("localhost.cer", false);
            final String pkPem = readContent("localhost.key", false);
            final KeyStore keyStore = KeyStoreUtils.initFromPem(certPem, pkPem, "secret", "custom-alias");

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(1).contains("custom-alias");
        }

        @Test
        public void should_init_from_certificate_and_key_paths() throws KeyStoreException {
            List<String> certs = List.of(
                getPath("localhost.cer"),
                getPath("localhost2.cer"),
                getPath("localhost3.cer"),
                getPath("wildcard.cer")
            );

            List<String> keys = List.of(
                getPath("localhost.key"),
                getPath("localhost2.key"),
                getPath("localhost3.key"),
                getPath("wildcard.key")
            );

            final KeyStore keyStore = KeyStoreUtils.initFromPems(certs, keys, "secret");

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(4);
        }

        @Test
        public void should_init_from_certificate_content() throws IOException, KeyStoreException {
            final String certPem = readContent("localhost.cer", false);
            final KeyStore keyStore = KeyStoreUtils.initFromPemCertificate(certPem, "secret", null);

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(1).contains(DEFAULT_ALIAS);
        }

        @Test
        public void should_init_from_certificate_content_containing_more_than_one_certificate() throws IOException, KeyStoreException {
            final String certPem = readContent("certificates.pem", false);
            final KeyStore keyStore = KeyStoreUtils.initFromPemCertificate(certPem, "secret", null);

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(2).contains(DEFAULT_ALIAS, DEFAULT_ALIAS + "_1");
        }

        @Test
        public void should_init_from_certificate_paths() throws KeyStoreException {
            List<String> certs = List.of(
                getPath("localhost.cer"),
                getPath("localhost2.cer"),
                getPath("localhost3.cer"),
                getPath("wildcard.cer")
            );

            final KeyStore keyStore = KeyStoreUtils.initFromPemCertificateFiles(certs, "secret");

            assertThat(keyStore.aliases().asIterator())
                .toIterable()
                .hasSize(4)
                .contains(DEFAULT_ALIAS, DEFAULT_ALIAS + "_1", DEFAULT_ALIAS + "_2", DEFAULT_ALIAS + "_3");
        }
    }

    @Nested
    class Merge {

        @Test
        public void should_merge_key_stores() throws KeyStoreException {
            final KeyStore source1 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("localhost.p12"), "secret");
            final KeyStore source2 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("localhost2.p12"), "secret");
            final KeyStore source3 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("localhost3.p12"), "secret");
            final KeyStore source4 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("wildcard.p12"), "secret");

            final KeyStore keyStore = merge(Arrays.asList(source1, source2, source3, source4), "secret");

            assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(4);
        }
    }

    @Nested
    class GetCommonNamesByAlias {

        @Test
        public void should_get_cn_aliases_with_SAN_and_wildcard() {
            final KeyStore keyStore = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("all-in-one.p12"), "secret");

            final Map<String, String> commonNamesByAlias = getCommonNamesByAlias(keyStore);

            assertThat(commonNamesByAlias)
                .hasSize(5)
                .containsEntry("localhost", "localhost")
                .containsEntry("localhost2", "localhost2")
                .containsEntry("localhost3", "localhost3")
                .containsEntry("xyz.localhost.com", "localhost3")
                .containsEntry("*.localhost.com", "wildcard");
        }
    }

    private String readContent(String resource) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(new File(getPath(resource)).toPath()));
    }

    private String readContent(String resource, boolean encode) throws IOException {
        if (encode) {
            return readContent(resource);
        }
        return new String(Files.readAllBytes(new File(getPath(resource)).toPath()));
    }

    private String getPath(String resource) {
        return Objects.requireNonNull(this.getClass().getResource("/keystores/" + resource)).getPath();
    }
}
