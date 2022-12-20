package etithespirit.orimod.datagen.audio;

import com.google.common.hash.HashCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.OriMod;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom and rather hacky "data generator" (not in the traditional Minecraft sense!) that generates sounds.json based on some completely custom
 * and arbitrary tomfoolery.
 *
 * The actual reason is that I change and add sounds a LOT. And this mod has a lot of sounds (it makes up most of the mod's size!)
 * so being able to quickly organize and track down sounds in the corresponding directories rather than a huge mishmash of everything at once
 * is very useful.
 */
public final class GenerateSoundsJson implements DataProvider {
	
	// TODO: Use ExistingFileHelper!
	
	protected static final ExistingFileHelper.ResourceType SOUND = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "sounds");
	
	private final String modid;
	private final File srcMain;
	private final File srcGenerated;
	private final Logger log;
	private final Map<String, List<String>> allRsrcsByCategory = new HashMap<>();
	private final Map<String, String> audioToMinecraftCategory = new HashMap<>();
	private boolean reportedffprobeMissing = false;
	private boolean validateChannels = false;
	
	/**
	 * Create a sound json generator for the given mod using the given source folder for the repository.
	 * @param modid The ID of the mod to generate for.
	 */
	public GenerateSoundsJson(String modid) {
		this(modid, "main", "generated");
	}
	
	public GenerateSoundsJson(String modid, String mainFolderName, String generatedFolderName) {
		if (!GeneralUtils.IS_DEV_ENV) throw new IllegalCallerException("This can only be called in a development environment!");
		File modProjectRoot = new File(".").getAbsoluteFile().getParentFile().getParentFile();
		String srcFolder = new File(modProjectRoot, "src").getAbsolutePath();
		
		this.modid = modid;
		this.log = LogManager.getLogger(modid);
		this.srcMain = new File(srcFolder, mainFolderName);
		this.srcGenerated = new File(srcFolder, generatedFolderName);
	}
	
	/**
	 * Returns whether or not the given category exists.
	 * @param category The category to check.
	 * @return Whether or not this category exists.
	 */
	private static boolean isValidAudioCategory(String category) {
		for (SoundSource src : ALL_SOUND_SRC) {
			if (src.getName().equalsIgnoreCase(category)) {
				return true;
			}
		}
		return false;
	}
	private static final SoundSource[] ALL_SOUND_SRC = SoundSource.values();
	
	/**
	 * Enables the audio channel validator, which ensures all audio files are mono and warns for those that aren't. This can increase the amount
	 * of time this data generator takes to execute dramatically, so only use it when adding new audio.
	 * @return This, for chaining.
	 */
	public GenerateSoundsJson setValidatesChannels() {
		this.validateChannels = true;
		return this;
	}
	
	/**
	 * Returns whether or not the given file has the given extension. The extension should begin with a dot.
	 * @param file The file to check.
	 * @param ext The extension to check.
	 * @return True if the file's name ends with the given extension.
	 */
	private static boolean isExtension(File file, String ext) {
		if (!file.isFile()) throw new IllegalArgumentException("Directories cannot have extensions.");
		if (!ext.startsWith(".")) throw new IllegalArgumentException("The extension string must start with a dot.");
		return file.getName().endsWith(ext);
	}
	
	private String asResource(String clipped) {
		if (clipped.toLowerCase().endsWith(".ogg")) {
			clipped = clipped.substring(0, clipped.length() - 4);
		}
		return modid + ":" + clipped.replace('\\', '/');
	}
	
	/**
	 * Uses ffprobe (assuming it is installed in the system PATH) to return the number of channels in an audio file.
	 * @param file The file to check.
	 * @return The number of channels in the file, or 0 if an exception occurred.
	 */
	private int getNumberOfAudioChannels(File file) {
		if (!validateChannels) return 1;
		try {
			if (file.exists() && file.isFile()) {
				Process process = Runtime.getRuntime().exec(String.format("ffprobe -show_entries stream=channels -of compact=p=0:nk=1 -v 0 \"%s\"", file.getAbsolutePath()));
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String value = reader.readLine();
				int ret = Integer.parseInt(value);
				reader.close();
				return ret;
			}
		} catch (IOException ioe) {
			if (!reportedffprobeMissing) {
				reportedffprobeMissing = true;
				log.error("Failed to execute ffprobe! If you are building this mod as a third party, consider downloading ffmpeg and adding its directory to your PATH. ffprobe is used for error correction by reporting an invalid number of audio channels in .ogg files, which breaks Minecraft's attenuation. Coupled with ffmpeg, the system can automatically repair audio files.");
				log.error(ioe);
			}
		} catch (NumberFormatException fmt) {
			log.error("Failed to read output from ffprobe when parsing " + file.getAbsolutePath());
			log.error(fmt);
		}
		return 0;
	}
	
	private void getAllFoldersWithAudio(File inDir) throws Exception {
		if (!inDir.isDirectory()) throw new IllegalArgumentException("Cannot search a file for subfolders. Please input a directory!");
		File[] files = inDir.listFiles();
		File procgen = null;
		Map<File, String> fileToRsrc = new HashMap<>();
		List<String> audioFileRsrcs = Lists.newArrayList();
		for (File sub : files) {
			if (sub.isFile()) {
				if (isExtension(sub, ".ogg")) {
					//audioFiles.add(sub);
					String pathEnding = "resources\\assets\\" + modid + "\\sounds";
					int end = sub.getAbsolutePath().lastIndexOf(pathEnding);
					if (end != -1) {
						end += pathEnding.length() + 1;
						String rsrc = asResource(sub.getAbsolutePath().substring(end));
						audioFileRsrcs.add(rsrc);
						fileToRsrc.put(sub, rsrc);
						int channelCount = getNumberOfAudioChannels(sub);
						if (channelCount > 1) {
							log.warn("Audio file {} is not a mono audio file! If this audio file is intended for use in 3D (it is not a GUI sound or clientside sound), it will emit across the entire Level. You have been warned!", sub.getAbsolutePath());
						}
					}
				} else if (sub.getName().equalsIgnoreCase("procgen_info.json")) {
					procgen = sub;
				}
			} else {
				getAllFoldersWithAudio(sub);
			}
		}
		
		if (!audioFileRsrcs.isEmpty()) {
			if (procgen == null) {
				log.warn("Directory " + inDir.getAbsolutePath() + " contains .ogg files but no procgen_info.json! It will be ignored when generating sounds.json.");
			} else {
				parseAudioData(procgen, audioFileRsrcs, fileToRsrc);
			}
		}
	}
	
	private void parseAudioData(File procgenInfoJson, List<String> audioFileRsrcs, Map<File, String> audioFileToRsrc) throws Exception {
		Map<String, String> audioFileToCategory = new HashMap<>();
		FileReader procgenReader = new FileReader(procgenInfoJson);
		JsonObject fileJson = JsonParser.parseReader(procgenReader).getAsJsonObject();
		boolean hasSingleAudioPath = fileJson.has("audioName");
		boolean hasMultiAudioPath = fileJson.has("audioNames");
		
		if (hasSingleAudioPath && hasMultiAudioPath) {
			throw new JsonSyntaxException("A procedural generation json file for audio (" + procgenInfoJson.getAbsolutePath() + ") defined both audioName and audioNames. You must define either one or the other. Do not define both!");
		}
		if (hasSingleAudioPath == hasMultiAudioPath) {
			throw new JsonSyntaxException("A procedural generation json file for audio (" + procgenInfoJson.getAbsolutePath() + ") defined neither audioName nor audioNames. One of these two must be defined to generate audio.");
		}
		
		boolean hasCategory = fileJson.has("category");
		if (!hasCategory) throw new JsonSyntaxException("A procedural generation json file for audio (" + procgenInfoJson.getAbsolutePath() + ") was missing its category field!");
		
		String minecraftCategory = fileJson.get("category").getAsString();
		if (!isValidAudioCategory(minecraftCategory)) {
			throw new JsonSyntaxException("A procedural generation json file for audio (" + procgenInfoJson.getAbsolutePath() + ") has an invalid category field! It does not correspond to one of Minecraft's SoundSource enums.");
		}
		String categoryLower = minecraftCategory.toLowerCase();
		if (!categoryLower.equals(minecraftCategory)) {
			log.warn("The given sound category (" + minecraftCategory + ") had capital letters in it! (File: " + procgenInfoJson.getAbsolutePath() + ")");
		}
		minecraftCategory = categoryLower;
		
		if (hasSingleAudioPath) {
			String audioName = fileJson.get("audioName").getAsString();
			for (String resource : audioFileRsrcs) {
				audioFileToCategory.put(resource, audioName);
			}
			audioToMinecraftCategory.put(audioName, minecraftCategory);
		} else {
			File parent = audioFileToRsrc.keySet().stream().findFirst().get().getParentFile();
			File[] allFiles = parent.listFiles();
			JsonObject audioPaths = fileJson.get("audioNames").getAsJsonObject();
			for (String registryCtr : audioPaths.keySet()) {
				JsonArray files = audioPaths.getAsJsonArray(registryCtr);
				for (JsonElement element : files) {
					String fileName = element.getAsString();
					File equalFile = null;
					for (File allFile : allFiles) {
						if (allFile.getName().equalsIgnoreCase(fileName)) {
							equalFile = allFile;
							break;
						}
					}
					if (equalFile == null)
						throw new FileNotFoundException("Failed to locate " + fileName + " in directory " + parent.getAbsolutePath());
					if (audioFileToCategory.containsKey(audioFileToRsrc.get(equalFile))) {
						throw new IllegalStateException("Audio file " + equalFile.getName() + " has already been registered to " + audioFileToRsrc.get(equalFile) + " in this json file (" + procgenInfoJson.getAbsolutePath() + ")");
					}
					audioFileToCategory.put(audioFileToRsrc.get(equalFile), registryCtr);
				}
			}
		}
		
		for (String rsrc : audioFileToCategory.keySet()) {
			String cat = audioFileToCategory.get(rsrc);
			List<String> entries = allRsrcsByCategory.computeIfAbsent(cat, k -> new ArrayList<>());
			if (entries.contains(rsrc)) {
				throw new IllegalStateException("Audio file " + rsrc + " has already been registered to " + cat + " (duplicate exists in " + procgenInfoJson.getAbsolutePath() + ")");
			}
			entries.add(rsrc);
			if (audioToMinecraftCategory.containsKey(rsrc)) {
				String mcCat = audioToMinecraftCategory.get(rsrc);
				if (!minecraftCategory.equals(mcCat)) {
					log.warn("Something is going to override the Minecraft Sound Category of " + cat + " (from "+mcCat+" to "+minecraftCategory+")");
				}
			}
			audioToMinecraftCategory.put(cat, minecraftCategory);
		}
	}
	
	private void addToJson(JsonObject obj) {
		for (String category : allRsrcsByCategory.keySet()) {
			List<String> entries = allRsrcsByCategory.get(category);
			if (entries.isEmpty()) continue;
			String firstRsrc = entries.stream().findFirst().get();
			
			JsonObject soundObj = new JsonObject();
			soundObj.addProperty("category", audioToMinecraftCategory.getOrDefault(firstRsrc, "master"));
			JsonArray arr = new JsonArray();
			for (String entry : entries) {
				arr.add(entry);
			}
			soundObj.add("sounds", arr);
			
			obj.add(category, soundObj);
		}
	}
	
	@Override
	public void run(@Nonnull CachedOutput pCache) {
		try {
			log.info("Note: This generator is custom! Do not report issues with it to Mojang or the Forge team! It has been designed by Xan explicitly for The Ori Mod.");
			if (validateChannels) log.warn("The data generator has been instructed to verify that all audio files are mono. This will dramatically increase the time it takes to execute!");
			log.info("Iterating sounds folder...");
			getAllFoldersWithAudio(new File(srcMain, "resources/assets/" + modid + "/sounds"));
			JsonObject soundJson = new JsonObject();
			addToJson(soundJson);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			File out = new File(srcGenerated, "resources/assets/" + modid + "/sounds.json");
			String json = gson.toJson(soundJson);
			
			pCache.writeIfNeeded(out.toPath(), json.getBytes(StandardCharsets.UTF_8), HashCode.fromInt(json.hashCode()));
			
		} catch (Exception exc) {
			// lmao
			throw new RuntimeException(exc);
		}
	}
	
	/**
	 * Gets a name for this provider, to use in logging.
	 */
	@Override
	public @Nonnull String getName() {
		return "SoundJsonGenerator";
	}
	
}
