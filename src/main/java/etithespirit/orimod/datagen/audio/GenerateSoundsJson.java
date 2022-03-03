package etithespirit.orimod.datagen.audio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import etithespirit.orimod.GeneralUtils;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.sounds.SoundSource;
import org.apache.commons.compress.utils.Lists;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A custom and rather hacky "data generator" (not in the traditional Minecraft sense!) that generates sounds.json based on some completely custom
 * and arbitrary tomfoolery.
 */
public final class GenerateSoundsJson implements DataProvider {
	
	private final String modid;
	private final File srcMain;
	private final File srcGenerated;
	//private final List<AudioDirectory> audioDirectories = Lists.newArrayList();
	private final Logger log;
	private final Map<String, List<String>> allRsrcsByCategory = new HashMap<>();
	private final Map<String, String> audioCategoryToMinecraftCategory = new HashMap<>();
	
	/**
	 * Create a sound json generator for the given mod using the given source folder for the repository.
	 * @param modid The ID of the mod to generate for.
	 * @param srcFolder The source folder. For obvious reasons, this is only usable in an IDE.
	 */
	public GenerateSoundsJson(String modid, String srcFolder) {
		this(modid, srcFolder, "main", "generated");
	}
	
	public GenerateSoundsJson(String modid, String srcFolder, String mainFolderName, String generatedFolderName) {
		if (!GeneralUtils.IS_DEV_ENV) throw new IllegalCallerException("This can only be called in a development environment!");
		
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
				log.error("Directory " + inDir.getAbsolutePath() + " contains .ogg files but no procgen_info.json! It will be ignored when generating sounds.json.");
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
			String cat = fileJson.get("audioName").getAsString();
			for (String f : audioFileRsrcs) {
				audioFileToCategory.put(f, cat);
			}
			audioCategoryToMinecraftCategory.put(cat, minecraftCategory);
		} else {
			File parent = audioFileToRsrc.keySet().stream().findFirst().get().getParentFile();
			File[] allFiles = parent.listFiles();
			JsonObject audioPaths = fileJson.get("audioNames").getAsJsonObject();
			for (String registryCtr : audioPaths.keySet()) {
				JsonArray files = audioPaths.getAsJsonArray(registryCtr);
				for (JsonElement element : files) {
					String fileName = element.getAsString();
					File equalFile = null;
					for (int idx = 0; idx < allFiles.length; idx++) {
						if (allFiles[idx].getName().equalsIgnoreCase(fileName)) {
							equalFile = allFiles[idx];
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
			if (audioCategoryToMinecraftCategory.containsKey(rsrc)) {
				String mcCat = audioCategoryToMinecraftCategory.get(rsrc);
				if (!minecraftCategory.equals(mcCat)) {
					log.warn("Something is going to override the Minecraft Sound Category of " + cat + " (from "+mcCat+" to "+minecraftCategory+")");
				}
			}
			audioCategoryToMinecraftCategory.put(cat, minecraftCategory);
		}
	}
	
	private void addToJson(JsonObject obj) {
		for (String category : allRsrcsByCategory.keySet()) {
			List<String> entries = allRsrcsByCategory.get(category);
			if (entries.isEmpty()) continue;
			String firstRsrc = entries.stream().findFirst().get();
			
			JsonObject soundObj = new JsonObject();
			soundObj.addProperty("category", audioCategoryToMinecraftCategory.getOrDefault(firstRsrc, "master"));
			JsonArray arr = new JsonArray();
			for (String entry : entries) {
				arr.add(entry);
			}
			soundObj.add("sounds", arr);
			
			obj.add(category, soundObj);
		}
	}
	
	@Override
	public void run(HashCache pCache) {
		try {
			getAllFoldersWithAudio(new File(srcMain, "resources/assets/" + modid + "/sounds"));
			JsonObject soundJson = new JsonObject();
			addToJson(soundJson);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			File out = new File(srcGenerated, "resources/assets/" + modid + "/sounds.json");
			JsonWriter writer = gson.newJsonWriter(new FileWriter(out));
			writer.jsonValue(gson.toJson(soundJson));
			writer.flush();
			writer.close();
		} catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}
	
	/**
	 * Gets a name for this provider, to use in logging.
	 */
	@Override
	public String getName() {
		return "SoundJsonGenerator";
	}
	
	/*
	private static class AudioDirectory {
		
		private final Logger log;
		
		public final File procgenInfoJson;
		
		public final List<String> audioFileRsrcs;
		
		private final Map<String, String> audioFileToCategory = new HashMap<>();
		
		private final Map<File, String> audioFileToRsrc;
		
		private String category;
		
		public AudioDirectory(Logger log, File procgen, List<String> audioFileRsrcs, Map<File, String> fileToRsrc) throws Exception {
			this.log = log;
			this.procgenInfoJson = procgen;
			this.audioFileRsrcs = audioFileRsrcs;
			this.audioFileToRsrc = fileToRsrc;
			setupAudioFileToCategory();
		}
		
		private void setupAudioFileToCategory() throws Exception {
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
			
			String category = fileJson.get("category").getAsString();
			if (!isValidAudioCategory(category)) {
				throw new JsonSyntaxException("A procedural generation json file for audio (" + procgenInfoJson.getAbsolutePath() + ") has an invalid category field! It does not correspond to one of Minecraft's SoundSource enums.");
			}
			String categoryLower = category.toLowerCase();
			if (!categoryLower.equals(category)) {
				log.warn("The given sound category (" + category + ") had capital letters in it! (File: " + procgenInfoJson.getAbsolutePath() + ")");
			}
			this.category = categoryLower;
			
			if (hasSingleAudioPath) {
				String audioPath = fileJson.get("audioName").getAsString();
				for (String f : audioFileRsrcs) {
					audioFileToCategory.put(f, audioPath);
				}
			} else {
				File parent = audioFileToRsrc.keySet().stream().findFirst().get().getParentFile();
				File[] allFiles = parent.listFiles();
				JsonObject audioPaths = fileJson.get("audioNames").getAsJsonObject();
				for (String registryCtr : audioPaths.keySet()) {
					JsonArray files = audioPaths.getAsJsonArray(registryCtr);
					for (JsonElement element : files) {
						String fileName = element.getAsString();
						File equalFile = null;
						for (int idx = 0; idx < allFiles.length; idx++) {
							if (allFiles[idx].getName().equalsIgnoreCase(fileName)) {
								equalFile = allFiles[idx];
								break;
							}
						}
						if (equalFile == null)
							throw new FileNotFoundException("Failed to locate " + fileName + " in directory " + parent.getAbsolutePath());
						if (audioFileToCategory.containsKey(audioFileToRsrc.get(equalFile))) {
							throw new IllegalStateException("Audio file " + equalFile.getName() + " has already been registered to " + audioFileToRsrc.get(equalFile) + " (duplicate defined in: " + procgenInfoJson.getAbsolutePath() + ")");
						}
						audioFileToCategory.put(audioFileToRsrc.get(equalFile), registryCtr);
					}
				}
			}
		}
		
		public void addToJson(JsonObject obj) {
			Map<String, List<String>> byCategory = new HashMap<>();
			for (String rsrc : audioFileToCategory.keySet()) {
				String cat = audioFileToCategory.get(rsrc);
				List<String> entries = byCategory.computeIfAbsent(cat, k -> new ArrayList<>());
				entries.add(rsrc);
			}
			
			for (String category : byCategory.keySet()) {
				List<String> entries = byCategory.get(category);
				
				JsonObject soundObj = new JsonObject();
				soundObj.addProperty("category", category);
				JsonArray arr = new JsonArray();
				for (String entry : entries) {
					arr.add(entry);
				}
				soundObj.add("sounds", arr);
				
				obj.add(category, soundObj);
			}
		}
		
	}
*/
}
