package etithespirit.etimod.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * Provides utilities associated with reflection in cases where using an access transformer is not ideal.
 * @author Eti
 *
 */
public final class ReflectionUtils {
	
	private static final Set<Field> ModifiedFields = new HashSet<Field>();
	
	/**
	 * Slotta field.
	 */
	private static Field modifiersField;
	
	/**
	 * Adds the given modifiers to the given field (such as its final). To remove modifiers, use {@code RemoveFieldModifiers}
	 * @param ofField
	 * @param modifiers
	 */
	public static void addFieldModifiers(Field ofField, int modifiers) throws SecurityException, IllegalAccessException {
		modifiersField.setInt(ofField, ofField.getModifiers() & modifiers);
	}
	
	/**
	 * Removes the given modifiers from the given field (such as final). To add modifiers, use {@code AddFieldModifiers}
	 * @param ofField
	 * @param modifiers
	 */
	public static void removeFieldModifiers(Field ofField, int modifiers) throws SecurityException, IllegalAccessException {
		modifiersField.setInt(ofField, ofField.getModifiers() & ~modifiers);
	}
	
	/**
	 * Modifies the given final field on {@code ofObject} so that it equals {@code newValue}. 
	 * @param ofObject The object to modify, or null if the field is static.
	 * @param field The field to modify.
	 * @param newValue The value to set the field to.
	 */
	public static void modifyFinalField(Object ofObject, Field field, Object newValue) {
		try {
			if (!ModifiedFields.contains(field)) {
				field.setAccessible(true);
				removeFieldModifiers(field, Modifier.FINAL);
				ModifiedFields.add(field);
			}
			field.set(ofObject, newValue);
		} catch (Exception exc) { 
			System.err.println(exc.toString());
		}
	}
	
	static {
		try {
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
		} catch (Exception exc) { 
			modifiersField = null;
		}
	}
	
}
