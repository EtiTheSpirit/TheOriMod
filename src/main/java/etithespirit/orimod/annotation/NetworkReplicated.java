package etithespirit.orimod.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link NetworkReplicated} annotation signifies that the attached element is automatically replicated over the network by some system.<br/>
 * <strong>This does NOT imply it is synchronized immediately!</strong>
 * This is not functional and exists for programmers only. It has a number of implications when used:<br/>
 * <pre>
 *  - When used on a field, the field's value will be replicated and attempt to synchronize between the server and all clients.
 *  - When used on a class, the entirety of the class's members (inherited included) will be synchronized between the server and all clients.
 *  - When used on a method, the action performed when the method is called on the server will occur on all clients as well.
 * </pre>
 * Note that all cases listed above assume the server is responsible for managing the value. Clientside changes will not be replicated unless explicitly stated otherwise.
 */
@Retention (RetentionPolicy.CLASS)
@Documented
@Inherited
@Target ({ElementType.FIELD, ElementType.TYPE, ElementType.METHOD})
public @interface NetworkReplicated {}
