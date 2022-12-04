package etithespirit.orimod.util.collection;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

public class WeakList<E> implements List<E> {
	
	private final List<EquatableWeakReference<E>> internalList = new ArrayList<>();
	
	private int reservations = 0;
	
	private void clearGarbage() {
		internalList.removeIf(ref -> ref.get() == null);
	}
	
	private void prepareForOperation(boolean isStarting) {
		reservations += isStarting ? 1 : -1;
		if (reservations < 0) throw new IllegalStateException("Unexpected call to prepareForOperation - there was no previous call to this method with a parameter of true!");
		for (EquatableWeakReference<E> ref : internalList) {
			if (ref.isStrong() != isStarting) {
				if (isStarting) {
					ref.tryMakeStrong();
				} else if (reservations == 0) {
					ref.makeWeak();
				}
			}
		}
	}
	
	/**
	 * Returns the number of elements in this list.  If this list contains
	 * more than {@code Integer.MAX_VALUE} elements, returns
	 * {@code Integer.MAX_VALUE}.
	 *
	 * @return the number of elements in this list
	 */
	@Override
	public int size() {
		clearGarbage();
		return internalList.size();
	}
	
	/**
	 * Returns {@code true} if this list contains no elements.
	 *
	 * @return {@code true} if this list contains no elements
	 */
	@Override
	public boolean isEmpty() {
		clearGarbage();
		return internalList.isEmpty();
	}
	
	/**
	 * Returns {@code true} if this list contains the specified element.
	 * More formally, returns {@code true} if and only if this list contains
	 * at least one element {@code e} such that
	 * {@code Objects.equals(o, e)}.
	 *
	 * @param o element whose presence in this list is to be tested
	 * @return {@code true} if this list contains the specified element
	 * @throws ClassCastException   if the type of the specified element
	 *                              is incompatible with this list
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this
	 *                              list does not permit null elements
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public boolean contains(Object o) {
		clearGarbage();
		return internalList.stream().anyMatch(obj -> obj.refersTo((E)o));
	}
	
	/**
	 * Returns an iterator over the elements in this list in proper sequence.
	 *
	 * @return an iterator over the elements in this list in proper sequence
	 */
	@NotNull
	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			private int currentPosition = 0;
			
			@Override
			public boolean hasNext() {
				return nextNonDisposed() != null;
			}
			
			@Override
			public E next() {
				E next = nextNonDisposed();
				currentPosition++;
				return next;
			}
			
			private E nextNonDisposed() {
				while (true) {
					try {
						WeakReference<E> element = internalList.get(currentPosition);
						if (element.get() != null) return element.get();
						currentPosition++;
					} catch (IndexOutOfBoundsException exc) {
						return null;
					}
				}
			}
		};
	}
	
	/**
	 * Returns an array containing all of the elements in this list in proper
	 * sequence (from first to last element).
	 *
	 * <p>The returned array will be "safe" in that no references to it are
	 * maintained by this list.  (In other words, this method must
	 * allocate a new array even if this list is backed by an array).
	 * The caller is thus free to modify the returned array.
	 *
	 * <p>This method acts as bridge between array-based and collection-based
	 * APIs.
	 *
	 * @return an array containing all of the elements in this list in proper
	 * sequence
	 * @see Arrays#asList(Object[])
	 */
	@NotNull
	@Override
	public Object[] toArray() {
		ArrayList<E> list = new ArrayList<>(this);
		return list.toArray();
	}
	
	/**
	 * Returns an array containing all of the elements in this list in
	 * proper sequence (from first to last element); the runtime type of
	 * the returned array is that of the specified array.  If the list fits
	 * in the specified array, it is returned therein.  Otherwise, a new
	 * array is allocated with the runtime type of the specified array and
	 * the size of this list.
	 *
	 * <p>If the list fits in the specified array with room to spare (i.e.,
	 * the array has more elements than the list), the element in the array
	 * immediately following the end of the list is set to {@code null}.
	 * (This is useful in determining the length of the list <i>only</i> if
	 * the caller knows that the list does not contain any null elements.)
	 *
	 * <p>Like the {@link #toArray()} method, this method acts as bridge between
	 * array-based and collection-based APIs.  Further, this method allows
	 * precise control over the runtime type of the output array, and may,
	 * under certain circumstances, be used to save allocation costs.
	 *
	 * <p>Suppose {@code x} is a list known to contain only strings.
	 * The following code can be used to dump the list into a newly
	 * allocated array of {@code String}:
	 *
	 * <pre>{@code
	 *     String[] y = x.toArray(new String[0]);
	 * }</pre>
	 * <p>
	 * Note that {@code toArray(new Object[0])} is identical in function to
	 * {@code toArray()}.
	 *
	 * @param a the array into which the elements of this list are to
	 *          be stored, if it is big enough; otherwise, a new array of the
	 *          same runtime type is allocated for this purpose.
	 * @return an array containing the elements of this list
	 * @throws ArrayStoreException  if the runtime type of the specified array
	 *                              is not a supertype of the runtime type of every element in
	 *                              this list
	 * @throws NullPointerException if the specified array is null
	 */
	@NotNull
	@Override
	public <T> T[] toArray(@NotNull T[] a) {
		ArrayList<E> list = new ArrayList<>(this);
		return list.toArray(a);
	}
	
	/**
	 * Appends the specified element to the end of this list (optional
	 * operation).
	 *
	 * <p>Lists that support this operation may place limitations on what
	 * elements may be added to this list.  In particular, some
	 * lists will refuse to add null elements, and others will impose
	 * restrictions on the type of elements that may be added.  List
	 * classes should clearly specify in their documentation any restrictions
	 * on what elements may be added.
	 *
	 * @param e element to be appended to this list
	 * @return {@code true} (as specified by {@link Collection#add})
	 * @throws UnsupportedOperationException if the {@code add} operation
	 *                                       is not supported by this list
	 * @throws ClassCastException            if the class of the specified element
	 *                                       prevents it from being added to this list
	 * @throws NullPointerException          if the specified element is null and this
	 *                                       list does not permit null elements
	 * @throws IllegalArgumentException      if some property of this element
	 *                                       prevents it from being added to this list
	 */
	@Override
	public boolean add(E e) {
		if (e == null) throw new NullPointerException("Elements of a WeakList cannot be null.");
		return add(new EquatableWeakReference<E>(e));
	}
	
	public boolean add(WeakReference<E> e) {
		if (e == null) throw new NullPointerException("Elements of a WeakList cannot be null");
		return add(EquatableWeakReference.from(e));
	}
	
	/**
	 * Adds the given element to the end of this list.
	 * @param e The element, already boxed in a weak reference.
	 * @return {@code true} (as specified by {@link Collection#add})
	 */
	public boolean add(EquatableWeakReference<E> e) {
		if (e == null) throw new NullPointerException("Elements of a WeakList cannot be null.");
		return internalList.add(e);
	}
	
	/**
	 * Removes the first occurrence of the specified element from this list,
	 * if it is present (optional operation).  If this list does not contain
	 * the element, it is unchanged.  More formally, removes the element with
	 * the lowest index {@code i} such that
	 * {@code Objects.equals(o, get(i))}
	 * (if such an element exists).  Returns {@code true} if this list
	 * contained the specified element (or equivalently, if this list changed
	 * as a result of the call).
	 *
	 * @param o element to be removed from this list, if present
	 * @return {@code true} if this list contained the specified element
	 * @throws ClassCastException            if the type of the specified element
	 *                                       is incompatible with this list
	 *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if the specified element is null and this
	 *                                       list does not permit null elements
	 *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws UnsupportedOperationException if the {@code remove} operation
	 *                                       is not supported by this list
	 */
	@Override
	public boolean remove(Object o) {
		if (o instanceof EquatableWeakReference ref) {
			return internalList.remove(ref);
		} else if (o instanceof WeakReference weak) {
			WeakReference toRemove = null;
			for (WeakReference<E> obj : internalList) {
				if (weak.refersTo(obj.get())) {
					toRemove = obj;
					break;
				}
			}
			if (toRemove != null) {
				return internalList.remove(toRemove);
			}
			return false;
		} else {
			WeakReference toRemove = null;
			for (WeakReference<E> obj : internalList) {
				if (obj.refersTo((E)o)) {
					toRemove = obj;
					break;
				}
			}
			if (toRemove != null) {
				return internalList.remove(toRemove);
			}
			return false;
		}
	}
	
	/**
	 * Returns {@code true} if this list contains all of the elements of the
	 * specified collection.
	 *
	 * @param c collection to be checked for containment in this list
	 * @return {@code true} if this list contains all of the elements of the
	 * specified collection
	 * @throws ClassCastException   if the types of one or more elements
	 *                              in the specified collection are incompatible with this
	 *                              list
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified collection contains one
	 *                              or more null elements and this list does not permit null
	 *                              elements
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>),
	 *                              or if the specified collection is null
	 * @see #contains(Object)
	 */
	@Override
	public boolean containsAll(@NotNull Collection<?> c) {
		return false;
	}
	
	/**
	 * Appends all of the elements in the specified collection to the end of
	 * this list, in the order that they are returned by the specified
	 * collection's iterator (optional operation).  The behavior of this
	 * operation is undefined if the specified collection is modified while
	 * the operation is in progress.  (Note that this will occur if the
	 * specified collection is this list, and it's nonempty.)
	 *
	 * @param c collection containing elements to be added to this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the {@code addAll} operation
	 *                                       is not supported by this list
	 * @throws ClassCastException            if the class of an element of the specified
	 *                                       collection prevents it from being added to this list
	 * @throws NullPointerException          if the specified collection contains one
	 *                                       or more null elements and this list does not permit null
	 *                                       elements, or if the specified collection is null
	 * @throws IllegalArgumentException      if some property of an element of the
	 *                                       specified collection prevents it from being added to this list
	 * @see #add(Object)
	 */
	@Override
	public boolean addAll(@NotNull Collection<? extends E> c) {
		return false;
	}
	
	/**
	 * Inserts all of the elements in the specified collection into this
	 * list at the specified position (optional operation).  Shifts the
	 * element currently at that position (if any) and any subsequent
	 * elements to the right (increases their indices).  The new elements
	 * will appear in this list in the order that they are returned by the
	 * specified collection's iterator.  The behavior of this operation is
	 * undefined if the specified collection is modified while the
	 * operation is in progress.  (Note that this will occur if the specified
	 * collection is this list, and it's nonempty.)
	 *
	 * @param index index at which to insert the first element from the
	 *              specified collection
	 * @param c     collection containing elements to be added to this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the {@code addAll} operation
	 *                                       is not supported by this list
	 * @throws ClassCastException            if the class of an element of the specified
	 *                                       collection prevents it from being added to this list
	 * @throws NullPointerException          if the specified collection contains one
	 *                                       or more null elements and this list does not permit null
	 *                                       elements, or if the specified collection is null
	 * @throws IllegalArgumentException      if some property of an element of the
	 *                                       specified collection prevents it from being added to this list
	 * @throws IndexOutOfBoundsException     if the index is out of range
	 *                                       ({@code index < 0 || index > size()})
	 */
	@Override
	public boolean addAll(int index, @NotNull Collection<? extends E> c) {
		return false;
	}
	
	/**
	 * Removes from this list all of its elements that are contained in the
	 * specified collection (optional operation).
	 *
	 * @param c collection containing elements to be removed from this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the {@code removeAll} operation
	 *                                       is not supported by this list
	 * @throws ClassCastException            if the class of an element of this list
	 *                                       is incompatible with the specified collection
	 *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if this list contains a null element and the
	 *                                       specified collection does not permit null elements
	 *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
	 *                                       or if the specified collection is null
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override
	public boolean removeAll(@NotNull Collection<?> c) {
		return false;
	}
	
	/**
	 * Retains only the elements in this list that are contained in the
	 * specified collection (optional operation).  In other words, removes
	 * from this list all of its elements that are not contained in the
	 * specified collection.
	 *
	 * @param c collection containing elements to be retained in this list
	 * @return {@code true} if this list changed as a result of the call
	 * @throws UnsupportedOperationException if the {@code retainAll} operation
	 *                                       is not supported by this list
	 * @throws ClassCastException            if the class of an element of this list
	 *                                       is incompatible with the specified collection
	 *                                       (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException          if this list contains a null element and the
	 *                                       specified collection does not permit null elements
	 *                                       (<a href="Collection.html#optional-restrictions">optional</a>),
	 *                                       or if the specified collection is null
	 * @see #remove(Object)
	 * @see #contains(Object)
	 */
	@Override
	public boolean retainAll(@NotNull Collection<?> c) {
		return false;
	}
	
	/**
	 * Removes all of the elements from this list (optional operation).
	 * The list will be empty after this call returns.
	 *
	 * @throws UnsupportedOperationException if the {@code clear} operation
	 *                                       is not supported by this list
	 */
	@Override
	public void clear() {
	
	}
	
	/**
	 * Returns the element at the specified position in this list.
	 *
	 * @param index index of the element to return
	 * @return the element at the specified position in this list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   ({@code index < 0 || index >= size()})
	 */
	@Override
	public E get(int index) {
		return null;
	}
	
	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element (optional operation).
	 *
	 * @param index   index of the element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 * @throws UnsupportedOperationException if the {@code set} operation
	 *                                       is not supported by this list
	 * @throws ClassCastException            if the class of the specified element
	 *                                       prevents it from being added to this list
	 * @throws NullPointerException          if the specified element is null and
	 *                                       this list does not permit null elements
	 * @throws IllegalArgumentException      if some property of the specified
	 *                                       element prevents it from being added to this list
	 * @throws IndexOutOfBoundsException     if the index is out of range
	 *                                       ({@code index < 0 || index >= size()})
	 */
	@Override
	public E set(int index, E element) {
		return null;
	}
	
	/**
	 * Inserts the specified element at the specified position in this list
	 * (optional operation).  Shifts the element currently at that position
	 * (if any) and any subsequent elements to the right (adds one to their
	 * indices).
	 *
	 * @param index   index at which the specified element is to be inserted
	 * @param element element to be inserted
	 * @throws UnsupportedOperationException if the {@code add} operation
	 *                                       is not supported by this list
	 * @throws ClassCastException            if the class of the specified element
	 *                                       prevents it from being added to this list
	 * @throws NullPointerException          if the specified element is null and
	 *                                       this list does not permit null elements
	 * @throws IllegalArgumentException      if some property of the specified
	 *                                       element prevents it from being added to this list
	 * @throws IndexOutOfBoundsException     if the index is out of range
	 *                                       ({@code index < 0 || index > size()})
	 */
	@Override
	public void add(int index, E element) {
	
	}
	
	/**
	 * Removes the element at the specified position in this list (optional
	 * operation).  Shifts any subsequent elements to the left (subtracts one
	 * from their indices).  Returns the element that was removed from the
	 * list.
	 *
	 * @param index the index of the element to be removed
	 * @return the element previously at the specified position
	 * @throws UnsupportedOperationException if the {@code remove} operation
	 *                                       is not supported by this list
	 * @throws IndexOutOfBoundsException     if the index is out of range
	 *                                       ({@code index < 0 || index >= size()})
	 */
	@Override
	public E remove(int index) {
		clearGarbage();
		EquatableWeakReference<E> obj = internalList.remove(index);
		if (obj != null) return obj.get();
		return null;
	}
	
	/**
	 * Returns the index of the first occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the lowest index {@code i} such that
	 * {@code Objects.equals(o, get(i))},
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for
	 * @return the index of the first occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element
	 * @throws ClassCastException   if the type of the specified element
	 *                              is incompatible with this list
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this
	 *                              list does not permit null elements
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public int indexOf(Object o) {
		if (o instanceof WeakReference weak) o = EquatableWeakReference.from(weak);
		
		if (o instanceof EquatableWeakReference ref) {
			return internalList.indexOf(ref);
		} else {
			int size = size();
			for (int index = 0; index < size; index++) {
				EquatableWeakReference<E> element = internalList.get(index);
				E obj = element.get();
				if (obj != null && obj.equals(o)) {
					return index;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Returns the index of the last occurrence of the specified element
	 * in this list, or -1 if this list does not contain the element.
	 * More formally, returns the highest index {@code i} such that
	 * {@code Objects.equals(o, get(i))},
	 * or -1 if there is no such index.
	 *
	 * @param o element to search for
	 * @return the index of the last occurrence of the specified element in
	 * this list, or -1 if this list does not contain the element
	 * @throws ClassCastException   if the type of the specified element
	 *                              is incompatible with this list
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
	 * @throws NullPointerException if the specified element is null and this
	 *                              list does not permit null elements
	 *                              (<a href="Collection.html#optional-restrictions">optional</a>)
	 */
	@Override
	public int lastIndexOf(Object o) {
		if (o instanceof WeakReference weak) o = EquatableWeakReference.from(weak);
		
		if (o instanceof EquatableWeakReference ref) {
			return internalList.lastIndexOf(ref);
		} else {
			int size = size();
			for (int index = size - 1; index >= 0; index--) {
				EquatableWeakReference<E> element = internalList.get(index);
				E obj = element.get();
				if (obj != null && obj.equals(o)) {
					return index;
				}
			}
		}
		return -1;
	}
	
	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence).
	 *
	 * @return a list iterator over the elements in this list (in proper
	 * sequence)
	 */
	@NotNull
	@Override
	public ListIterator<E> listIterator() {
		return new ListIterator<E>() {
			private int currentIndex = 0;
			
			@Override
			public boolean hasNext() {
				return false;
			}
			
			@Override
			public E next() {
				return null;
			}
			
			@Override
			public boolean hasPrevious() {
				return false;
			}
			
			@Override
			public E previous() {
				return null;
			}
			
			@Override
			public int nextIndex() {
				return 0;
			}
			
			@Override
			public int previousIndex() {
				return 0;
			}
			
			@Override
			public void remove() {
			
			}
			
			@Override
			public void set(E e) {
			
			}
			
			@Override
			public void add(E e) {
			
			}
		};
	}
	
	/**
	 * Returns a list iterator over the elements in this list (in proper
	 * sequence), starting at the specified position in the list.
	 * The specified index indicates the first element that would be
	 * returned by an initial call to {@link ListIterator#next next}.
	 * An initial call to {@link ListIterator#previous previous} would
	 * return the element with the specified index minus one.
	 *
	 * @param index index of the first element to be returned from the
	 *              list iterator (by a call to {@link ListIterator#next next})
	 * @return a list iterator over the elements in this list (in proper
	 * sequence), starting at the specified position in the list
	 * @throws IndexOutOfBoundsException if the index is out of range
	 *                                   ({@code index < 0 || index > size()})
	 */
	@NotNull
	@Override
	public ListIterator<E> listIterator(int index) {
		return null;
	}
	
	/**
	 * Returns a view of the portion of this list between the specified
	 * {@code fromIndex}, inclusive, and {@code toIndex}, exclusive.  (If
	 * {@code fromIndex} and {@code toIndex} are equal, the returned list is
	 * empty.)  The returned list is backed by this list, so non-structural
	 * changes in the returned list are reflected in this list, and vice-versa.
	 * The returned list supports all of the optional list operations supported
	 * by this list.<p>
	 * <p>
	 * This method eliminates the need for explicit range operations (of
	 * the sort that commonly exist for arrays).  Any operation that expects
	 * a list can be used as a range operation by passing a subList view
	 * instead of a whole list.  For example, the following idiom
	 * removes a range of elements from a list:
	 * <pre>{@code
	 *      list.subList(from, to).clear();
	 * }</pre>
	 * Similar idioms may be constructed for {@code indexOf} and
	 * {@code lastIndexOf}, and all of the algorithms in the
	 * {@code Collections} class can be applied to a subList.<p>
	 * <p>
	 * The semantics of the list returned by this method become undefined if
	 * the backing list (i.e., this list) is <i>structurally modified</i> in
	 * any way other than via the returned list.  (Structural modifications are
	 * those that change the size of this list, or otherwise perturb it in such
	 * a fashion that iterations in progress may yield incorrect results.)
	 *
	 * @param fromIndex low endpoint (inclusive) of the subList
	 * @param toIndex   high endpoint (exclusive) of the subList
	 * @return a view of the specified range within this list
	 * @throws IndexOutOfBoundsException for an illegal endpoint index value
	 *                                   ({@code fromIndex < 0 || toIndex > size ||
	 *                                   fromIndex > toIndex})
	 */
	@NotNull
	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return null;
	}
}
