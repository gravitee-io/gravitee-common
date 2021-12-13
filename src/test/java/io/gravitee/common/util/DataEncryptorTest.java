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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

@ExtendWith(MockitoExtension.class)
public class DataEncryptorTest {

    private static final String SECRET_PROPERTY_KEY = "environment.property.key";
    private static final String SECRET_KEY = "XxXxXZRmurH3YtzFCrYATkSG8H65xwah";
    private static final String DEFAULT_SECRET_KEY = "XxXxXZ_DEFAULT_ONE_TkSG8H65xwah";
    private static final String PLAIN_VALUE = "i want to encrypt and decrypt this string";
    private static final String ENCRYPTED_VALUE = "AUkE3/wECYHLkkf+eh5PcKRETJdf+haNagK0bEk+R6DhPr2tm8/O7573fJ16IvFZ";

    @Mock
    private Environment environment;

    @Test
    public void should_encrypt_value() throws GeneralSecurityException {
        mockEnvironmentContainingSecretKey();
        String encryptedString = buildDataEncryptor().encrypt(PLAIN_VALUE);
        assertEquals(ENCRYPTED_VALUE, encryptedString);
    }

    @Test
    public void should_decrypt_value() throws GeneralSecurityException {
        mockEnvironmentContainingSecretKey();
        String decryptedString = buildDataEncryptor().decrypt(ENCRYPTED_VALUE);
        assertEquals(PLAIN_VALUE, decryptedString);
    }

    @Test
    public void should_throw_exception_when_decrypting_invalid_encrypted_value() throws GeneralSecurityException {
        mockEnvironmentContainingSecretKey();
        assertThrows(IllegalArgumentException.class, () -> buildDataEncryptor().decrypt("this is not encrypted"));
    }

    @Test
    public void should_throw_exception_when_using_a_wrong_secret() throws GeneralSecurityException {
        mockEnvironmentContainingWrongSecretKey();
        assertThrows(InvalidKeyException.class, () -> buildDataEncryptor().encrypt(PLAIN_VALUE));
    }

    private DataEncryptor buildDataEncryptor() {
        return new DataEncryptor(environment, SECRET_PROPERTY_KEY, DEFAULT_SECRET_KEY);
    }

    private void mockEnvironmentContainingSecretKey() {
        when(environment.getProperty(SECRET_PROPERTY_KEY, DEFAULT_SECRET_KEY)).thenReturn(SECRET_KEY);
    }

    private void mockEnvironmentContainingWrongSecretKey() {
        when(environment.getProperty(SECRET_PROPERTY_KEY, DEFAULT_SECRET_KEY)).thenReturn("wrongSecretKey");
    }
}
