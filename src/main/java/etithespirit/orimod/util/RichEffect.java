package etithespirit.orimod.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;

/**
 * This class allows for overriding the rendered text color of the status effect. It also allows rendering custom icons trivially easily.
 */
public abstract class RichEffect extends MobEffect {
	
	private static final IClientMobEffectExtensions RENDERER = new IClientMobEffectExtensions() {
		
		/**
		 * Renders the icon of the specified effect in the player's inventory.
		 * This can be used to render icons from your own texture sheet.
		 *
		 * @param instance   The effect instance
		 * @param screen     The effect-rendering screen
		 * @param poseStack  The pose stack
		 * @param x          The x coordinate
		 * @param y          The y coordinate
		 * @param blitOffset The blit offset
		 * @return true to prevent default rendering, false otherwise
		 */
		@Override
		public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, PoseStack poseStack, int x, int y, int blitOffset) {
			MobEffect effect = instance.getEffect();
			if (effect instanceof RichEffect rich) {
				RenderSystem.setShaderTexture(0, rich.getCustomIcon());
				GuiComponent.blit(poseStack, x + 7, y + 7, blitOffset, 0, 0, 18, 18, 18, 18);
				return true;
			}
			return false;
		}
		
		/**
		 * Renders the icon of the specified effect on the player's HUD.
		 * This can be used to render icons from your own texture sheet.
		 *
		 * @param instance      The effect instance
		 * @param gui           The gui
		 * @param poseStack     The pose stack
		 * @param x             The x coordinate
		 * @param y             The y coordinate
		 * @param blitOffset    The offset used for the drawn texture. This is an int despite the interface declaring it as a float.
		 * @param alpha         The alpha value. Blinks when the effect is about to run out
		 * @return true to prevent default rendering, false otherwise
		 */
		@Override
		public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, PoseStack poseStack, int x, int y, float blitOffset, float alpha) {
			if (instance.getEffect() instanceof RichEffect rich) {
				RenderSystem.setShaderTexture(0, rich.getCustomIcon());
				RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
				GuiComponent.blit(poseStack, x + 7, y + 7, (int)blitOffset, 0, 0, 18, 18, 18, 18);
				return true;
			}
			return false;
		}
	};
	
	/** */
	public RichEffect() {
		this(MobEffectCategory.NEUTRAL, 0);
	}
	
	protected RichEffect(MobEffectCategory typeIn, int liquidColorIn) {
		super(typeIn, liquidColorIn);
	}
	
	/**
	 * Returns the default type for this effect. This value is cached and this method will only be called once.
	 * @return The type of effect.
	 */
	@Override
	public abstract MobEffectCategory getCategory();
	
	/**
	 * Returns the default color for this effect. This value is cached and this method will only be called once.
	 * @return The custom color. 0x00RRGGBB
	 */
	@Override
	public abstract int getColor();
	
	/**
	 * Returns the {@link ResourceLocation} representing the image to be used as this effect's icon. It may return null if a default icon is used.
	 * @return The custom icon, or null to use a vanilla icon as defined by {@link MobEffect}.
	 */
	public abstract ResourceLocation getCustomIcon();
	
	/*
	 * @return The color of the text foreground.
	 */
	//public int getTextMainColor() {
	//	return 0xFFFFFF;
	//}
	
	/*
	 * @return The color of the text shadow.
	 */
	//public int getTextShadowColor() {
	//	return 0x7F7F7F;
	//}
	
	@Override
	public Object getEffectRendererInternal() {
		return RENDERER;
	}
}
