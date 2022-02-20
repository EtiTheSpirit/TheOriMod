package etithespirit.exception;

/**
 * A lookup of error messages that the API may generate for any given reason.
 */
public final class ConstantErrorMessages {
	private ConstantErrorMessages() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/** This error message appears if a mod attempts to interact with an API member that must be used before the Forge loading cycle completes. */
	public static final String FORGE_LOADING_COMPLETED = "The forge mod loading cycle has completed, and it is now illegal to use this method. Only call this method during the forge loading event cycle.";
}