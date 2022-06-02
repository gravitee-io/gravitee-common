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
package io.gravitee.common.security.jwt;

import com.nimbusds.jose.Header;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import java.text.ParseException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class LazyJWT {

    private static final Logger log = LoggerFactory.getLogger(LazyJWT.class);

    private JWT jwt;

    private final String token;

    private Map<String, Object> headers;

    private Map<String, Object> claims;

    private boolean parsed = false;

    public LazyJWT(final String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public JWT getDelegate() {
        parse();
        return jwt;
    }

    public Map<String, Object> getHeaders() {
        if (parse()) {
            return headers;
        }

        return Collections.emptyMap();
    }

    public Map<String, Object> getClaims() {
        if (parse()) {
            return claims;
        }
        return Collections.emptyMap();
    }

    private boolean parse() {
        if (!parsed) {
            parsed = true;

            try {
                jwt = JWTParser.parse(token);
                if (jwt.getHeader() != null) {
                    headers = jwt.getHeader().toJSONObject();
                }
                if (jwt.getJWTClaimsSet() != null) {
                    claims = jwt.getJWTClaimsSet().getClaims();
                }
            } catch (ParseException ex) {
                // Nothing to do in case of a bad JWT token.
                log.debug("Error while parsing JWT token", ex);
            }
        }

        return jwt != null;
    }
}
