package etithespirit.etimod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import etithespirit.datagen.GenerateBlockModels;
import etithespirit.datagen.GenerateItemModels;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.networking.status.ReplicateEffect;
import etithespirit.etimod.registry.BiomeRegistry;
import etithespirit.etimod.registry.BlockRegistry;
import etithespirit.etimod.registry.DimensionRegistry;
import etithespirit.etimod.registry.EntityRegistry;
import etithespirit.etimod.registry.ItemRegistry;
import etithespirit.etimod.registry.PotionRegistry;
import etithespirit.etimod.registry.RenderRegistry;
import etithespirit.etimod.registry.SoundRegistry;
import etithespirit.etimod.registry.TileEntityRegistry;
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
public class EtiMod {
		
    public static final String MODID = "etimod";
    public static final ResourceLocation RSRC = new ResourceLocation(MODID);
    
    public static EtiMod INSTANCE;
    
    private static boolean isModLoadingComplete = false;
    
    public static final Logger LOG = LogManager.getLogger();
    
    public EtiMod() {
    	INSTANCE = this;
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverInit);
    	// FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    	// FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onDataGenerated);
    	
    	MinecraftForge.EVENT_BUS.addListener(this::commandInit);
    	MinecraftForge.EVENT_BUS.register(this);
    	//MinecraftForge.EVENT_BUS.register(Configuration.class);
    	//MinecraftForge.EVENT_BUS.register(SpiritMarshaller.class);
    	
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
    	MinecraftForge.EVENT_BUS.register(etithespirit.etimod.client.player.spiritbehavior.SpiritSounds.class);
    	
		event.enqueueWork(() -> {
			Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation(EtiMod.MODID, "light_forest_chunkgen"), LightForestChunkGenerator.CORE_CODEC);
			Registry.register(Registry.BIOME_SOURCE, new ResourceLocation(EtiMod.MODID, "light_forest"), LightForestBiomeProvider.BIOME_CODEC);
		});
	}
    
    public void clientInit(final FMLClientSetupEvent event) {
    	MinecraftForge.EVENT_BUS.register(etithespirit.etimod.client.player.spiritbehavior.SpiritDash.class);
    	
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onEntityJumped);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onKeyPressed);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onPlayerTicked);
    	
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onPlayerTicked);
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
    
    public void serverInit(final FMLDedicatedServerSetupEvent event) {
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onGetEntitySize);
    	
    	ReplicateMorphStatus.registerPackets(Dist.DEDICATED_SERVER);
    	ReplicateEffect.registerPackets(Dist.DEDICATED_SERVER);
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