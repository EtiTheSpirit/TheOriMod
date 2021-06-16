package etithespirit.etimod;

import etithespirit.etimod.client.player.spiritbehavior.SpiritSounds;
import etithespirit.etimod.common.block.UpdateHelper;
import etithespirit.etimod.registry.*;
import etithespirit.etimod.util.profiling.UniProfiler;
import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import etithespirit.datagen.GenerateBlockModels;
import etithespirit.datagen.GenerateItemModels;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.networking.status.ReplicateEffect;
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

@Mod(value=EtiMod.MODID)
public final class EtiMod {
		
    public static final String MODID = "etimod";
    public static final ResourceLocation RSRC = new ResourceLocation(MODID);
    
    public static EtiMod INSTANCE;
    
    private static boolean isModLoadingComplete = false;
    
    public static final Logger LOG = LogManager.getLogger();
    
    public EtiMod() {
    	INSTANCE = this;
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::dedicatedServerInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataGenerated);
    	
    	MinecraftForge.EVENT_BUS.addListener(this::commandInit);
	    MinecraftForge.EVENT_BUS.addListener(this::anyServerInit);
	    
    	MinecraftForge.EVENT_BUS.register(this);
    	
    	EntityRegistry.registerAll();
    	SoundRegistry.registerAll();
    	PotionRegistry.registerAll();
    	BlockRegistry.registerAll();
    	TileEntityRegistry.registerAll();
    	ItemRegistry.registerAll();
    	BiomeRegistry.registerAll();
    	DimensionRegistry.registerAll();
    }
    
    /**
     * Whether or not the forge mod loading cycle has completed.
     */
    public static boolean forgeLoadingComplete() {
    	return isModLoadingComplete;
    }
    
	public void commonInit(final FMLCommonSetupEvent event) {
    	MinecraftForge.EVENT_BUS.register(SpiritSounds.class);
    	MinecraftForge.EVENT_BUS.addListener(EntityAttributeMarshaller::onAttributesRegistered);
    	MinecraftForge.EVENT_BUS.addListener(UpdateHelper::onBlockChanged);
    	
		event.enqueueWork(() -> {
			Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(EtiMod.MODID, "light_forest_chunkgen"), LightForestChunkGenerator.CORE_CODEC);
			Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(EtiMod.MODID, "light_forest"), LightForestBiomeProvider.BIOME_CODEC);
		});
	}
    
    public void clientInit(final FMLClientSetupEvent event) {
    	UniProfiler.setProfiler(Minecraft.getInstance().getProfiler(), Dist.CLIENT);
    	
    	MinecraftForge.EVENT_BUS.register(etithespirit.etimod.client.player.spiritbehavior.SpiritDash.class);
    	
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onEntityJumped);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onKeyPressed);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onPlayerTicked);
	
	    MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onPlayerTickedClient);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onGetEntitySize);
    	
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.RenderPlayerAsSpirit::whenRenderingPlayer);
		MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.gui.CustomHealthForEffects::onElementDrawn);
	    // MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.routine.Recursor::onRenderTick);
    	
    	RenderRegistry.registerAll();    	
    	ReplicateMorphStatus.registerPackets(Dist.CLIENT);
    	ReplicateEffect.registerPackets(Dist.CLIENT);
    	ClientRegistry.registerKeyBinding(etithespirit.etimod.client.player.spiritbehavior.SpiritDash.DASH_BIND);
    	ClientRegistry.registerKeyBinding(etithespirit.etimod.client.player.spiritbehavior.SpiritJump.CLING_BIND);
    }
    
    public void dedicatedServerInit(final FMLDedicatedServerSetupEvent event) {
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onGetEntitySize);
	    MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onPlayerTickedServer);
    	
    	ReplicateMorphStatus.registerPackets(Dist.DEDICATED_SERVER);
    	ReplicateEffect.registerPackets(Dist.DEDICATED_SERVER);
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