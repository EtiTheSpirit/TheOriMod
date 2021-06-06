package etithespirit.etimod.world.dimension.generation.feature;

import etithespirit.etimod.world.dimension.generation.style.GenerationStyle;

public interface IFeatureRoutine {

	
	
	/**
	 * Returns whether or not this element is enabled.
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public default boolean isEnabled() {
		if (this instanceof GenerationStyle) {
			return ((GenerationStyle)this).getSettings().getEnabled();
		}
		return true;
	}
}
