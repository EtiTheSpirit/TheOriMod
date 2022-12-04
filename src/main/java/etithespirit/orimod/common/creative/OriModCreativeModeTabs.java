package etithespirit.orimod.common.creative;

import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.registry.ItemRegistry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class OriModCreativeModeTabs {
	
	private OriModCreativeModeTabs() {}
	
	/** A creative tab for combat items made by Spirits */
	public static final CreativeModeTab SPIRIT_COMBAT = new CreativeModeTab("orimod.spirit_combat") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(ItemRegistry.SPIRIT_ARC.get());
		}
	};
	
	/** A creative tab for tools made by Spirits */
	public static final CreativeModeTab SPIRIT_TOOLS = new CreativeModeTab("orimod.spirit_tools") {
		@Override
		public ItemStack makeIcon() {
			//return new ItemStack(ItemRegistry.SPIRIT_ARC.get());
			return ItemStack.EMPTY;
		}
	};
	
	/** A creative tab for machinery using Light as its energy. */
	public static final CreativeModeTab SPIRIT_MACHINERY_COMPLETE = new CreativeModeTab("orimod.spirit_machinery") {
		@Override
		public ItemStack makeIcon() {
			//return new ItemStack(BlockRegistry.LIGHT_CAPACITOR.get().asItem());
			return ItemStack.EMPTY;
		}
	};
	
	/** A creative tab for machinery using Light as its energy. */
	public static final CreativeModeTab SPIRIT_MACHINERY_PARTS = new CreativeModeTab("orimod.spirit_machinery_parts") {
		@Override
		public ItemStack makeIcon() {
			//return new ItemStack(BlockRegistry.LIGHT_CAPACITOR.get().asItem());
			return ItemStack.EMPTY;
		}
	};
	
	/** A creative tab for decay blocks. */
	public static final CreativeModeTab DECAY = new CreativeModeTab("orimod.decay") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.DECAY_DIRT_MYCELIUM.get().asItem());
		}
	};
	
}
