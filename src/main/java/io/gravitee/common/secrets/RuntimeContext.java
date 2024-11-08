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
package io.gravitee.common.secrets;

/**
 * Object representing the ability for a secret to be used at runtime.
 * This object is added in the EL context then removed after evaluation.
 * EL expression may use this context to allow or deny access to the secret.
 *
 * @param allowed If a secret can be resolved (true = the plugin/entity field supports secrets)
 * @param valueKind the secret value kind admitted for this field
 * @param field the field name where the secret is used
 *
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public record RuntimeContext(boolean allowed, ValueKind valueKind, String field) {
    public static final String EL_VARIABLE = "runtime_secrets_context";
}
