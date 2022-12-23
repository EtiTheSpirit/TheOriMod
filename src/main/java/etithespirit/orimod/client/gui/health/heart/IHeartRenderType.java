package etithespirit.orimod.client.gui.health.heart;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import etithespirit.orimod.OriMod;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

/**
 * Provides the coordinates used to render a heart from a texture, as well as the texture and any health-related settings.
 * This is intended to be used by individual heart textures (that is, the texture has either two or four hearts). The layout rules are as follows:<br/>
 * <ol>
 *     <li>The hearts MUST have a resolution that is 9x9 pixels.</li>
 *     <li>The hearts MUST be ordered, from left to right, as: Full, Half, Full (When Blinking), Half (When Blinking)</li>
 *     <li>OPTIONALLY, the hearts may have a second row directly below them for hardcore.</li>
 *     <li>OPTIONALLY, the hearts may have a second pair to the right of the first pair for blinking graphics.</li>
 *     <li>You can pack multiple heart types into one texture if desired, but separate textures for each heart type is okay.</li>
 * </ol>
 */
public interface IHeartRenderType {
	ResourceLocation VANILLA_ICONS = GuiComponent.GUI_ICONS_LOCATION;
	
	/** @return The texture containing the hearts. */
	ResourceLocation getTexture();
	
	/** @return The base (full heart, not blinking, not hardcore) X coordinate of this graphic on the texture. */
	int getBaseX();
	
	/** @return The base (full heart, not blinking, not hardcore) Y coordinate of this graphic on the texture. */
	int getBaseY();
	
	/**
	 * Given further context about what is happening to the heart, this returns the base X value with any necessary changes applied, assuming the heart texture follows the proper shape (see the docs of {@link IHeartRenderType} for more info).
	 * @param isHalfHeart If true, the half heart should be used in place of the full heart.
	 * @param isFlashing If true, the heart is flashing.
	 * @return The X coordinate on the texture for the appropriate heart variant of this type.
	 */
	default int getX(boolean isHalfHeart, boolean isFlashing) {
		int baseX = getBaseX();
		if (isHalfHeart && canHaveHalfHeart()) baseX += 9;
		if (isFlashing && canBlink()) baseX += 18;
		return baseX;
	}
	
	/**
	 * See {@link #getY(boolean, boolean)}.
	 * @param isHardcore If true, the heart type should be its hardcore variant.
	 * @return The Y coordiante on the texture for the appropriate heart variant of this type.
	 */
	default int getY(boolean isHardcore) {
		return getY(isHardcore, isVanilla());
	}
	
	/**
	 * Given further context about what is happening to the heart, this returns the base Y value with any necessary changes applied, assuming the heart texture follows the proper shape (see the docs of {@link IHeartRenderType} for more info).
	 * @param isHardcore If true, the heart type should be its hardcore variant.
	 * @param useVanillaOffset If true, the texture of this heart is the vanilla icons.png file which offsets hardcore hearts further down the texture (by 45px) than the shape here intends (9px).
	 * @return The Y coordiante on the texture for the appropriate heart variant of this type.
	 */
	default int getY(boolean isHardcore, boolean useVanillaOffset) {
		return getBaseY() + ((isHardcore && canBeHardcore()) ? (useVanillaOffset ? 45 : 9) : 0);
	}
	
	/** @return true if this heart blinks when healing. */
	boolean canBlink();
	
	/** @return true if this heart has a special variation for hardcore mode. */
	boolean canBeHardcore();
	
	/** @return true if this heart has a half heart variant. Only ever false on containers (sensibly). */
	boolean canHaveHalfHeart();
	
	/** @return true if the texture is the vanilla icons.png file. */
	default boolean isVanilla() {
		return getTexture().equals(VANILLA_ICONS);
	}
	
	/**
	 * @return The width of the texture for this heart.
	 */
	int getImageWidth();
	
	/**
	 * @return The height of the texture for this heart.
	 */
	int getImageHeight();
	
	/**
	 * This method automatically draws the given heart type at the given position.
	 * @param poseStack The pose stack controlling the render data itself.
	 * @param renderType The type of heart to draw.
	 * @param player The player this is rendering for.
	 * @param screenX The X coordinate on the player's screen to render at.
	 * @param screenY The Y coordinate on the player's screen to render at.
	 */
	static void drawSingle(PoseStack poseStack, IHeartRenderType renderType, Entity player, int screenX, int screenY, boolean isHalfHeart, boolean isBlinkFrame) {
		RenderSystem.setShaderTexture(0, renderType.getTexture());
		int u = renderType.getX(isHalfHeart, isBlinkFrame);
		int v = renderType.getY(player.getCommandSenderWorld().getLevelData().isHardcore());
		GuiComponent.blit(poseStack, screenX, screenY, u, v, 9, 9, renderType.getImageWidth(), renderType.getImageHeight());
	}
	
}
