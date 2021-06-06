package etithespirit.etimod.imc;

import error.IMCException;

public final class IMCStatusContainer {
	
	/**
	 * A successful IMC status.
	 */
	public static final IMCStatusContainer SUCCESS = new IMCStatusContainer(IMCRegistryError.SUCCESS);
	
	public final IMCRegistryError error;
	
	private final String alteredMessage;
	
	public final boolean isSuccess;
	
	public IMCStatusContainer(IMCRegistryError error, String...format) {
		this.error = error;
		Object[] asObjects = new Object[format.length];
		for (int i = 0; i < asObjects.length; i++) {
			asObjects[i] = format[i];
		}
		if (asObjects.length > 0) {
			alteredMessage = String.format(error.message, asObjects);
		} else {
			alteredMessage = error.message;
		}
		isSuccess = error.equals(IMCRegistryError.SUCCESS);
	}

	/**
	 * Throws an IMCException which will terminate forge mod loading and display an error.
	 */
	public void throwIMCException(String sender, String imcParam) {
		if (error.hasUserMessage) {
			String message = String.format(
				"%s\n\n----- INFO FOR DEVELOPERS: -----\nERROR: %s: %s\nPROVIDED ARGS: %s",
				String.format(error.userMessage, sender != null ? sender : "(unknown :c)"),
				error.name(),
				alteredMessage,
				imcParam
			);
			throw new IMCException(message);
		} else {
			String message = String.format(
				"§n§e%s§r§c sent malformed data! Report this to its creator.\n\n----- INFO FOR DEVELOPERS: -----\nERROR: %s: %s\nPROVIDED ARGS: %s",
				sender,
				error.name(),
				alteredMessage,
				imcParam
			);
			throw new IMCException(message);
		}
	}
	
}
