package etithespirit.orimod;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import etithespirit.orimod.apiimpl.EnvironmentalAffinityAPI;
import etithespirit.orimod.client.render.hud.SpiritHealthGui;
import etithespirit.orimod.command.SetSpiritCommand;
import etithespirit.orimod.common.datamanagement.WorldLoading;
import etithespirit.orimod.common.potion.DecayEffect;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.datagen.BlockToolRelations;
import etithespirit.orimod.datagen.block.GenerateBlockModels;
import etithespirit.orimod.datagen.GenerateItemModels;
import etithespirit.orimod.client.render.RenderPlayerAsSpirit;
import etithespirit.orimod.datagen.audio.GenerateSoundsJson;
import etithespirit.orimod.networking.potion.EffectModificationReplication;
import etithespirit.orimod.networking.spirit.ReplicateSpiritStatus;
import etithespirit.orimod.player.DamageMarshaller;
import etithespirit.orimod.registry.BlockRegistry;
import etithespirit.orimod.registry.EntityRegistry;
import etithespirit.orimod.registry.FluidRegistry;
import etithespirit.orimod.registry.ItemRegistry;
import etithespirit.orimod.registry.PotionRegistry;
import etithespirit.orimod.registry.RenderRegistry;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.TileEntityRegistry;
import etithespirit.orimod.spirit.SpiritIdentifier;
import etithespirit.orimod.spirit.SpiritRestrictions;
import etithespirit.orimod.spirit.SpiritSize;
import etithespirit.orimod.spirit.SpiritSounds;
import etithespirit.orimod.spirit.client.SpiritDash;
import etithespirit.orimod.spirit.client.SpiritJump;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
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
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientGameBuildInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedServerBuildInit);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataGenerated);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModLoadingComplete);
		
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerAllLayers);
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerAllEntities);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerBERenderers);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(RenderRegistry::registerShaders);
		
		MinecraftForge.EVENT_BUS.addListener(this::commandInit);
		
		etithespirit.orimod.api.spirit.SpiritAccessor._setMethods(SpiritIdentifier::isSpirit, SpiritIdentifier::setSpiritNetworked);
		
		BlockRegistry.registerAll();
		FluidRegistry.registerAll();
		ItemRegistry.registerAll();
		PotionRegistry.registerAll();
		SoundRegistry.registerAll();
		TileEntityRegistry.registerAll();
		EntityRegistry.registerAll();
		
		MinecraftForge.EVENT_BUS.addListener(EnvironmentalAffinityAPI::onPlayerTickEvent);
		MinecraftForge.EVENT_BUS.addListener(EnvironmentalAffinityAPI::onWorldTickEvent);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritRestrictions::onEat);
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
				if (!(ent instanceof LightEnergyStorageTile)) {
					LOG.info("Found a chunk that was being kept alive by what was believed to be a Light Tech block at {}, however there was no Tech Block at that location, so the keep-alive ticket has been removed and this chunk may now rest.", at);
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
		
		MinecraftForge.EVENT_BUS.addListener(SpiritHealthGui::setupHealthElement);
		MinecraftForge.EVENT_BUS.addListener(SpiritDash::onKeyPressed);
		MinecraftForge.EVENT_BUS.addListener(SpiritDash::onClientUpdated);
		
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onEntityJumped);
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onKeyPressed);
		MinecraftForge.EVENT_BUS.addListener(SpiritJump::onPlayerTicked);
		
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedInClient);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onLoggedOutClient);
		MinecraftForge.EVENT_BUS.addListener(WorldLoading::onRespawnedClient);
		
		ReplicateSpiritStatus.registerPackets(Dist.CLIENT);
		EffectModificationReplication.registerPackets(Dist.CLIENT);
		
		//OnRegisterKeyMappings?
		//ClientRegistry.registerKeyBinding(SpiritDash.DASH_BIND);
		//ClientRegistry.registerKeyBinding(SpiritJump.CLING_BIND);

		MinecraftForge.EVENT_BUS.addListener(RenderPlayerAsSpirit::whenRenderingPlayer);
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.DECAY_FLUID_STATIC.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(FluidRegistry.DECAY_FLUID_FLOWING.get(), RenderType.translucent());
		
	}
	
	/**
	 * Occurs on setup for the server.
	 * @param event The setup event.
	 */
	public void dedicatedServerBuildInit(final FMLDedicatedServerSetupEvent event) {
		ReplicateSpiritStatus.registerPackets(Dist.DEDICATED_SERVER);
		EffectModificationReplication.registerPackets(Dist.DEDICATED_SERVER);
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
		if (dataEvt.includeClient()) {
			DataGenerator generator = dataEvt.getGenerator();
			GenerateBlockModels models = new GenerateBlockModels(generator, dataEvt.getExistingFileHelper());
			GenerateItemModels items = new GenerateItemModels(generator, dataEvt.getExistingFileHelper());
			BlockToolRelations blockTags = new BlockToolRelations(generator, dataEvt.getExistingFileHelper());
			
			File modProjectRoot = new File(".").getAbsoluteFile().getParentFile().getParentFile();
			File srcFolder = new File(modProjectRoot, "src");
			LOG.info(srcFolder.getAbsolutePath());
			
			GenerateSoundsJson sounds = new GenerateSoundsJson(OriMod.MODID, srcFolder.getAbsolutePath());
			
			generator.addProvider(true, models);
			generator.addProvider(true, items);
			generator.addProvider(true, blockTags);
			generator.addProvider(true, sounds);
			//File file = new File(".");
			
		}
	}
	
	/**
	 * Occurs when mod loading has completed.
	 * @param evt The mod loading event.
	 */
	public void onModLoadingComplete(final FMLLoadCompleteEvent evt) {
		isModLoadingComplete = true;
		etithespirit.orimod.api.environment.defaultimpl.DefaultEnvironments.init(PotionRegistry.get(DecayEffect.class));
		EnvironmentalAffinityAPI.validate();
	}
	
}
