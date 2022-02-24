package etithespirit.orimod.common.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * An item used in crafting Light-based technologies.
 */
public class LightLens extends Item {
	public LightLens(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return 16;
	}
}
