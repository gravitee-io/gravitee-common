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
 * Represents an HTTP method.
 *
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public enum HttpMethod {

  CONNECT(1), DELETE(2), GET(3), HEAD(4), OPTIONS(5), PATCH(6), POST(7), PUT(8), TRACE(9), OTHER(0);

  private int code;

  HttpMethod(int code) {
    this.code = code;
  }

  public int code() {
    return code;
  }

  public static HttpMethod get(int code) {
    for(HttpMethod method : HttpMethod.values()) {
      if (method.code == code) {
        return method;
      }
    }


    return HttpMethod.OTHER;
  }
}
