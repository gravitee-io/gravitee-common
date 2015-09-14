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
package io.gravitee.common.http;

/**
 * Gravitee specific HTTP header enumeration.
 *
 * @author Aurélien Bourdon (aurelien.bourdon at gmail.com)
 */
public enum GraviteeHttpHeader {

    /**
     * Response time used to compute the entire Policy chain, in ms.
     */
    X_GRAVITEE_RESPONSE_TIME("X-Gravitee-ResponseTime"),

    /**
     * Key to access to an API through the Gravitee gateway.
     */
    X_GRAVITEE_API_KEY("X-Gravitee-Api-Key"),

    /**
     * The name of the API handle by Gravitee gateway.
     * This header must always be set by the gateway.
     */
    X_GRAVITEE_API_NAME("X-Gravitee-Api-Name");

    private String headerKey;

    GraviteeHttpHeader(String headerKey) {
        this.headerKey = headerKey;
    }

    @Override
    public String toString() {
        return headerKey;
    }

}
