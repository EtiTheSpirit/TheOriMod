package etithespirit.orimod.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.common.block.decay.DecayLiquidBlock;
import etithespirit.orimod.common.block.decay.DecayWorldConfigBehavior;
import etithespirit.orimod.common.block.decay.IDecayBlockIdentifier;
import etithespirit.orimod.common.block.decay.world.DecaySurfaceMyceliumBlock;
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
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class OriModConfigs {
	
	public static ForgeConfigSpec CLIENT_ONLY;
	public static ForgeConfigSpec SERVER_SYNCED;
	public static ForgeConfigSpec SEPARATE_SIDES;
	
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
	
	public static ForgeConfigSpec.BooleanValue ONLY_EAT_PLANTS;
	
	protected static ForgeConfigSpec.EnumValue<DecayWorldConfigBehavior> DECAY_SPREADING;
	protected static ForgeConfigSpec.EnumValue<DecayWorldConfigBehavior> DECAY_COATING_SPREADING;
	protected static ForgeConfigSpec.EnumValue<DecayWorldConfigBehavior> DECAY_FLUID_SPREADING;
	
	public static ForgeConfigSpec.BooleanValue KEEP_CHUNKS_ALIVE;
	public static ForgeConfigSpec.BooleanValue DO_DIAGONAL_SPREAD;
	
	@Deprecated
	public static ForgeConfigSpec.DoubleValue HEALTH_TO_LUX_RATIO;
	
	@ClientUseOnly
	public static ForgeConfigSpec.BooleanValue DEBUG_RENDER_ASSEMBLIES;
	
	@ClientUseOnly
	public static ForgeConfigSpec.BooleanValue OVERRIDE_HEALTH_RENDERING;
	
	@ServerUseOnly
	public static ForgeConfigSpec.IntValue MAX_ASSEMBLY_ITERATIONS;
	
	/**
	 * Returns the spreading behavior of the given decay block. It should be a BlockState of decay or FluidState of decay.
	 * @param state
	 * @return
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
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SEPARATE_SIDES);
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
	
	private static void setupClientCfg() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		String current = "rendering";
		builder.push(current);
		DEBUG_RENDER_ASSEMBLIES = createBoolean(builder, current, "assembly_debug", false, false);
		OVERRIDE_HEALTH_RENDERING = createBoolean(builder, current, "override_health_rendering", false, false);
		
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
		LUX_TO_RF_RATIO = createDoubleRange(builder, current, "rf_to_lux", 5000, 0.0001D, 100000D, false);
		USE_ENV_POWER = createBoolean(builder, current, "env_power", true, false);
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
		SEPARATE_SIDES = builder.build();
	}
	
}
