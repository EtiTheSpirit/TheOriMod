package etithespirit.exception;

/**
 * A mimicry of .NET's <a href="https://docs.microsoft.com/en-us/dotnet/api/system.notimplementedexception?view=net-5.0">NotImplementedException</a> which is used for methods that have not been developed.
 *
 * @author Microsoft (for the .NET implementation)
 * @author Eti (for the Java implementation)
 */
public class NotImplementedException extends UnsupportedOperationException {
	
	/***/
	public NotImplementedException() {
		this("The method or operation is not implemented.");
	}
	
	/***/
	public NotImplementedException(String message) {
		super(message);
	}
	
}
