package etithespirit.etimod;

import etithespirit.etimod.client.gui.CustomHealthForEffects;
import etithespirit.etimod.client.player.RenderPlayerAsSpirit;
import etithespirit.etimod.client.player.spiritbehavior.SpiritDash;
import etithespirit.etimod.client.player.spiritbehavior.SpiritJump;
import etithespirit.etimod.client.player.spiritbehavior.SpiritSize;
import etithespirit.etimod.client.player.spiritbehavior.SpiritSounds;
import etithespirit.etimod.client.render.debug.LightTileDebugRenderer;
import etithespirit.etimod.common.block.UpdateHelper;
import etithespirit.etimod.common.datamanagement.WorldLoading;
import etithespirit.etimod.common.player.DamageMarshaller;
import etithespirit.etimod.common.player.EffectEnforcement;
import etithespirit.etimod.registry.*;
import etithespirit.etimod.util.profiling.UniProfiler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import etithespirit.datagen.GenerateBlockModels;
import etithespirit.datagen.GenerateItemModels;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.server.command.SetSpiritCommand;
import etithespirit.etimod.world.dimension.LightForestBiomeProvider;
import etithespirit.etimod.world.dimension.LightForestChunkGenerator;
import net.minecraft.command.CommandSource;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The core code for Eti's Mod // The Ori Mod
 *
 * @author Eti
 */
@Mod(value=EtiMod.MODID)
public final class EtiMod {
		
    public static final String MODID = "etimod";
    // public static final ResourceLocation RSRC_BASE = new ResourceLocation(MODID);
    
    public static EtiMod INSTANCE;
    
    private static boolean isModLoadingComplete = false;
    
    public static final Logger LOG = LogManager.getLogger();
    
    public EtiMod() {
    	INSTANCE = this;
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientGameBuildInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedServerBuildInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataGenerated);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModLoadingComplete);
    	
    	MinecraftForge.EVENT_BUS.addListener(this::commandInit);
	    MinecraftForge.EVENT_BUS.addListener(this::anyServerInit);
	
	    BiomeRegistry.registerAll();
	    BlockRegistry.registerAll();
	    // CapabilityRegistry.registerAll();
	    DimensionRegistry.registerAll();
	    EntityRegistry.registerAll();
	    EntityAttributeMarshaller.registerAll();
	    ItemRegistry.registerAll();
	    PotionRegistry.registerAll();
    	SoundRegistry.registerAll();
    	TileEntityRegistry.registerAll();
    }
    
    /**
     * Whether or not the forge mod loading cycle has completed.
     */
    public static boolean forgeLoadingComplete() {
    	return isModLoadingComplete;
    }
    
	public void commonInit(final FMLCommonSetupEvent event) {
    	// For TEs
    	MinecraftForge.EVENT_BUS.addListener(UpdateHelper::onBlockChanged);
		
    	// These events need to run for both the client game build and dedicated server build (as they apply to the integrated server too)
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedInServer);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedOutServer);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onRespawnedServer);
		
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityRegistry::attachCapabilities);
		MinecraftForge.EVENT_BUS.addListener(CapabilityRegistry::persistCapabilities);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::performAirSounds);
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::onEntityHurt);
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::onEntityDied);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritSize::onPlayerTickedCommon);
		MinecraftForge.EVENT_BUS.addListener(SpiritSize::onGetEntitySizeCommon);
		
		MinecraftForge.EVENT_BUS.addListener(DamageMarshaller::onEntityAttacked);
		MinecraftForge.EVENT_BUS.addListener(DamageMarshaller::onEntityDamaged);
		
		// MinecraftForge.EVENT_BUS.addListener(EffectEnforcement::enforceEffects);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onEntityJumped);
    	
		event.enqueueWork(() -> {
			Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(EtiMod.MODID, "light_forest_chunkgen"), LightForestChunkGenerator.CORE_CODEC);
			Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(EtiMod.MODID, "light_forest"), LightForestBiomeProvider.BIOME_CODEC);
		});
		
		CapabilityRegistry.registerAll();
	}
    
    public void clientGameBuildInit(final FMLClientSetupEvent event) {
    	UniProfiler.setProfiler(Minecraft.getInstance().getProfiler(), Dist.CLIENT);
    	
	    MinecraftForge.EVENT_BUS.addListener(SpiritDash::onKeyPressed);
	    MinecraftForge.EVENT_BUS.addListener(SpiritDash::onClientUpdated);
    	
    	MinecraftForge.EVENT_BUS.addListener(SpiritJump::onEntityJumped);
    	MinecraftForge.EVENT_BUS.addListener(SpiritJump::onKeyPressed);
    	MinecraftForge.EVENT_BUS.addListener(SpiritJump::onPlayerTicked);
    	
    	MinecraftForge.EVENT_BUS.addListener(RenderPlayerAsSpirit::whenRenderingPlayer);
		MinecraftForge.EVENT_BUS.addListener(CustomHealthForEffects::onElementDrawn);
	    MinecraftForge.EVENT_BUS.addListener(LightTileDebugRenderer::onWorldFinishedRendering);
	
	    MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedInClient);
	    MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedOutClient);
	    MinecraftForge.EVENT_BUS.addListener(WorldLoading::onRespawnedClient);
    	
    	RenderRegistry.registerAll();
    	ReplicateMorphStatus.registerPackets(Dist.CLIENT);
    	ClientRegistry.registerKeyBinding(SpiritDash.DASH_BIND);
    	ClientRegistry.registerKeyBinding(SpiritJump.CLING_BIND);
    }
    
    @SuppressWarnings("unused")
    public void dedicatedServerBuildInit(final FMLDedicatedServerSetupEvent event) {
    	ReplicateMorphStatus.registerPackets(Dist.DEDICATED_SERVER);
    }
    
    public void anyServerInit(final FMLServerStartedEvent event) {
    	if (event.getServer() instanceof DedicatedServer) {
		    UniProfiler.setProfiler(event.getServer().getProfiler(), Dist.DEDICATED_SERVER);
	    }
    }
    
    public void commandInit(final RegisterCommandsEvent event) {
    	CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
    	SetSpiritCommand.registerCommand(dispatcher);
    }
    
	public void onDataGenerated(final GatherDataEvent dataEvt) {
		if (dataEvt.includeClient()) {
			DataGenerator generator = dataEvt.getGenerator();
			GenerateBlockModels models = new GenerateBlockModels(generator, dataEvt.getExistingFileHelper());
			GenerateItemModels items = new GenerateItemModels(generator, dataEvt.getExistingFileHelper());
			generator.addProvider(models);
			generator.addProvider(items);
		}
	}
	
	public void onModLoadingComplete(final FMLLoadCompleteEvent evt) {
		isModLoadingComplete = true;
	}
}