package etithespirit.orimod.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import etithespirit.orimod.common.block.decay.DecayLiquidBlock;
import etithespirit.orimod.common.block.decay.DecayWorldConfigBehavior;
import etithespirit.orimod.common.block.decay.IDecayBlockIdentifier;
import etithespirit.orimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
import etithespirit.orimod.common.item.ISpiritLightItem;
import etithespirit.orimod.registry.FluidRegistry;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class OriModConfigs {
	
	/** Corresponds to {@link ModConfig.Type#CLIENT} -- This exists explicitly on the client and should control render options and other similar behaviors. */
	public static ForgeConfigSpec CLIENT_ONLY;
	
	/** Corresponds to {@link ModConfig.Type#SERVER} -- This exists on both dedicated and integrated servers. It synchronizes to clients upon connection. */
	public static ForgeConfigSpec SERVER_SYNCED;
	
	/** Corresponds to {@link ModConfig.Type#COMMON} -- This exists <em>independently</em> on both client and server; not replicated in multiplayer environments. */
	public static ForgeConfigSpec NOT_REPLICATED;
	
	public static ForgeConfigSpec.IntValue AIR_JUMP_COUNT;
	public static ForgeConfigSpec.BooleanValue KNOW_DOUBLE_JUMP;
	public static ForgeConfigSpec.BooleanValue KNOW_DASH;
	public static ForgeConfigSpec.BooleanValue KNOW_AIR_DASH;
	public static ForgeConfigSpec.BooleanValue KNOW_WATER_DASH;
	
	public static ForgeConfigSpec.BooleanValue DEFAULT_SPIRIT_STATE;
	public static ForgeConfigSpec.BooleanValue FORCE_STATE;
	public static ForgeConfigSpec.BooleanValue ALLOW_CHANGING_BY_DEFAULT;
	
	public static ForgeConfigSpec.DoubleValue LUX_TO_RF_RATIO;
	public static ForgeConfigSpec.BooleanValue USE_ENV_POWER;
	// public static ForgeConfigSpec.EnumValue<LightEnergyComponentProvider.ShowRFType> SHOW_RF_WHEN;
	
	public static ForgeConfigSpec.BooleanValue ONLY_EAT_PLANTS;
	public static ForgeConfigSpec.EnumValue<ISpiritLightItem.SelfRepairLimit> SELF_REPAIR_LIMITS;
	public static ForgeConfigSpec.BooleanValue ANYONE_CAN_SELF_REPAIR;
	public static ForgeConfigSpec.DoubleValue SELF_REPAIR_DAMAGE;
	public static ForgeConfigSpec.IntValue SELF_REPAIR_EARNINGS;
	
	protected static ForgeConfigSpec.EnumValue<DecayWorldConfigBehavior> DECAY_SPREADING;
	protected static ForgeConfigSpec.EnumValue<DecayWorldConfigBehavior> DECAY_COATING_SPREADING;
	protected static ForgeConfigSpec.EnumValue<DecayWorldConfigBehavior> DECAY_FLUID_SPREADING;
	
	public static ForgeConfigSpec.BooleanValue KEEP_CHUNKS_ALIVE;
	public static ForgeConfigSpec.BooleanValue DO_DIAGONAL_SPREAD;
	
	public static ForgeConfigSpec.IntValue MAX_ASSEMBLY_ITERATIONS;
	
	@ClientUseOnly
	public static ForgeConfigSpec.BooleanValue OVERRIDE_HEALTH_RENDERING;
	
	@ClientUseOnly
	/** This is not ready yet. */
	private static Map<SpiritMaterial, ForgeConfigSpec.ConfigValue<List<String>>> USER_DEFINED_SPIRIT_MATERIALS;
	
	/**
	 * Returns the spreading behavior of the given decay block. It should be a BlockState of decay or FluidState of decay.
	 * @param state The state to check.
	 * @return The default spreading behavior of this block, limited by the permissiveness of the user settings.
	 * @throws IllegalArgumentException If the given block is not a decay block.
	 */
	public static DecayWorldConfigBehavior getDecaySpreadBehavior(StateHolder<?, ?> state) throws IllegalArgumentException {
		if (state instanceof BlockState block) {
			if (block.getBlock() instanceof IDecayBlockIdentifier) {
				if (block.getBlock() instanceof DecaySurfaceMyceliumBlock) {
					if (DECAY_SPREADING.get().permissiveness < DECAY_COATING_SPREADING.get().permissiveness) {
						return DECAY_SPREADING.get();
					}
					return DECAY_COATING_SPREADING.get();
				} else if (block.getBlock() instanceof DecayLiquidBlock) {
					if (DECAY_SPREADING.get().permissiveness < DECAY_FLUID_SPREADING.get().permissiveness) {
						return DECAY_SPREADING.get();
					}
				}
				return DECAY_SPREADING.get();
			}
		} else if (state instanceof FluidState fluid) {
			if (FluidRegistry.DECAY_FLUID_STATIC.get().isSame(fluid.getType())) {
				if (DECAY_SPREADING.get().permissiveness < DECAY_FLUID_SPREADING.get().permissiveness) {
					return DECAY_SPREADING.get();
				}
				return DECAY_FLUID_SPREADING.get();
			}
		}
		throw new IllegalArgumentException("The given block is not a decay block.");
	}
	
	private static final Language ORI_MOD_LANGUAGE = earlyLoadLanguage();
	
	/**
	 * Loads the language file for this mod early so that it can be accessed.
	 * @return A copy of the language file for the ori mod, en_us.lang specifically as this is the default language.
	 */
	private static Language earlyLoadLanguage() {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		BiConsumer<String, String> put = builder::put;
		final String file = "/assets/orimod/lang/en_us.json";
		
		try {
			InputStream inputstream = OriModConfigs.class.getResourceAsStream(file);
			
			try {
				Language.loadFromJson(inputstream, put);
			} catch (Throwable err) {
				if (inputstream != null) {
					try {
						inputstream.close();
					} catch (Throwable subErr) {
						err.addSuppressed(subErr);
					}
				}
				
				throw err;
			} finally {
				inputstream.close();
			}
		} catch (JsonParseException | IOException ioexception) {
			OriMod.LOG.error("Couldn't read strings from {}", file, ioexception);
		}
		
		final Map<String, String> words = new java.util.HashMap<>(builder.build());
		return new Language() {
			public String getOrDefault(String key) {
				return words.getOrDefault(key, key);
			}
			
			public boolean has(String key) {
				return words.containsKey(key);
			}
			
			public boolean isDefaultRightToLeft() {
				return false;
			}
			
			public FormattedCharSequence getVisualOrder(FormattedText text) {
				return (sink) -> text.visit((style, content) -> StringDecomposer.iterateFormatted(content, style, sink) ? Optional.empty() : FormattedText.STOP_ITERATION, Style.EMPTY).isPresent();
			}
			
			@Override
			public Map<String, String> getLanguageData() {
				return words;
			}
		};
	}
	
	public static void initialize() {
		setupClientCfg();
		setupServerCfg();
		setupSeparatedCfg();
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_ONLY);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SYNCED);
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NOT_REPLICATED);
	}
	
	/**
	 * A method that takes in a builder, config category, config entry key, and default value, and automatically translates and assembles the config data.
	 * @param builder The builder for Forge configs.
	 * @param category The configuration category. This string is appended directly after "config.orimod."
	 * @param key The configuration key. This string is appended directly after the result of the category.
	 * @param defaultValue The default value for this config.
	 * @return
	 */
	private static ForgeConfigSpec.BooleanValue createBoolean(ForgeConfigSpec.Builder builder, String category, String key, boolean defaultValue, boolean requireRestart) {
		String base = "config.orimod." + category + "." + key;
		String desc = base + ".description";
		String descUsingStartupLang = ORI_MOD_LANGUAGE.getOrDefault(desc);
		builder = builder.translation(base).comment(descUsingStartupLang);
		if (requireRestart) builder.worldRestart();
		return builder.define(key, defaultValue);
	}
	
	private static <T extends Enum<T>> ForgeConfigSpec.EnumValue<T> createEnum(ForgeConfigSpec.Builder builder, String category, String key, T defaultValue, boolean requireRestart) {
		String base = "config.orimod." + category + "." + key;
		String desc = base + ".description";
		String descUsingStartupLang = ORI_MOD_LANGUAGE.getOrDefault(desc);
		builder = builder.translation(base).comment(descUsingStartupLang);
		if (requireRestart) builder.worldRestart();
		return builder.defineEnum(key, defaultValue);
	}
	
	private static ForgeConfigSpec.IntValue createIntRange(ForgeConfigSpec.Builder builder, String category, String key, int defaultValue, int min, int max, boolean requireRestart) {
		String base = "config.orimod." + category + "." + key;
		String desc = base + ".description";
		String descUsingStartupLang = ORI_MOD_LANGUAGE.getOrDefault(desc);
		builder = builder.translation(base).comment(descUsingStartupLang);
		if (requireRestart) builder.worldRestart();
		return builder.defineInRange(key, defaultValue, min, max);
	}
	
	private static ForgeConfigSpec.DoubleValue createDoubleRange(ForgeConfigSpec.Builder builder, String category, String key, double defaultValue, double min, double max, boolean requireRestart) {
		String base = "config.orimod." + category + "." + key;
		String desc = base + ".description";
		String descUsingStartupLang = ORI_MOD_LANGUAGE.getOrDefault(desc);
		builder = builder.translation(base).comment(descUsingStartupLang);
		if (requireRestart) builder.worldRestart();
		return builder.defineInRange(key, defaultValue, min, max);
	}
	
	@SuppressWarnings("unchecked")
	private static void setupClientCfg() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		String current = "rendering";
		builder.push(current);
		// DEBUG_RENDER_ASSEMBLIES = createBoolean(builder, current, "assembly_debug", false, false);
		OVERRIDE_HEALTH_RENDERING = createBoolean(builder, current, "override_health_rendering", false, false);
		builder.pop();
		
		/*
		builder.push("spirit_materials");
		for (SpiritMaterial mtl : SpiritMaterial.values()) {
			if (mtl == SpiritMaterial.INHERITED || mtl == SpiritMaterial.NULL || mtl.deprecated()) continue;
			String name = mtl.toString();
			builder.push(name);
			builder.comment("Blocks in this list will be added to the " + name + " material. §cRequires a full game restart due to how the registry works.");
			
			USER_DEFINED_SPIRIT_MATERIALS.put(mtl, (ForgeConfigSpec.ConfigValue<List<String>>)(Object)builder.defineListAllowEmpty(List.of(name), List::<String>of, element -> ResourceLocation.tryParse((String)element) != null));
			builder.pop();
			
		}
		builder.pop();
		*/
		
		CLIENT_ONLY = builder.build();
	}
	
	
	private static void setupServerCfg() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		String current = "spirit_management";
		builder.push(current);
		DEFAULT_SPIRIT_STATE = createBoolean(builder, current, "default_state", true, false);
		FORCE_STATE = createBoolean(builder, current, "force_state", false, false);
		ALLOW_CHANGING_BY_DEFAULT = createBoolean(builder, current, "allow_changes_default", true, false);
		ONLY_EAT_PLANTS = createBoolean(builder, current, "only_eat_plants", false, false);
		SELF_REPAIR_LIMITS = createEnum(builder, current, "self_repair_limits", ISpiritLightItem.SelfRepairLimit.NOT_ALLOWED, false);
		ANYONE_CAN_SELF_REPAIR = createBoolean(builder, current, "anyone_can_self_repair", false, false);
		SELF_REPAIR_DAMAGE = createDoubleRange(builder, current, "self_repair_damage", 3, 0, Double.POSITIVE_INFINITY, false);
		SELF_REPAIR_EARNINGS = createIntRange(builder, current, "self_repair_earnings", 50, 0, 65535, false);
		builder.pop();
		
		current = "spirit_abilities";
		builder.push(current);
		AIR_JUMP_COUNT = createIntRange(builder, current, "air_jumps", 1, 1, 2, false);
		KNOW_DOUBLE_JUMP = createBoolean(builder, current, "know_double_jump", false, false);
		KNOW_DASH = createBoolean(builder, current, "know_dash", false, false);
		KNOW_AIR_DASH = createBoolean(builder, current, "know_air_dash", false, false);
		KNOW_WATER_DASH = createBoolean(builder, current, "know_water_dash", false, false);
		builder.pop();
		
		current = "light_energy";
		builder.push(current);
		LUX_TO_RF_RATIO = createDoubleRange(builder, current, "rf_to_lux", 100, 0.0001D, 100000D, false);
		USE_ENV_POWER = createBoolean(builder, current, "env_power", true, false);
		// SHOW_RF_WHEN = createEnum(builder, current, "show_rf", LightEnergyComponentProvider.ShowRFType.ONLY_WHEN_SNEAKING, false);
		builder.pop();
		
		current = "world.decay";
		builder.push(current);
		DECAY_SPREADING = createEnum(builder, current, "spreading", DecayWorldConfigBehavior.ALLOW_SPREADING, false);
		DECAY_COATING_SPREADING = createEnum(builder, current, "surface_coating", DecayWorldConfigBehavior.ALLOW_SPREADING, false);
		DECAY_FLUID_SPREADING = createEnum(builder, current, "fluid", DecayWorldConfigBehavior.ALLOW_SPREADING, false);
		DO_DIAGONAL_SPREAD = createBoolean(builder, current, "diagonal_spread", true, false);
		builder.pop();
		
		current = "assembly_optimization_system";
		builder.push(current);
		KEEP_CHUNKS_ALIVE = createBoolean(builder, current, "keep_chunks_alive", true, false);
		MAX_ASSEMBLY_ITERATIONS = createIntRange(builder, current, "max_assembly_iterations", 256, 4, 16384, false);
		builder.pop();
		
		SERVER_SYNCED = builder.build();
	}
	
	private static void setupSeparatedCfg() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		/*
		String current = "assembly_optimization_system";
		builder.push(current);
		GREEDY_ASSEMBLY_OPTIMIZATION = createBoolean(builder, current, "greedy_optimization", true, true);
		builder.pop();
		*/
		NOT_REPLICATED = builder.build();
	}
	
}
