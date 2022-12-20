package etithespirit.orimod.datagen.loot;

import com.mojang.datafixers.util.Pair;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.world.FluidRegistry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GenerateLootTables extends LootTableProvider {
	public GenerateLootTables(DataGenerator pGenerator) {
		super(pGenerator);
	}
	
	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables() {
		return List.of(
			Pair.of(OriModBlockLoot::new, LootContextParamSet.builder().build())
		);
	}
	
	@Override
	protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) {
		map.forEach((location, lootTable) -> LootTables.validate(tracker, location, lootTable));
	}
	
	
	private static class OriModBlockLoot extends BlockLoot {
		@Override
		protected void addTables() {
			/*
			dropSelf(BlockRegistry.DECAY_DIRT_MYCELIUM.get());
			dropSelf(BlockRegistry.DECAY_PLANTMATTER_MYCELIUM.get());
			dropSelf(BlockRegistry.DECAY_LOG.get());
			dropSelf(BlockRegistry.DECAY_STRIPPED_LOG.get());
			*/
			//dropWhenSilkTouch(BlockRegistry.DECAY_SURFACE_MYCELIUM.get());
			add(BlockRegistry.DECAY_SURFACE_MYCELIUM.get(), noDrop());
			/*
			dropSelf(BlockRegistry.FORLORN_STONE.get());
			dropSelf(BlockRegistry.FORLORN_STONE_BRICKS.get());
			dropSelf(BlockRegistry.FORLORN_STONE_OMNI.get());
			dropSelf(BlockRegistry.FORLORN_STONE_LINE.get());
			dropSelf(BlockRegistry.HARDLIGHT_GLASS.get());
			dropSelf(BlockRegistry.LIGHT_REPAIR_BOX.get());
			*/
			add(BlockRegistry.GORLEK_ORE.get(), block -> createOreDrop(block, ItemRegistry.RAW_GORLEK_ORE.get()));
			/*
			dropSelf(BlockRegistry.GORLEK_METAL_BLOCK.get());
			dropSelf(BlockRegistry.LIGHT_CAPACITOR.get());
			dropSelf(BlockRegistry.LIGHT_CONDUIT.get());
			dropSelf(BlockRegistry.SOLID_LIGHT_CONDUIT.get());
			dropSelf(BlockRegistry.INFINITE_LIGHT_SOURCE.get());
			dropSelf(BlockRegistry.LIGHT_TO_REDSTONE_SIGNAL.get());
			dropSelf(BlockRegistry.LIGHT_TO_RF.get());
			dropSelf(BlockRegistry.SOLAR_ENERGY_BLOCK.get());
			dropSelf(BlockRegistry.THERMAL_ENERGY_BLOCK.get());
			*/
		}
		
		@Override
		@SuppressWarnings("unchecked")
		protected Iterable<Block> getKnownBlocks() {
			//return (Iterable<Block>)BlockRegistry.BLOCKS_TO_REGISTER.stream().map(RegistryObject::get).toList();
			return List.of(
				BlockRegistry.GORLEK_ORE.get(),
				BlockRegistry.DECAY_SURFACE_MYCELIUM.get()
			);
		}
	}
}
