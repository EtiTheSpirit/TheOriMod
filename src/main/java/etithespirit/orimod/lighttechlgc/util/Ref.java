package etithespirit.orimod.lighttechlgc.util;

import javax.annotation.Nonnull;
import java.util.WeakHashMap;

/**
 * A reference to an object. This is used by {@link etithespirit.orimod.common.tile.light.AbstractLightEnergyHub Tech Assemblies} to effectively serve as a
 * pointer to an object for this Assembly.<br/>
 * <br/>
 * This exists because this "pointer" mindset can be used to swap the references of many objects at once without having to
 * actually iterate through those objects and update them, saving time at the cost of a very small amount of memory.<br/>
 * <br/>
 * In order to fully leverage the benefit of this class, a single instance of this class should be shared. Shared instances
 * will be returned via the {@link #of(T)} method.<br/>
 * <br/>
 * <strong>This has a number of key details that dictate its use:</strong>
 * <ol>
 *     <li>There should never be more than one reference to any given object. If there is, this class should not be used.</li>
 *     <li>This class should be used as a way to change the object that many objects point to via a single operation, exclusively in order to omit the need to iterate over the objects to update them.</li>
 * </ol>
 * @param <T> The type of object that this reference contains.
 */
@Deprecated(forRemoval = true)
public final class Ref<T> {
	
	/**
	 * A lookup from objects to a ref that has been created for that object.
	 */
	private static final WeakHashMap<Object, Ref<?>> REF_LOOKUP = new WeakHashMap<>(512, 0.2f);
	// n.b. use a low load factor because the goal is lookup speed over reallocation overhead.
	
	private T value;
	
	private Ref(T value) {
		this.value = value;
	}
	
	/**
	 * Sets the value stored within this reference.
	 * @param value The value to store.
	 */
	public void set(T value) {
		this.value = value;
	}
	
	/**
	 * Returns the stored value.
	 * @return The stored value within this reference.
	 */
	public T get() {
		return value;
	}
	
	/**
	 * Returns true if the internal value is not null.
	 * @return True if the internal value is not null, false if it is not.
	 */
	public boolean isPresent() {
		return value != null;
	}
	
	/**
	 * In some cases, a duplicate {@link Ref<T>} may be created unintentionally around a given object. This can result in
	 * a "garbage ref" being created that is desynchronized from others of its type. This method can be used by the implementor to mitigate
	 * the potentially catastrophic issues that can come from this. This method returns the {@link Ref<T>} that is believed to be the appropriate
	 * instance to point to this.<br/>
	 * If this ref stores null, then this will always be returned.
	 * @return The {@link Ref<T>} that should be responsible for pointing to this value, or this if this is the correct object.
	 */
	@SuppressWarnings("unchecked")
	public @Nonnull Ref<T> getOriginalStorage() {
		if (value == null) return this;
		return (Ref<T>)REF_LOOKUP.getOrDefault(value, this);
	}
	
	/**
	 * Create a new reference pointing to the given value, unless a reference to the given value already exists
	 * somewhere, from which the existing reference will be returned.
	 * @param value The value to point to.
	 * @param <T> The type of value to point to.
	 * @return A new ref, or an existing one if applicable.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Ref<T> of(T value) {
		return (Ref<T>)REF_LOOKUP.computeIfAbsent(value, Ref::new);
	}
	
}