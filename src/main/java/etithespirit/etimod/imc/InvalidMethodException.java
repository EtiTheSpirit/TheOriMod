package etithespirit.etimod.imc;

/**
 * Made mostly because NoSuchMethodException is a standard Exception, requiring a throw to be declared.
 * @author Eti
 *
 */
public final class InvalidMethodException extends RuntimeException {
	
	private static final long serialVersionUID = 23890128390128903L;
	
	public InvalidMethodException(String methodName) {
		super(String.valueOf(methodName) + " is not a valid method.");
	}

}
