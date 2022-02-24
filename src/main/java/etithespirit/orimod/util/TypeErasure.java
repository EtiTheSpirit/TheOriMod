package etithespirit.orimod.util;

import java.lang.reflect.Array;

public final class TypeErasure {
	
	/**
	 * A method that (hilariously) abuses Java's type erasure to allow crudely casting into any generic to make the compiler <a href="https://youtu.be/0sLwHisz8TU?t=54">happy and the engineers sad.</a>
	 * @param in
	 * @param <T>
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T eraseAndTreatAsGeneric(Object in) {
		return (T)in;
	}
	
	
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
