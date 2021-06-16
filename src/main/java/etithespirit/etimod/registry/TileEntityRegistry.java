package etithespirit.etimod.registry;

import java.util.function.Supplier;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.tile.light.TileEntityLightCapacitor;
import etithespirit.etimod.common.tile.light.TileEntityLightEnergyConduit;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class TileEntityRegistry {

	private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EtiMod.MODID);
	
	public static final RegistryObject<TileEntityType<TileEntityLightCapacitor>>
		LIGHT_CAPACITOR = TILE_ENTITIES.register("light_capacitor", getBuilderFor(TileEntityLightCapacitor::new, BlockRegistry.LIGHT_CAPACITOR));
	
	public static final RegistryObject<TileEntityType<TileEntityLightEnergyConduit>>
		LIGHT_CONDUIT = TILE_ENTITIES.register("light_conduit", getBuilderFor(TileEntityLightEnergyConduit::new, BlockRegistry.LIGHT_CONDUIT));
	
	/**
	 * An alias method that quickly constructs a supplier for the given tile entity class,
	 * only requiring that said tile entity has a public parameterless constructor and that only one block
	 * is bound to it.
	 * @param <T> The TileEntity type.
	 * @param ctor The public parameterless constructor of said TileEntity type.
	 * @param block The block that can be used for this TileEntity
	 * @return A supplier for a {@link TileEntityType}
	 */
	private static <T extends TileEntity> Supplier<TileEntityType<T>> getBuilderFor(Supplier<T> ctor, RegistryObject<Block> block) {
		return () -> TileEntityType.Builder.of(ctor, block.get()).build(null);
	}
	
	public static void registerAll() {
		TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
