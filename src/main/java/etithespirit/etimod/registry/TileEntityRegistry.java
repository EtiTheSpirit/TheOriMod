package etithespirit.etimod.registry;

import java.util.function.Supplier;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.common.tile.light.TileEntityLightCapacitor;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TileEntityRegistry {

	private static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, EtiMod.MODID);
	
	public static final RegistryObject<TileEntityType<TileEntityLightCapacitor>> 
		LIGHT_CAPACITOR = TILE_ENTITIES.register("light_capacitor", getBuilderFor(TileEntityLightCapacitor::new, BlockRegistry.LIGHT_CAPACITOR));
	
	/**
	 * An alias method that quickly constructs a supplier for the given tile entity class, only requiring that said tile entity has a parameterless constructor and that only one block is bound to it.
	 * @param <T> The TileEntity type.
	 * @param cls The class of said TileEntity type.
	 * @param blockRegistries The block (as a RegistryObject) that can be used for this TileEntity
	 * @return
	 */
	private static <T extends TileEntity> Supplier<TileEntityType<T>> getBuilderFor(Supplier<T> ctor, RegistryObject<Block> block) {
		// TODO: This is a bit "grotesque" in general, feels a bit wrong to use. I wrote it so that it was shorter up top.
		// Shall I keep it? It's kinda stanky ngl
		return () -> TileEntityType.Builder.of(ctor, block.get()).build(null);
	}
	
	public static void registerAll() {
		TILE_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}
