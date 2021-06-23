package etithespirit.etimod.util.collection;

import java.util.Collections;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * A "cached immutable set", which is a mutable set with a quick means of getting an immutable copy.<br/>
 * For performance, this immutable copy is cached.
 * @param <T> The type of element to store.
 */
@SuppressWarnings("unused")
public final class CachedImmutableSetWrapper<T> implements Set<T>, List<T> {
	
	/**
	 * Whether or not this {@link CachedImmutableSetWrapper} allows duplicate elements. This also determines how methods
	 * like {@link #remove(T)} are handled. If this is true, {@link #remove(T)} will throw {@link IllegalArgumentException} if
	 * the given element is not contained, for instance.
	 */
	public final boolean strictElementTracking;
	
	private final ArrayList<T> elements;
	private final List<T> immutableElements;
	// This should improve speed (as opposed to using ImmutableSet<T>)
	// Mainly because I don't need to copy every time. What a waste.
	
	private static IllegalArgumentException strictFailure(boolean adding, boolean many) {
		if (adding) {
			if (many) {
				return new IllegalArgumentException("This set already contains at least one of the given items.");
			} else {
				return new IllegalArgumentException("This set already contains the given item.");
			}
		} else {
			if (many) {
				return new IllegalArgumentException("This set does not contain all of the given items.");
			} else {
				return new IllegalArgumentException("This set does not contain the given item.");
			}
		}
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetWrapper} that allows duplicate elements / soft removals with the default initial capacity.
	 */
	public CachedImmutableSetWrapper() {
		this(0, false);
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetWrapper} that may or may not allow duplicate elements / soft removals, but with the default initial capacity.
	 * @param strictElementTracking If true, then attempting to add duplicate a element or remove an element that is not part of this will throw {@link IllegalArgumentException}
	 */
	public CachedImmutableSetWrapper(boolean strictElementTracking) {
		this(0, strictElementTracking);
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetWrapper} that allows duplicate elements / soft removals with the given initial capacity.
	 * @param initialCapacity The initial capcity of the internal {@link ArrayList}.
	 */
	public CachedImmutableSetWrapper(int initialCapacity) {
		this(initialCapacity, false);
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetWrapper} that may or may not allow duplicate elements / soft removals with the given initial capacity.
	 * @param initialCapacity The initial capcity of the internal {@link ArrayList}.
	 * @param strictElementTracking If true, then attempting to add duplicate a element or remove an element that is not part of this will throw {@link IllegalArgumentException}
	 */
	public CachedImmutableSetWrapper(int initialCapacity, boolean strictElementTracking) {
		elements = new ArrayList<>(initialCapacity);
		immutableElements = Collections.unmodifiableList(elements);
		this.strictElementTracking = strictElementTracking;
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetWrapper} containing the given elements.
	 * @param enumerable The {@link Iterable} that will provide the elements.
	 */
	public CachedImmutableSetWrapper(Iterable<T> enumerable) {
		this(enumerable, false);
	}
	
	/**
	 * Construct a new {@link CachedImmutableSetWrapper} containing the given elements.
	 * @param enumerable The {@link Iterable} that will provide the elements.
	 * @param strictElementTracking If true, then attempting to add duplicate a element or remove an element that is not part of this will throw {@link IllegalArgumentException}
	 */
	public CachedImmutableSetWrapper(Iterable<T> enumerable, boolean strictElementTracking) {
		elements = new ArrayList<>();
		immutableElements = Collections.unmodifiableList(elements);
		this.strictElementTracking = strictElementTracking;
		
		for (T item : enumerable) {
			elements.add(item);
		}
	}
	
	/**
	 * @return Whether or not this {@link CachedImmutableSetWrapper}
	 */
	public boolean hasStrictElementTracking() {
		return strictElementTracking;
	}
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and the given element is already contained in this set.
	 */
	@Override
	public boolean add(T element) throws IllegalArgumentException {
		if (strictElementTracking && contains(element)) {
			throw strictFailure(true, false);
		}
		return elements.add(element);
	}
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and the given element is already contained in this set.
	 */
	@Override
	public void add(int index, T element) {
		if (strictElementTracking && contains(element)) {
			throw strictFailure(true, false);
		}
		elements.add(index, element);
	}
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and the given element is already contained in this set.
	 */
	@Override
	public T set(int index, T element) {
		if (strictElementTracking && contains(element)) {
			throw strictFailure(true, false);
		}
		return elements.set(index, element);
	}
	
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and any given element is already contained in this set.
	 */
	@Override
	public boolean addAll(Collection<? extends T> c) {
		if (strictElementTracking && containsAny(c)) {
			throw strictFailure(true, true);
		}
		return elements.addAll(c);
	}
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and any given element is already contained in this set.
	 */
	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		if (strictElementTracking && containsAny(c)) {
			throw strictFailure(true, true);
		}
		return elements.addAll(index, c);
	}
	
	
	
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and any given element is not contained in this set.
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		if (strictElementTracking && !containsAll(c)) {
			throw strictFailure(false, true);
		}
		return elements.retainAll(c);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}
	
	/**
	 * Returns true if this contains at least one of the items in {@code c}
	 * @param c The collection of elements to test.
	 * @return True if this contains at least one of the items in {@code c}, false if not.
	 */
	public boolean containsAny(Collection<? extends T> c) {
		for (T obj : c) {
			if (contains(c)) return true;
		}
		return false;
	}
	
	@Override
	public boolean contains(Object element) {
		return elements.contains(element);
	}
	
	@Override
	public T get(int index) {
		return elements.get(index);
	}
	
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and any given element is not contained in this set.
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		if (strictElementTracking && !containsAll(c)) {
			throw strictFailure(false, true);
		}
		return elements.removeAll(c);
	}
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and the given element is not contained in this set.
	 */
	@Override
	public boolean remove(Object element) throws IllegalArgumentException {
		if (strictElementTracking && !contains(element)) {
			throw strictFailure(false, false);
		}
		return elements.remove(element);
	}
	
	/**
	 * @throws IllegalArgumentException If {@link #strictElementTracking} is true, and the given element is not contained in this set.
	 */
	@Override
	public T remove(int index) {
		if (strictElementTracking && (index >= elements.size() || elements.get(index) == null)) {
			throw strictFailure(false, false);
		}
		return elements.remove(index);
	}
	
	@Override
	public void clear() {
		elements.clear();
	}
	
	@Override
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<T> listIterator() {
		return elements.listIterator();
	}
	
	@Override
	public ListIterator<T> listIterator(int index) {
		return elements.listIterator(index);
	}
	
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}
	
	/**
	 * @return This as a read-only list.
	 */
	public List<T> asReadOnly() {
		return immutableElements;
	}
	
	@Override
	public int size() {
		return elements.size();
	}
	
	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}
	
	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}
	
	@Override
	public Object[] toArray() {
		return elements.toArray();
	}
	
	@Override
	public <T1> T1[] toArray(T1[] a) {
		return elements.toArray(a);
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		for (T element : elements) {
			action.accept(element);
		}
	}
	
	@Override
	public Spliterator<T> spliterator() { return elements.spliterator(); }
}
