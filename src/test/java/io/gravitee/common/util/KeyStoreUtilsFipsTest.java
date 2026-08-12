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
package io.gravitee.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.KeyStore;
import java.security.Security;
import org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider;
import org.junit.jupiter.api.Test;

/**
 * {@link KeyStoreUtils#FIPS_MODE} is a static final field evaluated once when {@link KeyStoreUtils} is first
 * loaded, so BCFIPS must be registered before anything in this JVM touches it. This class also needs a
 * classpath without the non-FIPS bcprov-jdk18on/bcpkix-jdk18on/bcutil-jdk18on jars, since the JVM refuses to
 * load both BC variants (they share package names under different signers). Both constraints are handled by
 * running this class as its own Surefire execution (id {@code fips-test} in pom.xml), which excludes those
 * jars via {@code classpathDependencyExcludes}. The provider is registered in a static initializer,
 * guaranteeing it runs before the first reference to {@link KeyStoreUtils} in that execution.
 */
public class KeyStoreUtilsFipsTest {

    static {
        Security.addProvider(new BouncyCastleFipsProvider());
    }

    @Test
    public void should_default_to_bcfks_and_round_trip_self_signed_keystore() throws Exception {
        assertThat(KeyStoreUtils.DEFAULT_KEYSTORE_TYPE).isEqualTo(KeyStoreUtils.TYPE_BCFKS);

        final KeyStore keyStore = KeyStoreUtils.initSelfSigned("fips.localhost", "secret");

        assertThat(keyStore.getType()).isEqualTo(KeyStoreUtils.TYPE_BCFKS);
        assertThat(keyStore.aliases().asIterator()).toIterable().hasSize(1).contains(KeyStoreUtils.DEFAULT_ALIAS);

        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        keyStore.store(out, "secret".toCharArray());

        final KeyStore reloaded = KeyStore.getInstance(KeyStoreUtils.TYPE_BCFKS);
        reloaded.load(new ByteArrayInputStream(out.toByteArray()), "secret".toCharArray());

        assertThat(reloaded.getCertificate(KeyStoreUtils.DEFAULT_ALIAS)).isNotNull();
        assertThat(reloaded.getKey(KeyStoreUtils.DEFAULT_ALIAS, "secret".toCharArray())).isNotNull();
    }
}
