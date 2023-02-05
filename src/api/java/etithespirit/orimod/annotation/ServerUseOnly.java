package etithespirit.orimod.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link ServerUseOnly @ServerUseOnly} annotation signifies that the associated item should only be called from a
 * dedicated or integrated server. Using this on the client will not work and/or cause an exception to occur.
 */
@Retention (RetentionPolicy.CLASS)
@Documented
@Inherited
@Target ({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface ServerUseOnly { }
