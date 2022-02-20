package etithespirit.orimod.util;

public final class TypeErasure {
	
	/**
	 * A method that (hilariously) abuses Java's type erasure to allow crudely casting into any generic to make the compiler happy and the engineers sad.
	 * @param in
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T eraseAndTreatAsGeneric(Object in) {
		return (T)in;
	}
	
	/**
	 * Abuses type erasure to cast the given object array into a typed array.
	 * @param in
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] eraseAndTreatAsGeneric(Object[] in) { return (T[])in; }
	
}
