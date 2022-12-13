package etithespirit.orimod.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link ClientUseOnly @ClientUseOnly} annotation signifies that the associated item relies on classes that only
 * exist on the client, and thus using it on the server will cause a crash, an exception, or have unexpected behavior.
 */
@Retention (RetentionPolicy.CLASS)
@Documented
@Inherited
@Target ({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface ClientUseOnly { }
