package etithespirit.orimod.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
		 * Queries whether the given effect should be shown in the player's inventory.
		 * <p>
		 * By default, this returns {@code true}.
		 *
		 * @param instance
		 */
		@Override
		public boolean isVisibleInInventory(MobEffectInstance instance) {
			return !(instance.getEffect() instanceof RichEffect);
		}
		
		/**
		 * Queries whether the given effect should be shown in the HUD.
		 * <p>
		 * By default, this returns {@code true}.
		 *
		 * @param instance
		 */
		@Override
		public boolean isVisibleInGui(MobEffectInstance instance) {
			return !(instance.getEffect() instanceof RichEffect);
		}
		
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
			return IClientMobEffectExtensions.super.renderInventoryIcon(instance, screen, poseStack, x, y, blitOffset);
		}
		
		/**
		 * Renders the text of the specified effect in the player's inventory.
		 *
		 * @param effect     The effect instance
		 * @param screen     The effect-rendering screen
		 * @param mStack     The pose stack
		 * @param x          The x coordinate
		 * @param y          The y coordinate
		 * @param blitOffset The blit offset
		 * @return true to prevent default rendering, false otherwise
		 */
		@Override
		public boolean renderInventoryText(MobEffectInstance effect, EffectRenderingInventoryScreen<?> screen, PoseStack mStack, int x, int y, int blitOffset) {
			if (!(effect.getEffect() instanceof RichEffect)) {
				return false;
			}
			RichEffect rich = (RichEffect)effect.getEffect();
			int foreColor = rich.getTextMainColor();
			int backColor = rich.getTextShadowColor();
			
			Component component = screen.getEffectName(effect);
			screen.font.drawShadow(mStack, component, (float)(x + 10 + 18), (float)(y + 6), foreColor);
			String s = MobEffectUtil.formatDuration(effect, 1.0F);
			screen.font.drawShadow(mStack, s, (float)(x + 10 + 18), (float)(y + 6 + 10), backColor);
			return true;
		}
		
		/**
		 * Renders the icon of the specified effect on the player's HUD.
		 * This can be used to render icons from your own texture sheet.
		 *
		 * @param instance  The effect instance
		 * @param gui       The gui
		 * @param poseStack The pose stack
		 * @param x         The x coordinate
		 * @param y         The y coordinate
		 * @param z         The z depth
		 * @param alpha     The alpha value. Blinks when the effect is about to run out
		 * @return true to prevent default rendering, false otherwise
		 */
		@Override
		public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, PoseStack poseStack, int x, int y, float z, float alpha) {
			return IClientMobEffectExtensions.super.renderGuiIcon(instance, gui, poseStack, x, y, z, alpha);
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
	
	/**
	 * @return The color of the text foreground.
	 */
	public int getTextMainColor() {
		return 0xFFFFFF;
	}
	
	/**
	 * @return The color of the text shadow.
	 */
	public int getTextShadowColor() {
		return 0x7F7F7F;
	}
	
	@Override
	public Object getEffectRendererInternal() {
		return RENDERER;
	}
}
