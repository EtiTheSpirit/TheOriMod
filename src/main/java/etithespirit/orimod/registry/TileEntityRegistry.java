package etithespirit.orimod.registry;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.tile.light.TileEntityLightCapacitor;
import etithespirit.orimod.common.tile.light.TileEntityLightEnergyConduit;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Registers all of my tile entities to the game.
 *
 * @author Eti
 */
public final class TileEntityRegistry {
	
	private static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, OriMod.MODID);
	
	public static final RegistryObject<BlockEntityType<TileEntityLightCapacitor>>
		LIGHT_CAPACITOR = TILE_ENTITIES.register("light_capacitor", getBuilderFor(TileEntityLightCapacitor::new, BlockRegistry.LIGHT_CAPACITOR));
	
	public static final RegistryObject<BlockEntityType<TileEntityLightEnergyConduit>>
		LIGHT_CONDUIT = TILE_ENTITIES.register("light_conduit", getBuilderFor(TileEntityLightEnergyConduit::new, BlockRegistry.LIGHT_CONDUIT));
	
	/**
	 * An alias method that quickly constructs a supplier for the given tile entity class,
	 * only requiring that said tile entity has a public parameterless constructor and that only one block
	 * is bound to it.
	 * @param <T> The TileEntity type.
	 * @param ctor The public parameterless constructor of said TileEntity type.
	 * @param block The block that can be used for this TileEntity
	 * @return A supplier for a {@link BlockEntityType}
	 */
	private static <T extends BlockEntity> Supplier<BlockEntityType<T>> getBuilderFor(Function2<BlockPos, BlockState, T> ctor, RegistryObject<Block> block) {
		return () -> BlockEntityType.Builder.of((at, state) -> ctor.apply(at, state), block.get()).build(null);
	}
	
	public static void registerAll() {
		TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
	@FunctionalInterface
	private interface Function2<TParam1, TParam2, TReturn> {
		TReturn apply(TParam1 param1, TParam2 param2);
	}
}
