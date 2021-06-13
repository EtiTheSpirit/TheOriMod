package etithespirit.etimod.util.collection;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * A "cached immutable set", which is a mutable set with a quick means of getting an immutable copy.<br/>
 * For performance, this immutable copy is cached.
 * @param <T> The type of element to store.
 */
public final class CachedImmutableSetProvider<T> implements Iterable<T> {
	
	/**
	 * Whether or not this {@link CachedImmutableSetProvider} allows duplicate elements. This also determines how methods
	 * like {@link #remove(T)} are handled. If this is true, {@link #remove(T)} will throw {@link IllegalArgumentException} if
	 * the given element is not contained, for instance.
	 */
	public final boolean strictElementTracking;
	
	
	private final ArrayList<T> elements;
	private ImmutableSet<T> immutableElements = null;
	
	/**
	 * Construct a new {@link CachedImmutableSetProvider} that allows duplicate elements / soft removals with the default initial capacity.
	 */
	public CachedImmutableSetProvider() {
		this(true);
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetProvider} that may or may not allow duplicate elements / soft removals, but with the default initial capacity.
	 * @param strictElementTracking If true, then attempting to add duplicate a element or remove an element that is not part of this will throw {@link IllegalArgumentException}
	 */
	public CachedImmutableSetProvider(boolean strictElementTracking) {
		elements = new ArrayList<>();
		this.strictElementTracking = strictElementTracking;
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetProvider} that allows duplicate elements / soft removals with the given initial capacity.
	 * @param initialCapacity The initial capcity of the internal {@link ArrayList}.
	 */
	public CachedImmutableSetProvider(int initialCapacity) {
		this(initialCapacity, true);
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetProvider} that may or may not allow duplicate elements / soft removals with the given initial capacity.
	 * @param initialCapacity The initial capcity of the internal {@link ArrayList}.
	 * @param strictElementTracking If true, then attempting to add duplicate a element or remove an element that is not part of this will throw {@link IllegalArgumentException}
	 */
	public CachedImmutableSetProvider(int initialCapacity, boolean strictElementTracking) {
		elements = new ArrayList<>(initialCapacity);
		this.strictElementTracking = strictElementTracking;
	}
	
	/**
	 * Adds the given element to this set.
	 * @param element The element to add.
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and the given element is already contained in this set.
	 */
	public void add(T element) throws IllegalArgumentException {
		if (strictElementTracking) {
			if (contains(element)) throw new IllegalArgumentException("This set already contains the given item.");
		}
		elements.add(element);
		immutableElements = null;
	}
	
	/**
	 * @param element The element to test for.
	 * @return Whether or not the given element is contained in this set.
	 */
	public boolean contains(T element) {
		// Why do stock implementations use Object instead of T?
		return elements.contains(element);
	}
	
	/**
	 * Removes the given element from this set.
	 * @param element The element to remove.
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and the given element is not contained in this set.
	 * @return True if the element was successfully removed, false if not (only possible if {@link #strictElementTracking} is false).
	 */
	public boolean remove(T element) throws IllegalArgumentException {
		if (strictElementTracking) {
			if (!contains(element)) throw new IllegalArgumentException("This set does not contain the given item.");
		}
		boolean retn = elements.remove(element);
		immutableElements = null;
		return retn;
	}
	
	/**
	 * Removes all elements from this set.
	 */
	public void clear() {
		elements.clear();
		immutableElements = null;
	}
	
	/**
	 * @return This set as an immutable set. The return value is cached until this set is modified, from which a new immutable set will be
	 * constructed from this set.
	 */
	public ImmutableSet<T> immutable() {
		if (immutableElements != null) {
			return immutableElements;
		}
		immutableElements = ImmutableSet.copyOf(elements);
		return immutableElements;
	}
	
	/**
	 * Returns false if {@link #immutable()} will rebuilt the cached immutable copy of this set (for instance, if
	 * {@link #add(T)} or {@link #remove(T)} are called, this will return true until {@link #immutable()} is called
	 * again, which will rebuild the cached value.)
	 * @return Whether or not the cached immutable set for the set in its current state has been built.
	 */
	public boolean immutableReady() {
		return immutableElements != null;
	}
	
	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		for (T element : elements) {
			action.accept(element);
		}
	}
	
	// TODO: Spliterator? I don't ever use those.
}
