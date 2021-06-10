package etithespirit.etimod.exception;

/**
 * The exception that is thrown when a null reference is passed to a method that does not accept it as a valid argument. It is a more precise alternative to {@link IllegalArgumentException}<br/>
 * <br/>
 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0">https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0</a>
 * @author Microsoft (for the .NET implementation)
 * @author Eti (for the Java implementation)
 *
 */
public class ArgumentNullException extends IllegalArgumentException {
	private static final long serialVersionUID = 148904892384923213L;
	
	private final String detailMessage;
	
	private static final String GENERIC_THROW = "Exception of type '" + (ArgumentNullException.class.getCanonicalName()) + "' was thrown.";
	
	private static final String DEFAULT_MESSAGE = "Value cannot be null.";
	
	private static final String PARAM_NAME_COMPONENT = "Parameter name: %s";
	
	/**
	 * The exception that is thrown when a null reference is passed to a method that does not accept it as a valid argument.<br/>
	 * <br/>
	 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0">https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0</a>
	 * @author Microsoft
	 * @author Eti (for the Java implementation)
	 *
	 */
	public ArgumentNullException(String parameterName) {
		this(parameterName, DEFAULT_MESSAGE);
	}
	
	/**
	 * The exception that is thrown when a null reference is passed to a method that does not accept it as a valid argument.<br/>
	 * <br/>
	 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0">https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0</a>
	 * @author Microsoft
	 * @author Eti (for the Java implementation)
	 *
	 */
	public ArgumentNullException(String parameterName, String message) {
		if (parameterName == null) {
			if (message == null) {
				detailMessage = GENERIC_THROW;
			} else {
				detailMessage = message;
			}
		} else {
			if (message == null) {
				detailMessage = String.format("%s\r\n%s", GENERIC_THROW, String.format(PARAM_NAME_COMPONENT, parameterName));
			} else {
				detailMessage = String.format("%s\r\n%s", message, String.format(PARAM_NAME_COMPONENT, parameterName));
			}
		}
	}
	
	@Override
	public String getMessage() {
		return detailMessage;
	}
	
}
