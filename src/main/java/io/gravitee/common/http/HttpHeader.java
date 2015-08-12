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
 * HTTP headers enumeration.
 *
 * @author Aurélien Bourdon (aurelien.bourdon at gmail.com)
 */
public enum HttpHeader {

    ACCESS_CONTROL_ALLOW_ORIGIN("Access-Control-Allow-Origin"),
    ACCESS_CONTROL_ALLOW_CREDENTIALS("Access-Control-Allow-Credentials"),
    ACCESS_CONTROL_EXPOSE_HEADERS("Access-Control-Expose-Headers"),
    ACCESS_CONTROL_MAX_AGE("Access-Control-Max-Age"),
    ACCESS_CONTROL_ALLOW_METHODS("Access-Control-Allow-Methods"),
    ACCESS_CONTROL_ALLOW_HEADERS("Access-Control-Allow-Headers");

    private String headerKey;

    HttpHeader(String headerKey) {
        this.headerKey = headerKey;
    }

    @Override
    public String toString() {
        return headerKey;
    }

}
