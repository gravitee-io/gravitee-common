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

import static io.gravitee.common.util.KeyStoreUtils.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class KeyStoreUtilsTest {

    @Test
    public void shouldInitFromPathPKCS12() throws IOException, KeyStoreException {
        final KeyStore keyStore = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("all-in-one.p12"), "secret");

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(4, keyStore.size());
    }

    @Test
    public void shouldInitFromContentPKCS12() throws IOException, KeyStoreException {
        final KeyStore keyStore = KeyStoreUtils.initFromContent(TYPE_PKCS12, readContent("all-in-one.p12"), "secret");

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(4, keyStore.size());
    }

    @Test
    public void shouldInitFromPathJKS() throws IOException, KeyStoreException {
        final KeyStore keyStore = KeyStoreUtils.initFromPath(TYPE_JKS, getPath("all-in-one.jks"), "secret");

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(4, keyStore.size());
    }

    @Test
    public void shouldInitFromContentJKS() throws IOException, KeyStoreException {
        final KeyStore keyStore = KeyStoreUtils.initFromContent(TYPE_JKS, readContent("all-in-one.jks"), "secret");

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(4, keyStore.size());
    }

    @Test
    public void shouldLoadPKCS8Key() throws IOException, KeyStoreException {
        final PrivateKey privateKey = KeyStoreUtils.loadPemPrivateKey(readContent("rsakey.pem", false));

        Assertions.assertNotNull(privateKey);
    }

    @Test
    public void shouldInitFromContentPem() throws IOException, KeyStoreException {
        final String certPem = readContent("localhost.cer", false);
        final String pkPem = readContent("localhost.key", false);
        final KeyStore keyStore = KeyStoreUtils.initFromPem(certPem, pkPem, "secret", null);

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(1, keyStore.size());
        Assertions.assertEquals(DEFAULT_ALIAS, keyStore.aliases().nextElement());
    }

    @Test
    public void shouldInitFromContentPemWithCustomAlias() throws IOException, KeyStoreException {
        final String certPem = readContent("localhost.cer", false);
        final String pkPem = readContent("localhost.key", false);
        final KeyStore keyStore = KeyStoreUtils.initFromPem(certPem, pkPem, "secret", "custom-alias");

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(1, keyStore.size());
        Assertions.assertEquals("custom-alias", keyStore.aliases().nextElement());
    }

    @Test
    public void shouldInitFromContentPathPems() throws IOException, KeyStoreException {
        List<String> certs = new ArrayList<>();
        List<String> keys = new ArrayList<>();

        certs.add(getPath("localhost.cer"));
        certs.add(getPath("localhost2.cer"));
        certs.add(getPath("localhost3.cer"));
        certs.add(getPath("wildcard.cer"));

        keys.add(getPath("localhost.key"));
        keys.add(getPath("localhost2.key"));
        keys.add(getPath("localhost3.key"));
        keys.add(getPath("wildcard.key"));

        final KeyStore keyStore = KeyStoreUtils.initFromPems(certs, keys, "secret");

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(4, keyStore.size());
    }

    @Test
    public void shouldMergeKeyStores() throws KeyStoreException {
        final KeyStore source1 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("localhost.p12"), "secret");
        final KeyStore source2 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("localhost2.p12"), "secret");
        final KeyStore source3 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("localhost3.p12"), "secret");
        final KeyStore source4 = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("wildcard.p12"), "secret");

        final KeyStore keyStore = merge(Arrays.asList(source1, source2, source3, source4), "secret");

        Assertions.assertNotNull(keyStore);
        Assertions.assertEquals(4, keyStore.size());
    }

    @Test
    public void shouldGetCNAliasesWithSANAndWildcard() {
        final KeyStore keyStore = KeyStoreUtils.initFromPath(TYPE_PKCS12, getPath("all-in-one.p12"), "secret");

        final Map<String, String> commonNamesByAlias = getCommonNamesByAlias(keyStore);

        Assertions.assertNotNull(commonNamesByAlias);
        Assertions.assertEquals(5, commonNamesByAlias.size());
        Assertions.assertEquals("localhost", commonNamesByAlias.get("localhost"));
        Assertions.assertEquals("localhost2", commonNamesByAlias.get("localhost2"));
        Assertions.assertEquals("localhost3", commonNamesByAlias.get("localhost3"));
        Assertions.assertEquals("localhost3", commonNamesByAlias.get("xyz.localhost.com"));
        Assertions.assertEquals("wildcard", commonNamesByAlias.get("*.localhost.com"));
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
        return this.getClass().getResource("/keystores/" + resource).getPath();
    }
}
