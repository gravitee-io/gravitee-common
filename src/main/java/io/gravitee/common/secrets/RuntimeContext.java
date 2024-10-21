package io.gravitee.common.secrets;

/**
 * @author Benoit BORDIGONI (benoit.bordigoni at graviteesource.com)
 * @author GraviteeSource Team
 */
public record RuntimeContext(boolean allowed, ValueKind allowKind, String field) {
    public static final String EL_VARIABLE = "runtime_secrets_context";
}
