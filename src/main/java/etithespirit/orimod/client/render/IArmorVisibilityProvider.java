package etithespirit.orimod.client.render;

public interface IArmorVisibilityProvider {
	
	/**
	 * Update the visibility of all parts on this model.
	 * @param hasHelmet
	 * @param hasChestplate
	 * @param hasLeggings
	 * @param hasBoots
	 */
	void updateVisibility(boolean hasHelmet, boolean hasChestplate, boolean hasLeggings, boolean hasBoots);
	
}
