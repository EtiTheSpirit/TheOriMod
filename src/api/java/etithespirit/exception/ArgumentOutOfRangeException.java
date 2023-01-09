package etithespirit.exception;

/**
 * The exception that is thrown when an argument, typically a numeric one, is passed to a method that deems it to be outside the range of acceptable values. It is a more precise alternative to {@link IllegalArgumentException}<br/>
 * <br/>
 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentoutofrangeexception?view=net-7.0">System.ArgumentOutOfRangeException</a>
 * @author Microsoft (for the .NET implementation)
 * @author Eti (for the Java implementation)
 */
public class ArgumentOutOfRangeException extends IllegalArgumentException {
	
	private static final String DEFAULT_BASE_MESSAGE = "Specified argument was out of range of valid values.";
	private static final String DEFAULT_MESSAGE_FMT = "%s\nParameter name: %s";
	private static final String DEFAULT_MSG_WITH_ACTUAL_VALUE = "%s\nParameter name: %s\nActual value was %s.";
	
	public ArgumentOutOfRangeException(String parameterName) {
		super(String.format(DEFAULT_MESSAGE_FMT, DEFAULT_BASE_MESSAGE, parameterName));
	}
	
	public ArgumentOutOfRangeException(String parameterName, Object givenValue, String message) {
		super(String.format(DEFAULT_MSG_WITH_ACTUAL_VALUE, message, parameterName, givenValue));
	}
	
	public ArgumentOutOfRangeException(String parameterName, String message) {
		super(String.format(DEFAULT_MESSAGE_FMT, message, parameterName));
	}
	
	public ArgumentOutOfRangeException(String parameterName, Throwable cause) {
		super(String.format(DEFAULT_MESSAGE_FMT, DEFAULT_BASE_MESSAGE, parameterName), cause);
	}
	
}
