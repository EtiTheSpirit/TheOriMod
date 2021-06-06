package etithespirit.etimod.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;

public final class Configuration { 

	public static final String CATEGORY_GENERAL = "general";
	public static final String CATEGORY_SPIRITS = "spirits";
	public static final String CATEGORY_WORLD = "world";
	
	/*
	 * The configuration on the server only.
	 */
	//public static final ForgeConfigSpec SERVER_CONFIG;
	
	/*
	 * The configuration on the client only.
	 */
	//public static final ForgeConfigSpec CLIENT_CONFIG;
	
	/**
	 * The configuration for both the client and the server.
	 */
	public static final ForgeConfigSpec COMMON_CONFIG;
	
	/**
	 * The amount of jumps a spirit can perform in the air.
	 */
	public static final ForgeConfigSpec.IntValue AIR_JUMPS;
	
	static {
		//ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();
		//ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();
		ForgeConfigSpec.Builder commonBuilder = new ForgeConfigSpec.Builder();
		
		commonBuilder.push(CATEGORY_SPIRITS);
		AIR_JUMPS = commonBuilder.translation("config.etimod.spirit.air_jumps.description").defineInRange("airJumps", 1, 0, 2);
		commonBuilder.pop();
		
		COMMON_CONFIG = commonBuilder.build();
	}
	
	@SubscribeEvent
	public static void onLoad(final ModConfig.Loading configEvent) {
		
	}

    @SubscribeEvent
    public static void onReload(final ModConfig.Reloading configEvent) {
    	
    }
	
}