package etithespirit.etimod.routine;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

/**
 * A bare-minimum, non-cancelable implementation of a Promise. Basically, this just lets me return a value late. That's all it's good for.
 * @author Eti
 *
 * @param <TResult> The result of this promise.
 */
public class SimplePromise<TResult> {
	
	private List<Consumer<TResult>> handlers = new ArrayList<Consumer<TResult>>();
	
	private boolean hasResult = false;
	
	private TResult result = null;
	
	public SimplePromise() { }
	
	public SimplePromise(TResult result) {
		this.result = result;
		hasResult = true;
	}
	
	/**
	 * Register the given callback.
	 * @param callback
	 */
	public void connect(Consumer<TResult> callback) {
		handlers.add(callback);
	}
	
	/**
	 * Remove the given callback.
	 * @param callback
	 */
	public void disconnect(Consumer<TResult> callback) {
		handlers.remove(callback);
	}
	
	/**
	 * Attempts to acquire the result of this promise. To check if a result has been found, use {@link #hasResult()}
	 * @return The result of this promise, or null if it does not yet have a result.
	 */
	public @Nullable TResult getResult() {
		if (!hasResult) return null;
		return result;
	}
	
	/**
	 * @return Whether or not a result has been found.
	 */
	public boolean hasResult() {
		return hasResult;
	}
	
	/**
	 * <strong>This method is internal.</strong> For lack of a Java "internal" keyword, I can only politely ask you to not use this yourself 
	 * and hope for the best. Then again, you'd be shooting yourself in the foot in doing so. Oh well.
	 * @param result The result of this promise.
	 * @throws IllegalStateException If the result has already been set.
	 */
	public void setResult(TResult result) throws IllegalStateException {
		if (hasResult) throw new IllegalStateException("This promise already has a result. Cannot set a result again.");
		this.result = result;
		hasResult = true;
		for (Consumer<TResult> consumnnor : handlers) {
			// hnnng,,,, kernol,,,,, consumn big mac,,,,,,
			consumnnor.accept(result);
		}
	}

}
