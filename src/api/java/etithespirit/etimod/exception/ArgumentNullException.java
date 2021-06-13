package etithespirit.etimod.exception;

/**
 * The exception that is thrown when a null reference is passed to a method that does not accept it as a valid argument. It is a more precise alternative to {@link IllegalArgumentException}<br/>
 * <br/>
 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0">https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0</a>
 * @author Microsoft (for the .NET implementation)
 * @author Eti (for the Java implementation)
 *
 */
public class ArgumentNullException extends NullPointerException {
	private static final long serialVersionUID = 148904892384923213L;
	
	private final String detailMessage;
	
	private static final String GENERIC_THROW = "Exception of type '" + (ArgumentNullException.class.getCanonicalName()) + "' was thrown.";
	
	private static final String DEFAULT_MESSAGE = "Value cannot be null.";
	
	private static final String PARAM_NAME_COMPONENT = "Parameter name: %s";
	
	/**
	 * Alias method to ensure that a parameter is non-null.
	 * @param obj The value of the parameter.
	 * @param parameterName The name of the parameter.
	 * @throws ArgumentNullException If the value is equal to null.
	 */
	public static void assertNotNull(Object obj, String parameterName) throws ArgumentNullException {
		if (obj == null) {
			throw new ArgumentNullException(parameterName);
		}
	}
	
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
