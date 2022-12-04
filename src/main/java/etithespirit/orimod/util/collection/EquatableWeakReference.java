package etithespirit.orimod.util.collection;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * A modified variation of a weak reference that somewhat violates its contract by allowing two things:<br/>
 * <ul>
 *     <li>Allowing this reference to be compared to another with {@link EquatableWeakReference#equals}</li>
 *     <li>Allowing this reference to form a strong reference to its stored object (for its use in {@link WeakList})</li>
 * </ul>
 * @param <T>
 */
public class EquatableWeakReference<T> extends WeakReference<T> {
	
	private T storedStrong = null;
	
	/**
	 * Creates a new weak reference that refers to the given object.  The new
	 * reference is not registered with any queue.
	 *
	 * @param referent object the new weak reference will refer to
	 */
	public EquatableWeakReference(T referent) {
		super(referent);
	}
	
	/**
	 * Creates a new weak reference that refers to the given object and is
	 * registered with the given queue.
	 *
	 * @param referent object the new weak reference will refer to
	 * @param q        the queue with which the reference is to be registered,
	 *                 or {@code null} if registration is not required
	 */
	public EquatableWeakReference(T referent, ReferenceQueue<? super T> q) {
		super(referent, q);
	}
	
	public static <T> EquatableWeakReference<T> from(WeakReference<T> standard) {
		if (standard instanceof EquatableWeakReference) return (EquatableWeakReference<T>)standard;
		return new EquatableWeakReference(standard.get());
	}
	
	/**
	 * Attempts to make this reference strong.
	 * @return Whether or not this operation was successful. It will fail if the object was already disposed of.
	 * @throws IllegalStateException If this is already strong.
	 */
	public boolean tryMakeStrong() throws IllegalStateException {
		if (storedStrong != null) throw new IllegalStateException("Cannot make this reference strong; it is already strong!");
		storedStrong = get();
		return storedStrong != null;
	}
	
	/**
	 * Returns this reference to its weak state.
	 * @throws IllegalStateException If the reference is already marked as weak.
	 */
	public void makeWeak() throws IllegalStateException {
		if (storedStrong == null) throw new IllegalStateException("Cannot make this reference weak; it is already weak!");
		storedStrong = null;
	}
	
	public boolean isStrong() {
		return storedStrong != null && get() != null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WeakReference ref) {
			T element = get();
			if (element != null) return element.equals(ref.get());
		}
		return false;
	}
}
