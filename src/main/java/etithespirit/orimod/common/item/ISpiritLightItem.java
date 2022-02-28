package etithespirit.orimod.common.item;


import net.minecraft.world.item.ItemStack;

/**
 * Denotes this item as one of a Spirit's Light, which implies it was made through themselves or some technology they were using.
 */
public interface ISpiritLightItem {
	
	/**
	 * Whether or not this item is able to be repaired in a Luxen Forge, which does not exist yet.
	 * @param stack The specific item stack being put into the machine.
	 * @return True if the forge can repair the item, false if not.
	 */
	boolean canRepairAtLuxForge(ItemStack stack);
	
}
