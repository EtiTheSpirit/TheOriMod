package etithespirit.orimod;


import etithespirit.orimod.common.datamanagement.WorldLoading;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.datagen.BlockToolRelations;
import etithespirit.orimod.datagen.GenerateBlockModels;
import etithespirit.orimod.datagen.GenerateItemModels;
import etithespirit.orimod.client.render.RenderPlayerAsSpirit;
import etithespirit.orimod.common.block.UpdateHelper;
import etithespirit.orimod.networking.potion.EffectModificationReplication;
import etithespirit.orimod.networking.spirit.ReplicateSpiritStatus;
import etithespirit.orimod.player.DamageMarshaller;
import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.registry.ItemRegistry;
import etithespirit.orimod.registry.PotionRegistry;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.TileEntityRegistry;
import etithespirit.orimod.spirit.SpiritSize;
import etithespirit.orimod.spirit.SpiritSounds;
import etithespirit.orimod.spirit.client.SpiritDash;
import etithespirit.orimod.spirit.client.SpiritJump;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OriMod.MODID)
public final class OriMod {

	/** hnnnng kernol,,,,, id,,,, */
	public static final String MODID = "orimod";
	
	public static final Logger LOG = LogManager.getLogger();

	/** Returns the singleton instance of this mod. */
	public static OriMod getInstance() {
		return _instance;
	}
	public static OriMod _instance;
	
	private static boolean isModLoadingComplete = false;
	
	public OriMod() {
		_instance = this;
		OriModConfigs.initialize();
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientGameBuildInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedServerBuildInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataGenerated);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModLoadingComplete);
		
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerAllLayers);
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerAllEntities);
		
		MinecraftForge.EVENT_BUS.addListener(this::commandInit);
		
		BlockRegistry.registerAll();
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
		
		
		MinecraftForge.EVENT_BUS.addListener(DamageMarshaller::onEntityAttacked);
		MinecraftForge.EVENT_BUS.addListener(DamageMarshaller::onEntityDamaged);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritSize::onPlayerTickedCommon);
		MinecraftForge.EVENT_BUS.addListener(SpiritSize::onGetEntitySizeCommon);
		
		// These events need to run for both the client game build and dedicated server build (as they apply to the integrated server too)
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedInServer);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedOutServer);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onRespawnedServer);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::performAirSounds);
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::onEntityHurt);
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::onEntityDied);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onEntityJumped);
		
		/*
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityRegistry::attachCapabilities);
		MinecraftForge.EVENT_BUS.addListener(CapabilityRegistry::persistCapabilities);
		
		
		// MinecraftForge.EVENT_BUS.addListener(EffectEnforcement::enforceEffects);
		
		
		event.enqueueWork(() -> {
			Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(EtiMod.MODID, "light_forest_chunkgen"), LightForestChunkGenerator.CORE_CODEC);
			Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(EtiMod.MODID, "light_forest"), LightForestBiomeProvider.BIOME_CODEC);
		});
		
		CapabilityRegistry.registerAll();
		*/
	}
	
	public void clientGameBuildInit(final FMLClientSetupEvent event) {
		//UniProfiler.setProfiler(Minecraft.getInstance().getProfiler(), Dist.CLIENT);
		
		
		MinecraftForge.EVENT_BUS.addListener(SpiritDash::onKeyPressed);
		MinecraftForge.EVENT_BUS.addListener(SpiritDash::onClientUpdated);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onEntityJumped);
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onKeyPressed);
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onPlayerTicked);
		
		//MinecraftForge.EVENT_BUS.addListener(CustomHealthForEffects::onElementDrawn);
		//MinecraftForge.EVENT_BUS.addListener(LightTileDebugRenderer::onWorldFinishedRendering);
		
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedInClient);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedOutClient);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onRespawnedClient);
		
		ReplicateSpiritStatus.registerPackets(Dist.CLIENT);
		EffectModificationReplication.registerPackets(Dist.CLIENT);
		ClientRegistry.registerKeyBinding(SpiritDash.DASH_BIND);
		ClientRegistry.registerKeyBinding(SpiritJump.CLING_BIND);
		
		/*
		
		*/
		MinecraftForge.EVENT_BUS.addListener(RenderPlayerAsSpirit::whenRenderingPlayer);
		/*
		
		
		
		*/
		
	}
	
	public void dedicatedServerBuildInit(final FMLDedicatedServerSetupEvent event) {
		ReplicateSpiritStatus.registerPackets(Dist.DEDICATED_SERVER);
		EffectModificationReplication.registerPackets(Dist.DEDICATED_SERVER);
		//EffectModificationReplication.registerPackets(Dist.DEDICATED_SERVER);
		
		//UniProfiler.setProfiler(.getProfiler(), Dist.DEDICATED_SERVER);
	}
	
	public void commandInit(final RegisterCommandsEvent event) {
		//CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		//SetSpiritCommand.registerCommand(dispatcher);
	}
	
	public void onDataGenerated(final GatherDataEvent dataEvt) {
		if (dataEvt.includeClient()) {
			DataGenerator generator = dataEvt.getGenerator();
			GenerateBlockModels models = new GenerateBlockModels(generator, dataEvt.getExistingFileHelper());
			GenerateItemModels items = new GenerateItemModels(generator, dataEvt.getExistingFileHelper());
			BlockToolRelations blockTags = new BlockToolRelations(generator, dataEvt.getExistingFileHelper());
			generator.addProvider(models);
			generator.addProvider(items);
			generator.addProvider(blockTags);
		}
	}
	
	public void onModLoadingComplete(final FMLLoadCompleteEvent evt) {
		isModLoadingComplete = true;
	}
	
}