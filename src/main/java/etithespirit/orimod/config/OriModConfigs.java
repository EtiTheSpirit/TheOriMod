package etithespirit.orimod.config;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.StringDecomposer;
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
	
	public static ForgeConfigSpec.IntValue AIR_JUMP_COUNT;
	public static ForgeConfigSpec.BooleanValue KNOW_DOUBLE_JUMP;
	public static ForgeConfigSpec.BooleanValue KNOW_DASH;
	public static ForgeConfigSpec.BooleanValue KNOW_AIR_DASH;
	public static ForgeConfigSpec.BooleanValue KNOW_WATER_DASH;
	
	public static ForgeConfigSpec.BooleanValue DEFAULT_SPIRIT_STATE;
	public static ForgeConfigSpec.BooleanValue FORCE_STATE;
	public static ForgeConfigSpec.BooleanValue ALLOW_CHANGING_BY_DEFAULT;
	
	public static ForgeConfigSpec.DoubleValue LUX_TO_RF_RATIO;
	public static ForgeConfigSpec.BooleanValue USE_ENV_FLUX;
	public static ForgeConfigSpec.BooleanValue USE_ENV_POWER;
	
	public static ForgeConfigSpec.BooleanValue ONLY_EAT_PLANTS;
	
	@Deprecated
	public static ForgeConfigSpec.DoubleValue HEALTH_TO_LUX_RATIO;
	
	@ClientUseOnly
	public static ForgeConfigSpec.BooleanValue DEBUG_RENDER_ASSEMBLIES;
	
	private static final Language ORI_MOD_LANGUAGE = earlyLoadLanguage();
	
	/**
	 * Loads the language file for this mod early so that it can be accessed.
	 * @return
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
				return (p_128132_) -> text.visit((p_177835_, p_177836_) -> StringDecomposer.iterateFormatted(p_177836_, p_177835_, p_128132_) ? Optional.empty() : FormattedText.STOP_ITERATION, Style.EMPTY).isPresent();
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
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_ONLY);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_SYNCED);
	}
	
	/**
	 * A method that takes in a builder, config category, config entry key, and default value, and automatically translates and assembles the config data.
	 * @param builder The builder for Forge configs.
	 * @param category The configuration category. This string is appended directly after "config.orimod."
	 * @param key The configuration key. This string is appended directly after the result of the category.
	 * @param defaultValue The default value for this config.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static ForgeConfigSpec.BooleanValue createBoolean(ForgeConfigSpec.Builder builder, String category, String key, boolean defaultValue) {
		String base = "config.orimod." + category + "." + key;
		String desc = base + ".description";
		String descUsingStartupLang = ORI_MOD_LANGUAGE.getOrDefault(desc);
		return builder.translation(base).comment(descUsingStartupLang).define(key, defaultValue);
	}
	
	private static ForgeConfigSpec.IntValue createIntRange(ForgeConfigSpec.Builder builder, String category, String key, int defaultValue, int min, int max) {
		String base = "config.orimod." + category + "." + key;
		String desc = base + ".description";
		String descUsingStartupLang = ORI_MOD_LANGUAGE.getOrDefault(desc);
		return builder.translation(base).comment(descUsingStartupLang).defineInRange(key, defaultValue, min, max);
	}
	
	private static ForgeConfigSpec.DoubleValue createDoubleRange(ForgeConfigSpec.Builder builder, String category, String key, double defaultValue, double min, double max) {
		String base = "config.orimod." + category + "." + key;
		String desc = base + ".description";
		String descUsingStartupLang = ORI_MOD_LANGUAGE.getOrDefault(desc);
		return builder.translation(base).comment(descUsingStartupLang).defineInRange(key, defaultValue, min, max);
	}
	
	private static void setupClientCfg() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		
		String current = "rendering";
		builder.push(current);
		DEBUG_RENDER_ASSEMBLIES = createBoolean(builder, current, "assembly_debug", false);
		
		CLIENT_ONLY = builder.build();
	}
	
	
	private static void setupServerCfg() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		String current = "spirit_management";
		builder.push(current);
		DEFAULT_SPIRIT_STATE = createBoolean(builder, current, "default_state", true);
		FORCE_STATE = createBoolean(builder, current, "force_state", false);
		ALLOW_CHANGING_BY_DEFAULT = createBoolean(builder, current, "allow_changes_default", true);
		ONLY_EAT_PLANTS = createBoolean(builder, current, "only_eat_plants", false);
		builder.pop();
		
		current = "spirit_abilities";
		builder.push(current);
		AIR_JUMP_COUNT = createIntRange(builder, current, "air_jumps", 1, 1, 2);
		KNOW_DOUBLE_JUMP = createBoolean(builder, current, "know_double_jump", false);
		KNOW_DASH = createBoolean(builder, current, "know_dash", false);
		KNOW_AIR_DASH = createBoolean(builder, current, "know_air_dash", false);
		KNOW_WATER_DASH = createBoolean(builder, current, "know_water_dash", false);
		builder.pop();
		
		current = "light_energy";
		builder.push(current);
		LUX_TO_RF_RATIO = createDoubleRange(builder, current, "rf_to_lux", 5000, 0.0001D, 100000D);
		USE_ENV_FLUX = createBoolean(builder, current, "env_flux", true);
		USE_ENV_POWER = createBoolean(builder, current, "env_power", true);
		builder.pop();
		
		SERVER_SYNCED = builder.build();
	}
	
}
