package etithespirit.orimod.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link NotNetworkReplicated @NotNetworkReplicated} annotation signifies that the attached element is <strong>not</strong> replicated over the network by some system.
 * This is not functional and exists for programmers only. It is nearly similar to its counterpart, {@link NetworkReplicated @NetworkReplicated}, with the exception
 * that this marks elements on a "must not" basis (not being replicated is default, so this signifies that it <em>shouldn't</em> be replicated).
 * It has a number of implications when used:<br/>
 * <pre>
 *  - When used on a field, the field explicitly <strong>should not</strong> be synchronized.
 *  - When used on a class, the class's members explicitly <strong>should not</strong> be synchronized.
 *  - When used on a method, the action <strong>must</strong> run on the caller's side only.
 * </pre>
 */
@Retention(RetentionPolicy.CLASS)
@Documented
@Inherited
@Target({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface NotNetworkReplicated { }
