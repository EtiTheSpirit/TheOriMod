package etithespirit.orimod.datagen.loot;

import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.world.FluidRegistry;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenerateLootTables extends LootTableProvider {
	public GenerateLootTables(DataGenerator pGenerator) {
		super(pGenerator);
	}
	
	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
		// A list of key/value pairs where...
		// keys are Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>
		// values are LootContextParamSets
		return List.of(
			Pair.of(OriModBlockLoot::new, LootContextParamSets.BLOCK)
		);
	}
	
	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) {
		map.forEach((location, lootTable) -> LootTables.validate(tracker, location, lootTable));
	}
	
	@Override
	public String getName() {
		return "Ori Mod LootTables";
	}
	
	private static class OriModBlockLoot extends BlockLoot {
		
		/** The name of the empty loot table. */
		private static final ResourceLocation EMPTY = new ResourceLocation("minecraft", "empty");
		
		/** This allows a lazy solution where {@link #addTables} is also responsible for building the list of lootable blocks. */
		private final List<Block> KNOWN_BLOCKS = new ArrayList<>();
		
		@Override
		protected void add(Block block, LootTable.Builder builder) {
			if (block.getLootTable().equals(EMPTY)) {
				OriMod.LOG.warn("Block {} attempted to register itself for a loot table, but was declared with .noLootTable() in its properties constructor! It has been skipped.", block);
				return;
			}
			KNOWN_BLOCKS.add(block);
			super.add(block, builder);
		}
		
		@Override
		protected void addTables() {
			dropSelf(BlockRegistry.DECAY_DIRT_MYCELIUM.get());
			dropSelf(BlockRegistry.DECAY_PLANTMATTER_MYCELIUM.get());
			dropSelf(BlockRegistry.DECAY_LOG.get());
			dropSelf(BlockRegistry.DECAY_STRIPPED_LOG.get());
			add(BlockRegistry.DECAY_SURFACE_MYCELIUM.get(), noDrop());
			
			dropSelf(BlockRegistry.FORLORN_STONE.get());
			dropSelf(BlockRegistry.FORLORN_STONE_BRICKS.get());
			dropSelf(BlockRegistry.FORLORN_STONE_LINE.get());
			dropSelf(BlockRegistry.FORLORN_STONE_OMNI.get());
			
			dropSelf(BlockRegistry.HARDLIGHT_GLASS.get());
			dropSelf(BlockRegistry.LIGHT_REPAIR_BOX.get());
			
			add(BlockRegistry.GORLEK_ORE.get(), block -> createOreDrop(block, ItemRegistry.RAW_GORLEK_ORE.get()));
			dropSelf(BlockRegistry.RAW_GORLEK_ORE_BLOCK.get());
			dropSelf(BlockRegistry.GORLEK_METAL_BLOCK.get());
			dropSelf(BlockRegistry.GORLEK_NETHERITE_ALLOY_BLOCK.get());
			
			dropSelf(BlockRegistry.LIGHT_CAPACITOR.get());
			dropSelf(BlockRegistry.LIGHT_CONDUIT.get());
			dropSelf(BlockRegistry.SOLID_LIGHT_CONDUIT.get());
			dropSelf(BlockRegistry.LIGHT_TO_RF.get());
			dropSelf(BlockRegistry.INFINITE_LIGHT_SOURCE.get());
			dropSelf(BlockRegistry.LIGHT_TO_REDSTONE_SIGNAL.get());
			dropSelf(BlockRegistry.SOLAR_ENERGY_BLOCK.get());
			dropSelf(BlockRegistry.THERMAL_ENERGY_BLOCK.get());
		}
		
		@Override
		protected Iterable<Block> getKnownBlocks() {
			return KNOWN_BLOCKS;
		}
	}
}
