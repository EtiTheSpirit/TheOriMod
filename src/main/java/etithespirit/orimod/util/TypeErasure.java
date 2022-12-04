package etithespirit.orimod.util;

import java.lang.reflect.Array;

public final class TypeErasure {
	
	/**
	 * A method that (hilariously) abuses Java's type erasure to allow crudely casting into any generic type
	 * <a href="https://youtu.be/0sLwHisz8TU?t=54">to make the compiler happy and the engineers sad.</a>
	 * @param in The object to treat as a generic type.
	 * @param <T> The type of the generic.
	 * @return The exact same object, except the Java compiler thinks its an instance of T now lol.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T eraseAndTreatAsGeneric(Object in) {
		return (T)in;
	}
	
	
	/**
	 * Translates an array of Object[] into an array of T[]
	 * @param in The arbitrary array.
	 * @param tType The class to use when creating the array.
	 * @param <T> The type of the array.
	 * @return An array with a more specific type.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] translateArray(Object[] in, Class<T> tType) {
		try {
			T[] array = (T[]) Array.newInstance(tType, in.length);
			for (int index = 0; index < in.length; index++) {
				array[index] = (T)in[index];
			}
			return array;
		} catch (Exception exc) {
			throw new RuntimeException(exc); // TOP KEK
		}
	}
	
}
