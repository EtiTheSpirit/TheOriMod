package etithespirit.etimod.util.collection;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A lazy but more performant alternative to {@link com.google.common.collect.ImmutableSet ImmutableSet} that simply wraps a
 * {@link List} in a manner that prevents modifying it.<para/>
 * This is based off of .NET's {@code System.Collections.Generic.IReadOnlyList<out T>}
 * @param <T> The type of element to contain in this list.
 */
public interface IReadOnlyList<T> extends Set<T>, List<T> {
											 // ^ is this illegal? i hope i'm not breaking some unspoken law lol
	
	/**
	 * Returns a new instance of {@link UnsupportedOperationException} with a preset message.
	 */
	static UnsupportedOperationException immutableError() throws UnsupportedOperationException {
		return new UnsupportedOperationException("This set is immutable.");
	}
	
	@Override
	default boolean addAll(int index, Collection<? extends T> c) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	T get(int index);
	
	@Override
	default T set(int index, T element) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default void add(int index, T element) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default T remove(int index) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	int indexOf(Object o);
	
	@Override
	int lastIndexOf(Object o);
	
	@Override
	ListIterator<T> listIterator();
	
	@Override
	ListIterator<T> listIterator(int index);
	
	@Override
	List<T> subList(int fromIndex, int toIndex);
	
	@Override
	int size();
	
	@Override
	boolean isEmpty();
	
	@Override
	boolean contains(Object o);
	
	@Override
	Iterator<T> iterator();
	
	@Override
	void forEach(Consumer<? super T> action);
	
	@Override
	Object[] toArray();
	
	@Override
	<T1> T1[] toArray(T1[] a);
	
	@Override
	default boolean add(T t) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default boolean remove(Object o) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default boolean containsAll(Collection<?> c) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default boolean addAll(Collection<? extends T> c) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default boolean retainAll(Collection<?> c) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default void replaceAll(UnaryOperator<T> operator) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default void sort(Comparator<? super T> c) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default boolean removeAll(Collection<?> c) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default boolean removeIf(Predicate<? super T> filter) throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	default void clear() throws UnsupportedOperationException {
		throw immutableError();
	}
	
	@Override
	Spliterator<T> spliterator();
	
	@Override
	Stream<T> stream();
	
	@Override
	Stream<T> parallelStream();
}
