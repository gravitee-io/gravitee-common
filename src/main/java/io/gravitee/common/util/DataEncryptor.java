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

import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * @author GraviteeSource Team
 */
public class DataEncryptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataEncryptor.class);
    private static final String AES_ALGORITHM = "AES";

    private String secret;

    /**
     * Builds a DataEncryptor, using a secret key from environment configuration.
     *
     * If secret key is not found in environment configuration,
     * it uses a default key and displays a security warning.
     *
     * @param environment spring environment
     * @param secretPropertyKey key of the property containing secret key in environment configuration
     * @param defaultSecret default secret key to use
     */
    public DataEncryptor(Environment environment, String secretPropertyKey, String defaultSecret) {
        this.secret = environment.getProperty(secretPropertyKey, defaultSecret);
        if (defaultSecret.equals(secret)) {
            LOGGER.warn("");
            LOGGER.warn("##############################################################");
            LOGGER.warn("#                      SECURITY WARNING                      #");
            LOGGER.warn("##############################################################");
            LOGGER.warn("");
            LOGGER.warn("You still use the default secret.");
            LOGGER.warn("This known secret can be used to access protected information.");
            LOGGER.warn("Please customize the '{}' parameter value, or ask your administrator to do it.", secretPropertyKey);
            LOGGER.warn("");
            LOGGER.warn("##############################################################");
            LOGGER.warn("");
        }
    }

    /**
     * Encrypt the specified value in a base64 representation.
     *
     * @param value the value to encrypt.
     * @return the base64 representation of the encrypted value.
     * @throws GeneralSecurityException thrown if an error occurred during encrypt.
     */
    public String encrypt(String value) throws GeneralSecurityException {
        byte[] encrypted = buildCipher(ENCRYPT_MODE).doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypt the specified base64 encrypted value.
     *
     * @param base64value the value to decrypt. Must be in base64.
     * @return the decrypted value.
     * @throws GeneralSecurityException thrown if an error occurred during decrypt.
     */
    public String decrypt(String base64value) throws GeneralSecurityException {
        byte[] decrypted = buildCipher(DECRYPT_MODE).doFinal(Base64.getDecoder().decode(base64value));
        return new String(decrypted);
    }

    private Cipher buildCipher(int cipherMode) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(cipherMode, toSecretKeySpec(secret), cipher.getParameters());
        return cipher;
    }

    private SecretKeySpec toSecretKeySpec(String secret) {
        return new SecretKeySpec(secret.getBytes(), AES_ALGORITHM);
    }
}
