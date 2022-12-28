package etithespirit.exception;


import javax.annotation.Nullable;
import java.io.Serial;

/**
 * The exception that is thrown when a null reference is passed to a method that does not accept it as a valid argument. It is a more precise alternative to {@link IllegalArgumentException}<br/>
 * <br/>
 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-7.0">System.ArgumentNullException</a>
 * @author Microsoft (for the .NET implementation)
 * @author Eti (for the Java implementation)
 *
 */
public class ArgumentNullException extends IllegalArgumentException {
	
	@Serial
	private static final long serialVersionUID = 148904892384923213L;
	
	private final String detailMessage;
	
	private static final String GENERIC_THROW = "Exception of type '" + (ArgumentNullException.class.getCanonicalName()) + "' was thrown.";
	
	private static final String DEFAULT_MESSAGE = "Value cannot be null.";
	
	private static final String PARAM_NAME_COMPONENT = "Parameter name: %s";
	
	/**
	 * Raises an {@link ArgumentNullException} if the given object <code>obj</code> is null.
	 * @param obj The value of the parameter.
	 * @param parameterName The name of the parameter, for use in the exception message.
	 * @throws ArgumentNullException If the value of obj is null by reference.
	 */
	public static void throwIfNull(@Nullable Object obj, String parameterName) throws ArgumentNullException {
		if (obj == null) {
			throw new ArgumentNullException(parameterName);
		}
	}
	
	/**
	 * The exception that is thrown when a null reference is passed to a method that does not accept it as a valid argument.<br/>
	 * <br/>
	 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0">https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0</a>
	 * @param parameterName The parameter that was null when it should have not been.
	 *
	 */
	public ArgumentNullException(String parameterName) {
		this(parameterName, DEFAULT_MESSAGE);
	}
	
	/**
	 * The exception that is thrown when a null reference is passed to a method that does not accept it as a valid argument.<br/>
	 * <br/>
	 * Derived from .NET (mscorlib.dll, System.Runtime.dll) // See <a href="https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0">https://docs.microsoft.com/en-us/dotnet/api/system.argumentnullexception?view=net-5.0</a>
	 * @param parameterName The parameter that was null when it should have not been.
	 * @param message The message to add onto the null error.
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
