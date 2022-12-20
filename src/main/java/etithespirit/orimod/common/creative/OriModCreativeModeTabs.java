package etithespirit.orimod.common.creative;

import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
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
			return new ItemStack(ItemRegistry.LUMO_WAND.get());
		}
	};
	
	/** A creative tab for machinery using Light as its energy. */
	public static final CreativeModeTab SPIRIT_MACHINERY_COMPLETE = new CreativeModeTab("orimod.spirit_machinery") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.LIGHT_CAPACITOR.get().asItem());
		}
	};
	
	/** A creative tab for machinery using Light as its energy. */
	public static final CreativeModeTab SPIRIT_MACHINERY_PARTS = new CreativeModeTab("orimod.spirit_machinery_parts") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.LIGHT_CAPACITOR.get().asItem());
		}
	};
	
	/** A creative tab for decay blocks. */
	public static final CreativeModeTab DECAY = new CreativeModeTab("orimod.decay") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.DECAY_DIRT_MYCELIUM.get().asItem());
		}
	};
	
	
	/** A creative tab for decorative blocks. */
	public static final CreativeModeTab SPIRIT_DECORATION = new CreativeModeTab("orimod.spirit_decoration") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BlockRegistry.FORLORN_STONE.get().asItem());
		}
	};
	
}
