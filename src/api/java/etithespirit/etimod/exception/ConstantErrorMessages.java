package etithespirit.etimod.exception;

public final class ConstantErrorMessages {
	private ConstantErrorMessages() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	public static final String FORGE_LOADING_COMPLETED = "The forge mod loading cycle has completed, and it is now illegal to use this method. Only call this method during the forge loading event cycle.";
}
