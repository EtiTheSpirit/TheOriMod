package etithespirit.orimod.common.creative;

import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.registry.ItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class OriModCreativeModeTabs {
	
	private OriModCreativeModeTabs() {}
	
	/** A creative tab for combat items made by Spirits */
	public static final CreativeModeTab SPIRIT_COMBAT = new CreativeModeTab("spirit_combat") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.SPIRIT_ARC.get());
		}
	};
	
	/** A creative tab for machinery using Light as its energy. */
	public static final CreativeModeTab SPIRIT_MACHINERY = new CreativeModeTab("spirit_machinery") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.LIGHT_CAPACITOR.get().asItem());
		}
	};
	
	/** A creative tab for decay blocks. */
	public static final CreativeModeTab DECAY = new CreativeModeTab("decay") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.DECAY_DIRT_MYCELIUM.get().asItem());
		}
	};
	
}
