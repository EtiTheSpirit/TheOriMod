package etithespirit.etimod.world.dimension.generation.feature;

import etithespirit.etimod.world.dimension.generation.style.GenerationStyle;

@SuppressWarnings("unused")
public interface IFeatureRoutine {
	
	/**
	 * @return Whether or not this element is enabled.
	 */
	@SuppressWarnings("rawtypes")
	default boolean isEnabled() {
		if (this instanceof GenerationStyle) {
			return ((GenerationStyle)this).getSettings().getEnabled();
		}
		return true;
	}
}
