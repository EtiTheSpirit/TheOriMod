package etithespirit.etimod.registry;

import etithespirit.etimod.EtiMod;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

public class DimensionRegistry {
	
	public static final RegistryKey<DimensionType> LIGHT_FOREST_TYPE = RegistryKey.getOrCreateKey(Registry.DIMENSION_TYPE_KEY, new ResourceLocation(EtiMod.MODID, "light_forest"));
    public static final RegistryKey<World> LIGHT_FOREST = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(EtiMod.MODID, "light_forest"));
    
    /** Does nothing except for allow something to reference this class. */
    public static final void registerAll() { }

}
