package etithespirit.etimod.util.collection;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Internal implementation of {@link IReadOnlyList}.
 * @param <T>
 */
class ReadOnlyList<T> implements IReadOnlyList<T> {
	
	private final List<T> internal;
	
	public ReadOnlyList(List<T> base) {
		internal = base;
	}
	
	@Override
	public int size() {
		return internal.size();
	}
	
	@Override
	public boolean isEmpty() {
		return internal.isEmpty();
	}
	
	@Override
	public boolean contains(Object o) {
		return internal.contains(o);
	}
	
	@Override
	public Object[] toArray() {
		return internal.toArray();
	}
	
	@Override
	public <T1> T1[] toArray(T1[] a) {
		return internal.toArray(a);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return internal.containsAll(c);
	}
	
	@Override
	public T get(int index) {
		return internal.get(index);
	}
	
	@Override
	public int indexOf(Object o) {
		return internal.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(Object o) {
		return internal.lastIndexOf(o);
	}
	
	@Override
	public ListIterator<T> listIterator() {
		return internal.listIterator();
	}
	
	@Override
	public ListIterator<T> listIterator(int index) {
		return internal.listIterator(index);
	}
	
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new ReadOnlyList<T>(internal.subList(fromIndex, toIndex));
	}
	
	@Override
	public Iterator<T> iterator() {
		return internal.iterator();
	}
	
	@Override
	public void forEach(Consumer<? super T> action) {
		for (T element : internal) {
			action.accept(element);
		}
	}
	
	@Override
	public Spliterator<T> spliterator() {
		return internal.spliterator();
	}
	
	@Override
	public Stream<T> stream() {
		return internal.stream();
	}
	
	@Override
	public Stream<T> parallelStream() {
		return internal.parallelStream();
	}
}