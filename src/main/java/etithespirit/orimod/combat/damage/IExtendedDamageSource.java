package etithespirit.orimod.combat.damage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IExtendedDamageSource<TDamageSource> {
	
	String ALIGNMENT_KEY = "alignment";
	
	static boolean isDecay(IExtendedDamageSource<?> src) {
		Object value = src.getCustomData(ALIGNMENT_KEY);
		if (value instanceof Integer intValue) {
			return intValue == 1;
		}
		return false;
	}
	
	static boolean isLight(IExtendedDamageSource<?> src) {
		Object value = src.getCustomData(ALIGNMENT_KEY);
		if (value instanceof Integer intValue) {
			return intValue == 0;
		}
		return false;
	}
	
	static <TSrc extends IExtendedDamageSource<TSrc>> TSrc setDecay(TSrc src) {
		return src.putCustomData(ALIGNMENT_KEY, 1);
	}
	
	static <TSrc extends IExtendedDamageSource<TSrc>> TSrc setLight(TSrc src) {
		return src.putCustomData(ALIGNMENT_KEY, 0);
	}
	
	/**
	 * Retrieves arbitrary data from this source, or null if no such value is registered.
	 * @param key The key of the data.
	 * @param <TValue> The type of data to return.
	 * @return The value associated with the key, or null if no such key is registered.
	 */
	@Nullable <TValue> TValue getCustomData(String key);
	
	/**
	 * Puts arbitrary data with the given key into this source.
	 * @param key The key to associate with the value.
	 * @param value The actual value, or null to remove the value.
	 * @param <TValue> The type of the value, mostly for type coercion.
	 * @return A reference to this object, for chaining.
	 * @exception IllegalStateException If {@link #lock()} has been called.
	 */
	<TValue> TDamageSource putCustomData(String key, @Nullable TValue value) throws IllegalStateException;
	
	/**
	 * Prevents any future calls to {@link #putCustomData(String, Object)}.
	 * @return A reference to this object, for chaining.
	 */
	TDamageSource lock();
	
}
