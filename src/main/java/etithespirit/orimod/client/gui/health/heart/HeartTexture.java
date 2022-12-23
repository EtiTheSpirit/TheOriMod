package etithespirit.orimod.client.gui.health.heart;

import etithespirit.orimod.OriMod;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.fml.loading.FMLEnvironment;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Specifically designed just for heart textures. Contains the texture and the size of the atlas.
 */
public record HeartTexture(ResourceLocation texture, int sizeX, int sizeY, boolean hasHalfVariant, boolean supportsBlinking, boolean hasHardcoreVariant) {
	
	private static final String HEARTS_FOLDER = "textures/gui/hearts/";
	private static final List<HeartTexture> LOOKUP = new ArrayList<>();
	
	/**
	 * <strong>STRICTLY FOR USE IN IDE ONLY.</strong> This validates that all known instances (see {@link #validated(ResourceLocation, int, int)}) have the resolution they claim they should have.
	 * @throws AssertionError If the resolution is mismatched, the texture is missing, or another IO error occurs.
	 */
	public static void validateCorrectTextureResolution() {
		if (FMLEnvironment.production) {
			throw new IllegalStateException("Texture resolution validation is strictly IDE only! This should never be called in a production environment. If you are seeing this as a normal mod user, this is a serious bug!");
		}
		
		for (HeartTexture tex : LOOKUP) {
			Optional<Resource> rsrc = Minecraft.getInstance().getResourceManager().getResource(tex.texture);
			if (rsrc.isPresent()) {
				try {
					InputStream stream = rsrc.get().open();
					BufferedImage image = ImageIO.read(stream);
					
					int targetWidth = tex.sizeX;
					int targetHeight = tex.sizeY;
					int realWidth = image.getWidth();
					int realHeight = image.getHeight();
					
					if (targetWidth != realWidth || targetHeight != realHeight) {
						throw new AssertionError("HeartTexture failure: Resource [" + tex.texture + "] failed to validate: Mismatched width/height! Texture called a constructor that requires the image resolution to be [" + targetWidth + ',' + targetHeight + "] but the real resolution is [" + realWidth + ',' + realHeight + "]!");
					}
					OriMod.LOG.info("HeartTexture {} validated successfully (resolution is correct)!", tex.texture);
					stream.close();
				} catch (IOException ioErr) {
					throw new AssertionError(ioErr);
				}
			} else {
				throw new AssertionError("HeartTexture failure: No such resource " + tex.texture);
			}
		}
	}
	
	public static HeartTexture fromWholeOnly(ResourceLocation texture) {
		return validated(texture, 9, 9);
	}
	
	/**
	 * Alias method to construct an instance using a texture only containing the full and half heart without a hardcore nor flashing variant.
	 * @param texture
	 * @return
	 */
	public static HeartTexture fromSingleAndHalf(ResourceLocation texture) {
		return validated(texture, 18, 9);
	}
	
	/**
	 * Alias method to construct an instance using a texture only containing the full and half heart with a hardcore variant, but no flashing variant.
	 * @param texture
	 * @return
	 */
	public static HeartTexture fromHeartHardcore(ResourceLocation texture) {
		return validated(texture, 18, 18);
	}
	
	/**
	 * Alias method to construct an instance using a texture only containing the full and half heart with a flashing variant, but no hardcore variant.
	 * @param texture
	 * @return
	 */
	public static HeartTexture fromHeartFlash(ResourceLocation texture) {
		return validated(texture, 36, 9);
	}
	
	/**
	 * Alias method to construct an instance using a texture only containing the full and half heart both a hardcore and flashing variant.
	 * @param texture
	 * @return
	 */
	public static HeartTexture fromFullHeartSet(ResourceLocation texture) {
		return validated(texture, 36, 18);
	}
	
	/**
	 * Creates a new texture but also registers it for in-IDE validation.
	 * @param texture
	 * @param sizeX
	 * @param sizeY
	 * @return
	 */
	public static HeartTexture validated(ResourceLocation texture, int sizeX, int sizeY) {
		HeartTexture result = new HeartTexture(texture, sizeX, sizeY, sizeX > 9,sizeX == 36, sizeY == 18);
		LOOKUP.add(result);
		return result;
	}
	
	public static final HeartTexture CONTAINER = fromWholeOnly(OriMod.rsrc(HEARTS_FOLDER + "container.png"));
	public static final HeartTexture RADIANT_CONTAINER = fromWholeOnly(OriMod.rsrc(HEARTS_FOLDER + "container_radiant.png"));
	
	public static final HeartTexture NORMAL = fromFullHeartSet(OriMod.rsrc(HEARTS_FOLDER + "normal.png"));
	public static final HeartTexture POISONED = fromFullHeartSet(OriMod.rsrc(HEARTS_FOLDER + "poison.png"));
	public static final HeartTexture WITHERING = fromFullHeartSet(OriMod.rsrc(HEARTS_FOLDER + "wither.png"));
	public static final HeartTexture FROZEN = fromHeartHardcore(OriMod.rsrc(HEARTS_FOLDER + "frozen.png"));
	public static final HeartTexture ABSORB = fromHeartHardcore(OriMod.rsrc(HEARTS_FOLDER + "absorb.png"));
	public static final HeartTexture DECAY = fromFullHeartSet(OriMod.rsrc(HEARTS_FOLDER + "decay.png"));
	public static final HeartTexture DECAY_POISONED = fromFullHeartSet(OriMod.rsrc(HEARTS_FOLDER + "decay_poisoned.png"));
	public static final HeartTexture DECAY_WITHERING = fromFullHeartSet(OriMod.rsrc(HEARTS_FOLDER + "decay_withered.png"));
	public static final HeartTexture RADIANT = fromFullHeartSet(OriMod.rsrc(HEARTS_FOLDER + "radiant.png"));
	
	public static final @Deprecated HeartTexture RADIANT_OVERLAY = fromSingleAndHalf(OriMod.rsrc(HEARTS_FOLDER + "radiant_overlay.png"));
	// public static final HeartTexture ORIMOD_ATLAS = validated(OriMod.rsrc(HEARTS_FOLDER + "spirithealth.png"), 144, 27);
}
