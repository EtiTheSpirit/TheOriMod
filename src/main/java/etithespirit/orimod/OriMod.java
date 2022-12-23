package etithespirit.orimod;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import etithespirit.orimod.apiimpl.EnvironmentalAffinityAPI;
import etithespirit.orimod.client.gui.block.LightRepairDeviceScreen;
import etithespirit.orimod.client.gui.health.SpiritHealthGui;
import etithespirit.orimod.client.gui.health.heart.HeartTexture;
import etithespirit.orimod.command.SetSpiritCommand;
import etithespirit.orimod.common.datamanagement.WorldLoading;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.datagen.BlockToolRelations;
import etithespirit.orimod.datagen.advancements.GenerateAdvancements;
import etithespirit.orimod.datagen.block.GenerateBlockModels;
import etithespirit.orimod.datagen.GenerateItemModels;
import etithespirit.orimod.client.render.RenderPlayerAsSpirit;
import etithespirit.orimod.datagen.audio.GenerateSoundsJson;
import etithespirit.orimod.datagen.features.GenerateBiomeFeatures;
import etithespirit.orimod.datagen.loot.GenerateLootTables;
import etithespirit.orimod.datagen.recipe.GenerateRecipes;
import etithespirit.orimod.networking.player.ReplicateKnownAbilities;
import etithespirit.orimod.networking.player.ReplicatePlayerMovement;
import etithespirit.orimod.networking.potion.EffectModificationReplication;
import etithespirit.orimod.networking.spirit.ReplicateSpiritStatus;
import etithespirit.orimod.player.DamageMarshaller;
import etithespirit.orimod.registry.advancements.AdvancementRegistry;
import etithespirit.orimod.registry.world.BlockRegistry;
import etithespirit.orimod.registry.gameplay.CapabilityRegistry;
import etithespirit.orimod.registry.gameplay.EntityRegistry;
import etithespirit.orimod.registry.world.FeatureRegistry;
import etithespirit.orimod.registry.world.FluidRegistry;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.MenuRegistry;
import etithespirit.orimod.registry.gameplay.EffectRegistry;
import etithespirit.orimod.registry.RenderRegistry;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.spirit.client.SpiritInput;
import etithespirit.orimod.registry.world.TileEntityRegistry;
import etithespirit.orimod.spirit.SpiritIdentifier;
import etithespirit.orimod.spirit.SpiritRestrictions;
import etithespirit.orimod.spirit.SpiritSize;
import etithespirit.orimod.spirit.SpiritSounds;
import etithespirit.orimod.spirit.common.MotionMarshaller;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.ForgeChunkManager;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The main class for the entire mod.
 */
@Mod(OriMod.MODID)
public final class OriMod {

	/** hnnnng kernol,,,,, id,,,, */
	public static final String MODID = "orimod";
	
	/***/
	public static final Logger LOG = LogManager.getLogger();
	
	private static boolean gotUseTrace = false;
	private static boolean useTrace = false;
	private static boolean shouldUseOriModTraceLogging() {
		if (!gotUseTrace) {
			useTrace = System.getProperty("sun.java.command", "").contains("--useOriModTraceLogs");
			gotUseTrace = true;
			LOG.info("Trace logging for The Ori Mod is " + (useTrace ? "ENABLED" : "DISABLED"));
		}
		return useTrace;
	}
	
	/**
	 * Logs to debug iff the user has defined <code>-DuseOriModTraceLogs</code> in their game arguments.
	 * @param message The message to log.
	 */
	public static void logCustomTrace(String message) {
		if (shouldUseOriModTraceLogging()) {
			LOG.debug(message);
		}
	}
	
	/**
	 * Logs to debug iff the user has defined <code>-DuseOriModTraceLogs</code> in their game arguments.
	 * @param message The message to log.
	 * @param args Any arguments to include with that message.
	 */
	public static void logCustomTrace(String message, Object... args) {
		if (shouldUseOriModTraceLogging()) {
			LOG.debug(message, args);
		}
	}

	/** Returns the singleton instance of this mod.
	 * @return The singleton instance of this mod. */
	public static OriMod getInstance() {
		return _instance;
	}
	private static OriMod _instance;
	
	private static boolean isModLoadingComplete = false;
	
	/***/
	public OriMod() {
		_instance = this;
		OriModConfigs.initialize();
		shouldUseOriModTraceLogging();
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientGameBuildInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedServerBuildInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataGenerated);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModLoadingComplete);
		
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerAllLayers);
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerAllEntities);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerBERenderers);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerShaders);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(SpiritHealthGui::setupHealthElement);
		
		MinecraftForge.EVENT_BUS.addListener(this::commandInit);
		
		etithespirit.orimod.api.spirit.SpiritAccessor._setMethods(SpiritIdentifier::isSpirit, SpiritIdentifier::setSpiritNetworked);
		
		BlockRegistry.registerAll();
		FluidRegistry.registerAll();
		ItemRegistry.registerAll();
		EffectRegistry.registerAll();
		SoundRegistry.registerAll();
		TileEntityRegistry.registerAll();
		EntityRegistry.registerAll();
		MenuRegistry.registerAll();
		FeatureRegistry.registerAll();
		
		MinecraftForge.EVENT_BUS.addListener(EnvironmentalAffinityAPI::onPlayerTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EnvironmentalAffinityAPI::onWorldTickEvent);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritRestrictions::onEat);
		MinecraftForge.EVENT_BUS.addListener(CapabilityRegistry::registerPlayerCaps);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onPlayerClone);
		MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, CapabilityRegistry::attachPlayerCaps);
	}
	
	/**
	 * Whether or not the forge mod loading cycle has completed.
	 * @return Whether or not the forge mod loading cycle has completed.
	 */
	public static boolean forgeLoadingComplete() {
		return isModLoadingComplete;
	}
	
	/**
	 * Occurs on setup for both the client and server.
	 * @param event The setup event.
	 */
	public void commonInit(final FMLCommonSetupEvent event) {
		// For TEs
		// MinecraftForge.EVENT_BUS.addListener(UpdateHelper::onBlockChanged);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritHealthGui::cancelHealthRender);
		
		MinecraftForge.EVENT_BUS.addListener(DamageMarshaller::onEntityAttacked);
		MinecraftForge.EVENT_BUS.addListener(DamageMarshaller::onEntityDamaged);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritSize::onPlayerTickedCommon);
		MinecraftForge.EVENT_BUS.addListener(SpiritSize::onGetEntitySizeCommon);
		
		// These events need to run for both the client game build and dedicated server build (as they apply to the integrated server too)
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedInServer);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedOutServer);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onRespawnedServer);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onChangeDimensionServer);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::performAirSounds);
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::onEntityHurt);
		MinecraftForge.EVENT_BUS.addListener(SpiritSounds::onEntityDied);
		
		MinecraftForge.EVENT_BUS.addListener(MotionMarshaller::onEntityJumped);
		MinecraftForge.EVENT_BUS.addListener(MotionMarshaller.Server::onServerTick);
		
		ReplicateSpiritStatus.Server.registerServerPackets();
		EffectModificationReplication.Server.registerServerPackets();
		ReplicatePlayerMovement.Server.registerServerPackets();
		ReplicateKnownAbilities.Server.registerServerPackets();
		
		event.enqueueWork(AdvancementRegistry::registerAll);
		// event.enqueueWork(MenuRegistry::registerAll);
		
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
		
		ForgeChunkManager.setForcedChunkLoadingCallback(MODID, (serverLevel, ticketHelper) -> {
			Map<BlockPos, Pair<LongSet, LongSet>> tickets = ticketHelper.getBlockTickets();
			// All OriMod tech blocks use ticking live checks, so the second of the pair is the point of interest.
			for (Map.Entry<BlockPos, Pair<LongSet, LongSet>> data : tickets.entrySet()) {
				BlockPos at = data.getKey();
				LongSet chunks = data.getValue().getSecond();
				if (chunks.size() != 1) {
					LOG.warn("A Light Assembly block registered more than one chunk with one BlockPos?! This could be a serious problem!");
				}
				BlockEntity ent = serverLevel.getBlockEntity(at);
				if (!(ent instanceof LightEnergyHandlingTile)) {
					LOG.debug("Found a chunk that was being kept alive by what was believed to be a Light Tech block at {}, however there was no Tech Block at that location, so the keep-alive ticket has been removed and this chunk may now rest.", at);
					ForgeChunkManager.forceChunk(serverLevel, MODID, at, at.getX() >> 4, at.getY() >> 4, false, true);
				}
			}
		});
	}
	
	/**
	 * Occurs on setup for the client.
	 * @param event The setup event.
	 */
	public void clientGameBuildInit(final FMLClientSetupEvent event) {
		//UniProfiler.setProfiler(Minecraft.getInstance().getProfiler(), Dist.CLIENT);
		
		//MinecraftForge.EVENT_BUS.addListener(SpiritHealthGui::setupHealthElement);
		
		/*
		MinecraftForge.EVENT_BUS.addListener(SpiritDash::onKeyPressed);
		MinecraftForge.EVENT_BUS.addListener(SpiritDash::onClientUpdated);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onEntityJumped);
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onKeyPressed);
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onPlayerTicked);
		*/
		MinecraftForge.EVENT_BUS.addListener(SpiritInput::onKeyPressed);
		//MinecraftForge.EVENT_BUS.addListener(SpiritInput::movementChanged);
		
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedInClient);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedOutClient);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onRespawnedClient);
		MinecraftForge.EVENT_BUS.addListener(MotionMarshaller.Client::onClientTick);
		MinecraftForge.EVENT_BUS.addListener(ReplicateSpiritStatus.Client::onPlayerExist);
		
		ReplicateSpiritStatus.Client.registerClientPackets();
		EffectModificationReplication.Client.registerClientPackets();
		ReplicateKnownAbilities.Client.registerClientPackets();
		// ReplicatePlayerMovement.Client.registerClientPackets();
		
		//OnRegisterKeyMappings?
		//ClientRegistry.registerKeyBinding(SpiritDash.DASH_BIND);
		//ClientRegistry.registerKeyBinding(SpiritJump.CLING_BIND);
		
		event.enqueueWork(() -> MenuScreens.register(MenuRegistry.LIGHT_REPAIR_DEVICE.get(), LightRepairDeviceScreen::new));
		event.enqueueWork(ItemRegistry::registerPredicates);
		

		MinecraftForge.EVENT_BUS.addListener(RenderPlayerAsSpirit::whenRenderingPlayer);
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.DECAY_FLUID_STATIC.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.DECAY_FLUID_FLOWING.get(), RenderType.translucent());
		
	}
	
	/**
	 * Occurs on setup for the server.
	 * @param event The setup event.
	 */
	public void dedicatedServerBuildInit(final FMLDedicatedServerSetupEvent event) {
		//ReplicateSpiritStatus.Server.registerServerPackets();
		//EffectModificationReplication.Server.registerServerPackets();
		//ReplicatePlayerMovement.Server.registerServerPackets();
	}
	
	/**
	 * Occurs on setup for the server when registering commands.
	 * @param event The setup event.
	 */
	public void commandInit(final RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		SetSpiritCommand.register(dispatcher);
	}
	
	/**
	 * Occurs on execution of the data generator.
	 * @param dataEvt The setup event.
	 */
	public void onDataGenerated(final GatherDataEvent dataEvt) throws RuntimeException {
		DataGenerator generator = dataEvt.getGenerator();
		BlockToolRelations blockTags = new BlockToolRelations(generator, dataEvt.getExistingFileHelper());
		GenerateAdvancements advancements = new GenerateAdvancements(generator, dataEvt.getExistingFileHelper());
		GenerateRecipes recipes = new GenerateRecipes(generator);
		GenerateBiomeFeatures biomeFeatures = new GenerateBiomeFeatures(generator, dataEvt.getExistingFileHelper());
		GenerateLootTables lootTables = new GenerateLootTables(generator);
		GenerateSoundsJson sounds = new GenerateSoundsJson(OriMod.MODID);//.setValidatesChannels();
		
		if (dataEvt.includeClient()) {
			GenerateBlockModels models = new GenerateBlockModels(generator, dataEvt.getExistingFileHelper());
			GenerateItemModels items = new GenerateItemModels(generator, dataEvt.getExistingFileHelper());
			generator.addProvider(true, models);
			generator.addProvider(true, items);
		}
		
		generator.addProvider(true, blockTags);
		generator.addProvider(true, sounds);
		generator.addProvider(true, advancements);
		generator.addProvider(true, recipes);
		generator.addProvider(true, lootTables);
		biomeFeatures.generateGorlekOreFeaturesUsing(dataEvt);
	}
	
	/**
	 * Occurs when mod loading has completed.
	 * @param evt The mod loading event.
	 */
	public void onModLoadingComplete(final FMLLoadCompleteEvent evt) {
		isModLoadingComplete = true;
		etithespirit.orimod.api.environment.defaultimpl.DefaultEnvironments.init(EffectRegistry.DECAY.get());
		EnvironmentalAffinityAPI.validate();
		
		if (!FMLEnvironment.production) {
			HeartTexture.validateCorrectTextureResolution();
		}
	}
	
	/**
	 * Constructs a new {@link ResourceLocation} with its namespace already set to <c>orimod</c>.
	 * @param path The path to the resource.
	 * @return A new {@link ResourceLocation} made such that it is <c>orimod:(path here)</c>.
	 */
	public static ResourceLocation rsrc(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	
}
