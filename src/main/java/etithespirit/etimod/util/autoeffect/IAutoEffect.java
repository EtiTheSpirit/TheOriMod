package etithespirit.etimod.util.autoeffect;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.blaze3d.matrix.MatrixStack;

import etithespirit.etimod.util.autoeffect.data.EffectTextDisplayType;
import etithespirit.etimod.util.autoeffect.data.NumericUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeEffect;

/**
 * An extension to potion effects that provides more functionality for less work.
 * @author Eti
 *
 */
public interface IAutoEffect extends IForgeEffect {
	
	/**
	 * If this potion uses a custom icon, this is the icon file. Null if no custom icon is used. This is expected to be a singular 18x18 image.
	 * @return The ResourceLocation used for the icon.
	 */
	@OnlyIn(Dist.CLIENT)
	public abstract @Nullable ResourceLocation getCustomIcon();
	
	/**
	 * Returns a string that reflects on the remaining time of this effect. Should never return null. If you wish to not render a timer, consider overriding GetInfoDisplayType() and returning EffectTextDisplayType.NAME_ONLY
	 * 
	 * @param effect The EffectInstance that provides the time.
	 * @return A string that reflects the remaining time on this effect. Its default implementation is {@code EffectUtils.getPotionDurationString(effect, 1f);}
	 */
	@OnlyIn(Dist.CLIENT)
	public default @Nonnull String getCustomDurationString(EffectInstance effect) {
		// Don't use the long duration system here by default because that will have negative effects on default infinite potions, and that code was designed for edge cases anyway.
		return EffectUtils.formatDuration(effect, 1f);
	}
	
	/**
	 * If a custom HUD is desired when this effect is active, return the modified version of icons.png here.<br/>
	 * THIS IS NOT YET FUNCTIONAL.
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	@Deprecated
	public default void getHudWhenActive() {
		// TODO: Resource rendering methods e.g. overlay/replace (overlay is great for effects that should add cosmetic effects, e.g. a shield effect to your health)
		// TODO: System to create "importance" on HUD elements to aid in resolving conflicts. That's the big one. How do we decide which HUD to draw?
	}
	
	/**
	 * A simple preset method of rendering this potion in the inventory, which controls text.
	 * @return An EffectTextDisplayType, which controls the preset display behavior of this potion.
	 */
	@OnlyIn(Dist.CLIENT)
	public default @Nonnull EffectTextDisplayType getInfoDisplayType() {
		return EffectTextDisplayType.STOCK;
	}
	
	/**
	 * Returns an amplifier limit. Under normal circumstances, a return value greater than 255 (the default) is completely useless as vanilla Minecraft prevents this from occurring.<br/>
	 * <br/>
	 * <strong>THIS IS USED IN RENDERING ONLY.</strong> If you need to functionally limit your effect, it should be done elsewhere. This value causes the default display options to act like the effect's amplifier is at this value should it actually be larger (e.g. if effect.getAmplifier() is 10 and this returns 5, default display functions will treat it like it's 5).  
	 * @return A limit for this potion's amplifier, which is used in the rendering of Roman numerals after its name.
	 */
	@OnlyIn(Dist.CLIENT)
	public default int getMaxDisplayAmplifier() {
		return 255;
	}
	
	/**
	 * This only functions if you are using default render methods (you don't override them), or if you do override them and remember to implement this.
	 * @return The text color to use when rendering the display name of this effect. Default is white 0xFFFFFF
	 */
	@OnlyIn(Dist.CLIENT)
	public default int getNameColor() {
		return 0xFFFFFF;
	}
	
	/** 
	 * This only functions if you are using default render methods (you don't override them), or if you do override them and remember to implement this.
	 * @return The text color to use when rendering the timer for the remaining duration of this effect. Default is gray 0x7F7F7F
	 */
	@OnlyIn(Dist.CLIENT)
	public default int getTimeColor() {
		return 0x7F7F7F;
	}
	
	/**
	 * This only functions if you are using default render methods (you don't override them), or if you do override them and remember to implement this.
	 * If overridden, and if it returns a non-null value, the timer under the potion's name will be replaced with this string.
	 * @return A custom string to use as a subtitle for this effect in place of time, or null to use the time.
	 */
	@OnlyIn(Dist.CLIENT)
	public default @Nullable String getSubtitle() { 
		return null;
	}
	
	/**
	 * IAutoEffect renders text itself, and as such, this should be false. It is not advised that you override this to return true. 
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	default boolean shouldRenderInvText(EffectInstance effect) { 
		return false;
	}
	
	/**
	 * Renders the text in the GUI element for a potion in the player's inventory. By default, this will use {@link etithespirit.etimod.util.autoeffect.data.NumericUtilities.ToRomanNumerals} to display effect level.<br/>
	 * <br/>
	 * If you wish to not use this method of displaying potion amplifier (in favor of localized entries), override this method, and set it to just run {@code super.renderPotionText(effect, gui, mStack, x, y, z, false);}
	 * @param effect The associated potion effect that should have its details rendered.
	 * @param gui The GUI element that this will be drawn on.
	 * @param mStack The matrix storing coordinates for this GUI element.
	 * @param x The X coordinate of where this is expected to be drawn.
	 * @param y The Y coordinate of where this is expected to be drawn.
	 * @param z The Z coordinate of where this is expected to be drawn.
	 * @param useBigRomanNumerals If true, NumericUtilities.ToRomanNumerals will be used to render the amplifier level, which will grant Roman numerals up to 1000, albeit at the cost of disrespecting localization.
	 */
	@OnlyIn(Dist.CLIENT)
	public default void renderPotionText(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z, boolean useBigRomanNumerals) {
		EffectTextDisplayType type = getInfoDisplayType();
		if (type == EffectTextDisplayType.NO_TEXT) return;
		
		String displayName = I18n.get(effect.getEffect().getDisplayName().getString());
		if (effect.getAmplifier() > 0) {
			int effectiveAmp = Math.min(effect.getAmplifier() + 1, getMaxDisplayAmplifier());
			if (useBigRomanNumerals) {
				displayName += " " + NumericUtilities.toRomanNumerals(effectiveAmp);
			} else {
				displayName += I18n.get("enchantment.level." + effectiveAmp);
			}
		}
		
		Minecraft mc = Minecraft.getInstance();
		if (type == EffectTextDisplayType.STOCK) {
			// Render just like MC does, albeit with our custom colors.
			mc.font.drawShadow(mStack, displayName, (float)(x + 10 + 18), (float)(y + 6), getNameColor());
	        String durationString = getCustomDurationString(effect);
	        mc.font.drawShadow(mStack, durationString, (float)(x + 10 + 18), (float)(y + 6 + 10), getTimeColor());
	        
		} else if (type == EffectTextDisplayType.NAME_ONLY) {
			// Render only the potion's name. Omit the time.
			mc.font.drawShadow(mStack, displayName, (float)(x + 10 + 18), (float)(y + 12), getNameColor());
			// For the record: y + 12 is done so that the text is vertically centered.
			
		} else if (type == EffectTextDisplayType.TIME_ONLY) {
			// Render only the potion's time. Omit the name.
			String durationString = getCustomDurationString(effect);
		    mc.font.drawShadow(mStack, durationString, (float)(x + 10 + 18), (float)(y + 12), getTimeColor());
		    // For the record: y + 12 is done so that the text is vertically centered.
		    
		} // else {
			// Hey, you! You're finally awake! You were trying to cross the border, right? Same as us, and that thief over there.
		// }
	}
	
	/**
     * Called to draw the this Potion onto the player's inventory when it's active. It will display custom text here, as well as the icon, if applicable.
     *
     * @param effect The active PotionEffect to render the information of.
     * @param gui The GUI element this effect will be drawn on.
     * @param mStack The MatrixStack that affects how this element is drawn.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z level
     */
	@Override
    @OnlyIn(Dist.CLIENT)
    public default void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z) {
		// Do this first.
		renderPotionText(effect, gui, mStack, x, y, z, true);
		
		// Then this.
    	ResourceLocation rsrc = getCustomIcon();
    	if (rsrc == null) return;
        Minecraft mc = Minecraft.getInstance();
		TextureManager texMgr = mc.getTextureManager();
		texMgr.bind(rsrc);
		AbstractGui.fill(mStack, x + 6, y + 6, 18, 18, 0xFFFFFF);
    }
    
    /**
     * Called to draw the this Potion onto the player's ingame HUD when it's active.
     * This can be used to e.g. render Potion icons from your own texture.<br/>
     * 
     * In the default behavior of IAutoEffect, this renders the custom icon when applicable.
     * 
     * @param effect the active PotionEffect
     * @param gui the gui instance
     * @param mStack The MatrixStack
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z level
     * @param alpha the alpha value, blinks when the potion is about to run out
     */
	@Override
    @OnlyIn(Dist.CLIENT)
    public default void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha) {
    	ResourceLocation rsrc = getCustomIcon();
    	if (rsrc == null) return;
    	
		Minecraft mc = Minecraft.getInstance();
		TextureManager texMgr = mc.getTextureManager();
		texMgr.bind(rsrc);
		AbstractGui.fill(mStack, x + 3, y + 3, 18, 18, assembleTransparentWhiteColor(alpha));
		//Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }
    
    /**
	 * A simple method of constructing an effect instance from this effect that allows for editing the most common flags.<br/>
	 * Sets duration to 0 and particles to false.
	 * <br/>
	 * For help in the EffectInstance constructor, its parameters are: {@code Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticles, boolean showIcon}
	 * 
	 * @return An EffectInstance for this Effect constructed with the given duration, amplifier 0, and no particles.
	 */
	public default EffectInstance constructEffect(int duration) {
		return constructEffect(duration, 0, false);
	}
    
    /**
	 * A simple method of constructing an effect instance from this effect that allows for editing the most common flags.<br/>
	 * <br/>
	 * For help in the EffectInstance constructor, its parameters are: {@code Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticles, boolean showIcon}
	 * 
	 * @return An EffectInstance for this Effect constructed with the given duration and amplifier, and with no particles.
	 */
	public default EffectInstance constructEffect(int duration, int amplifier) {
		return constructEffect(duration, amplifier, false);
	}
	
    /**
	 * A simple method of constructing an effect instance from this effect that allows for editing the most common flags.<br/>
	 * 
	 * For help in the EffectInstance constructor, its parameters are: {@code Effect potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticles, boolean showIcon}
	 * 
	 * @return An EffectInstance for this Effect.
	 */
	public default EffectInstance constructEffect(int duration, int amplifier, boolean particles) {
		return new EffectInstance((Effect)this, duration, amplifier, false, particles, true);
	}
	
	/**
	 * Constructs this potion effect with amplifier 0. Whether or not it renders particles is determined by {@code PreferParticles()}. It has a timer duration that is effectively infinite.
	 * @return A new EffectInstance for this potion with amp=0, particles=false, duration=0x7FFFFFFF
	 */
	public default EffectInstance constructInfiniteEffect() {
		return constructEffect(Integer.MAX_VALUE, 0, false);
	}
	
	/**
	 * Constructs this potion effect with the given amplifier. Whether or not it renders particles is determined by {@code PreferParticles()}. It has a timer duration that is effectively infinite.
	 * @param amplifier The amplifier of the potion.
	 * @return A new EffectInstance for this potion with amp=(amplifier), particles=false, duration=0x7FFFFFFF
	 */
	public default EffectInstance constructInfiniteEffect(int amplifier) {
		return constructEffect(Integer.MAX_VALUE, amplifier, false);
	}
	
	/**
	 * Constructs this potion effect with the given amplifier, particle state, and a timer duration that is effectively infinite.
	 * @param amplifier The amplifier of the potion.
	 * @param particles Whether or not to render particles.
	 * @return A new EffectInstance for this potion with amp=(amplifier), particles=(particles), duration=0x7FFFFFFF
	 */
	public default EffectInstance constructInfiniteEffect(int amplifier, boolean particles) {
		return constructEffect(Integer.MAX_VALUE, amplifier, particles);
	}
	
	/**
	 * An alias method used in GUI rendering that returns white (255, 255, 255) with the given alpha, where 0 is invisible and 1 is fully opaque.
	 * @param alpha The desired alpha value from 0 to 1, corresponding to invisible and opaque respectively
	 * @return An integer color value in the format of ARGB.
	 */
	public static int assembleTransparentWhiteColor(float alpha) {
		int alphaInt = (int)Math.floor(Math.min(alpha * 255, 255));
		return (0xFFFFFF & (alphaInt << 24));
	}
	
}
