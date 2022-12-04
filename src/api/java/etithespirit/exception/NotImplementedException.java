package etithespirit.exception;

/**
 * A mimicry of .NET's <a href="https://docs.microsoft.com/en-us/dotnet/api/system.notimplementedexception?view=net-5.0">NotImplementedException</a> which is used for (usually) abstract methods that do not have an implementation, or code that is otherwise incomplete.
 *
 * @author Microsoft (for the .NET implementation)
 * @author Eti (for the Java implementation)
 */
public class NotImplementedException extends UnsupportedOperationException {
	
	/**
	 * Create a new instance of this exception using the default message, {@code "This method or operation is not implemented."}
	 */
	public NotImplementedException() {
		this("This method or operation is not implemented.");
	}
	
	/**
	 * Create a new instance of this exception, overriding the display message.
	 * @param message The message to use.
	 */
	public NotImplementedException(String message) {
		super(message);
	}
	
}
