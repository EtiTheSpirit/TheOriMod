package etithespirit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Signifies that this element is intended for use in a dedicated server build. Has no function.
 *
 * @author Eti
 */
@Retention (RetentionPolicy.SOURCE)
@Target ({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Documented
public @interface DedicatedServerOnly { }
