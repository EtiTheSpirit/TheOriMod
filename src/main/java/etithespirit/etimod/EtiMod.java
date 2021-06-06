package etithespirit.etimod;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;

import etithespirit.etimod.common.command.SetSpiritCommand;
//import etithespirit.etimod.config.Configuration;
import etithespirit.etimod.datagen.GenerateBlockModels;
import etithespirit.etimod.datagen.GenerateItemModels;
import etithespirit.etimod.imc.IMCRegistryError;
import etithespirit.etimod.imc.IMCStatusContainer;
import etithespirit.etimod.imc.SoftAPIBrancher;
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
import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.blockmtl.BlockToMaterialBinding;
import etithespirit.etimod.world.dimension.LightForestBiomeProvider;
import etithespirit.etimod.world.dimension.LightForestChunkGenerator;
import net.minecraft.command.CommandSource;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.InterModComms.IMCMessage;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.ModList;

@Mod(value=EtiMod.MODID)
public class EtiMod {
		
    public static final String MODID = "etimod";
    public static final ResourceLocation RSRC = new ResourceLocation(MODID);
    
    public static EtiMod INSTANCE;
    
    public static final Logger LOG = LogManager.getLogger();
    
    public EtiMod() {
    	INSTANCE = this;
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientInit);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverInit);
    	// FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
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
    
	public void commonInit(final FMLCommonSetupEvent event) {
    	MinecraftForge.EVENT_BUS.register(etithespirit.etimod.client.player.spiritbehavior.SpiritSounds.class);
    	
		event.enqueueWork(() -> {
			Registry.register(Registry.CHUNK_GENERATOR_CODEC, new ResourceLocation(EtiMod.MODID, "light_forest_chunkgen"), LightForestChunkGenerator.CORE_CODEC);
			Registry.register(Registry.BIOME_PROVIDER_CODEC, new ResourceLocation(EtiMod.MODID, "light_forest"), LightForestBiomeProvider.BIOME_CODEC);
		});
	}
    
    public void clientInit(final FMLClientSetupEvent event) {
    	MinecraftForge.EVENT_BUS.register(etithespirit.etimod.client.player.spiritbehavior.SpiritDash.class);
    	
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onEntityJumped);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onKeyPressed);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritJump::onPlayerTicked);
    	
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onPlayerTicked);
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.player.spiritbehavior.SpiritSize::onGetEntitySize);
    	
    	MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.render.RenderPlayerAsSpirit::whenRenderingPlayer);
		MinecraftForge.EVENT_BUS.addListener(etithespirit.etimod.client.gui.CustomHealthForEffects::onElementDrawn);
    	
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
    
    public void enqueueIMC(final InterModEnqueueEvent event) {
    	if (!EtiUtils.IS_DEV_ENV) return;
    	
    	net.minecraftforge.fml.InterModComms.sendTo(null, "etimod", "registerCustomMaterialSound", () -> { return "net.minecraft.block.material.Material.ROCK|STONE"; });
    }

	public void processIMC(final InterModProcessEvent event) {
		Stream<IMCMessage> messageStream = event.getIMCStream();
		messageStream.forEach(message -> {
			String method = message.getMethod();
			IMCStatusContainer status;
			String args = message.getMessageSupplier().get().toString();
			String sender = message.getSenderModId();
			String userFriendlySender = sender;
			if (sender == null || sender.length() == 0) {
				LOG.error("Discarding invalid IMC call. Reason: Sender mod ID is null. [imcMethod={}, args={}]", String.valueOf(method), String.valueOf(args));
				return;
			} else if (args == null) {
				status = new IMCStatusContainer(IMCRegistryError.FAILURE_NULL_ARGS);
			} else {
				Optional<? extends ModContainer> container = ModList.get().getModContainerById(sender);
				if (!container.isPresent() && !sender.equals("minecraft")) {
					LOG.error("Discarding invalid IMC call. Reason: Sender mod ID is bogus. [imcMethod={}, args={}]", String.valueOf(method), String.valueOf(args));
					return;
				} else if (sender.equals(MODID) && !EtiUtils.IS_DEV_ENV) {
					LOG.error("Discarding invalid IMC call. Reason: Attempt to call IMC with a mod ID of {} [imcMethod={}, args={}]", MODID, String.valueOf(method), String.valueOf(args));
					return;
				} else {
					// Below: Guaranteed to exist.
					userFriendlySender = EtiUtils.getModName(sender);
					
					if (method.equals("registerSpiritStepSound")) {
						status = BlockToMaterialBinding.IMC_TrySetEffectiveMaterialFor(args);
					} else if (method.equals("registerCustomMaterialSound")) {
						status = BlockToMaterialBinding.IMC_TrySetSoundForMaterial(args);
					} else if (method.equals("setUseIfIn")) {
						status = BlockToMaterialBinding.IMC_SetUseIfIn(args);
					} else if (method.equals("requestGenericAPI")) {
						InterModComms.sendTo(sender, MODID + "-sendGenericAPI", () -> {
							return SoftAPIBrancher.getGenericInterface();
							// TODO: Is this a good idea? 
							// TODO: ^ Let other mods access a BiFunction<String, Object[], Object> for agnostic design
							// TODO: ^ without the need to install an API, but at the cost of a 
							// TODO: ^ clunky feel and potential for general cluelessness as to what is being done.
						});
						status = IMCStatusContainer.SUCCESS;
					} else if (method.equals("requestComplexMaterialRegistry")) {
						InterModComms.sendTo(sender, MODID + "-sendComplexMaterialRegistry", () -> {
							return SoftAPIBrancher.getComplexMaterialRegistry();
						});
						status = IMCStatusContainer.SUCCESS;
					} else {
						status = new IMCStatusContainer(IMCRegistryError.FAILURE_INVALID_METHOD, String.valueOf(method));
					}
				}
			}
			if (status.isSuccess) {
				LOG.info("{} ({}) successfully performed IMC method {} with args {}", userFriendlySender, sender, method, args);
			} else {
				status.throwIMCException(userFriendlySender, args);
			}
		});
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
}