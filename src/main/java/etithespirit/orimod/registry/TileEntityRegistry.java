package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.LightEnergyTile;
import etithespirit.orimod.common.tile.light.implementations.LightCapacitorTile;
import etithespirit.orimod.common.tile.light.implementations.LightConduitTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Registers all of my tile entities to the game.
 *
 * @author Eti
 */
@SuppressWarnings("unchecked")
public final class TileEntityRegistry {
	
	private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, OriMod.MODID);
	/*
	public static final RegistryObject<BlockEntityType<TileEntityLightCapacitor>>
		LIGHT_CAPACITOR = TILE_ENTITIES.register("light_capacitor", getBuilderFor(TileEntityLightCapacitor::new, BlockRegistry.LIGHT_CAPACITOR));
	
	public static final RegistryObject<BlockEntityType<TileEntityLightCapacitor>>
		LIGHT_DEBUGGER = TILE_ENTITIES.register("light_debugger", getBuilderFor(TileEntityLightCapacitor::new, BlockRegistry.LIGHT_DEBUGGER));
	
	public static final RegistryObject<BlockEntityType<TileEntityLightEnergyConduit>>
		LIGHT_CONDUIT = TILE_ENTITIES.register("light_conduit", getBuilderFor(TileEntityLightEnergyConduit::new, BlockRegistry.LIGHT_CONDUIT));
	*/
	
	
	public static final RegistryObject<BlockEntityType<LightEnergyStorageTile>>
		LIGHT_ENERGY_STORAGE_TILE = TILE_ENTITIES.register(
		"light_capacitor",
		getBuilderFor(
			LightCapacitorTile::new,
			BlockRegistry.LIGHT_CAPACITOR
		));
	
	public static final RegistryObject<BlockEntityType<LightEnergyTile>>
		LIGHT_ENERGY_TILE = TILE_ENTITIES.register(
			"light_conduit",
			getBuilderFor(
				LightConduitTile::new,
				BlockRegistry.LIGHT_CONDUIT
			));
	
	/**
	 * An alias method that quickly constructs a supplier for the given tile entity class,
	 * only requiring that said tile entity has a public constructor that accepts the position and state.
	 * @param <T> The TileEntity type.
	 * @param ctor The public parameterless constructor of said TileEntity type.
	 * @param blocks The block(s) that can be used for this TileEntity
	 * @return A supplier for a {@link BlockEntityType}
	 */
	@SuppressWarnings("unchecked")
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> getBuilderFor(Function2<BlockPos, BlockState, T> ctor, RegistryObject<Block>... blocks) {
		return () -> {
			Block[] lookup = new Block[blocks.length];
			for (int i = 0; i < lookup.length; i++) {
				lookup[i] = blocks[i].get();
			}
			return BlockEntityType.Builder.of(ctor::apply, lookup).build(null);
		};
	}
	
	public static void registerAll() {
		TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	@FunctionalInterface
	private interface Function2<TParam1, TParam2, TReturn> {
		@NotNull TReturn apply(TParam1 param1, TParam2 param2);
	}
}
