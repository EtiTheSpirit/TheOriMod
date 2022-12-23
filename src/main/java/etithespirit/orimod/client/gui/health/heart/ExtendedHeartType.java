package etithespirit.orimod.client.gui.health.heart;

import etithespirit.orimod.registry.gameplay.EffectRegistry;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public enum ExtendedHeartType implements IHeartRenderType {
	/*
	VANILLA_CONTAINER(VANILLA_ICONS, 0, 0, false, true, true),
	VANILLA_NORMAL(VANILLA_ICONS, 2, 0, true, true, true),
	VANILLA_POISONED(VANILLA_ICONS, 4, 0, true, true, true),
	VANILLA_WITHERED(VANILLA_ICONS, 6, 0, true, true, true),
	VANILLA_ABSORBING(VANILLA_ICONS, 8, 0, false, true, true),
	VANILLA_FROZEN(VANILLA_ICONS, 9, 0, false, true, true),
	*/
	@Deprecated
	VANILLA_CONTAINER(0, 0, false),
	@Deprecated
	VANILLA_NORMAL(2, 0, true),
	@Deprecated
	VANILLA_POISONED(4, 0, true),
	@Deprecated
	VANILLA_WITHERED(6, 0, true),
	@Deprecated
	VANILLA_ABSORBING(8, 0, false),
	@Deprecated
	VANILLA_FROZEN(9, 0, false),
	// Above: 9 is not an error. It is exactly what vanilla code does.
	
	/*
	CONTAINER(HeartTextures.CONTAINER, false, false),
	RADIANT_CONTAINER(HeartTextures.RADIANT_CONTAINER, false, false),
	DECAYING(HeartTextures.DECAY),
	DECAYING_POISONED(HeartTextures.DECAY_POISONED),
	DECAYING_WITHERED(HeartTextures.DECAY_WITHERING),
	RADIANT_OVERLAY(HeartTextures.RADIANT_OVERLAY, false, false)
	*/
	/*
	CONTAINER(HeartTextures.CONTAINER, false, false),
	RADIANT_CONTAINER(HeartTextures.ORIMOD_ATLAS, 0, 2, false, false),
	DECAYING(HeartTextures.ORIMOD_ATLAS, 2, 0),
	DECAYING_POISONED(HeartTextures.ORIMOD_ATLAS, 4, 0),
	DECAYING_WITHERED(HeartTextures.ORIMOD_ATLAS, 6, 0),
	RADIANT_OVERLAY(HeartTextures.ORIMOD_ATLAS, 2, 2)
	*/
	CONTAINER(HeartTexture.CONTAINER),
	RADIANT_CONTAINER(HeartTexture.RADIANT_CONTAINER),
	
	NORMAL(HeartTexture.NORMAL),
	POISONED(HeartTexture.POISONED),
	WITHERED(HeartTexture.WITHERING),
	FROZEN(HeartTexture.FROZEN),
	
	DECAYING(HeartTexture.DECAY),
	DECAYING_POISONED(HeartTexture.DECAY_POISONED),
	DECAYING_WITHERED(HeartTexture.DECAY_WITHERING),
	@Deprecated RADIANT_OVERLAY(HeartTexture.RADIANT_OVERLAY),
	RADIANT(HeartTexture.RADIANT)
	;
	
	public static ExtendedHeartType getContainerForEntity(LivingEntity entity) {
		if (entity.hasEffect(EffectRegistry.RADIANT.get())) {
			return RADIANT_CONTAINER;
		}
		return CONTAINER;
	}
	
	public static ExtendedHeartType getBaseHeartForEntity(LivingEntity entity) {
		boolean decaying = entity.hasEffect(EffectRegistry.DECAY.get());
		boolean poisoned = entity.hasEffect(MobEffects.POISON);
		boolean wither = entity.hasEffect(MobEffects.WITHER);
		boolean isFrozen = entity.isFullyFrozen();
		
		if (decaying) {
			if (wither) {
				return DECAYING_WITHERED;
			} else if (poisoned) {
				return DECAYING_POISONED;
			}
			return DECAYING;
		} else {
			if (isFrozen) {
				return VANILLA_FROZEN;
			} else if (wither) {
				return VANILLA_WITHERED;
			} else if (poisoned) {
				return VANILLA_POISONED;
			}
			return VANILLA_NORMAL;
		}
	}
	
	private static int getXFromIndex(int x, boolean isVanilla) {
		int value = x * 18;
		if (isVanilla) value += 16;
		return value;
	}
	
	private final ResourceLocation rawTexture;
	private final int x;
	private final int y;
	
	private final boolean supportsBlinking;
	private final boolean hasHardcoreVariant;
	private final boolean hasHalf;
	
	private final int texWidth;
	private final int texHeight;
	
	private final boolean isVanilla;
	
	/**
	 * For vanilla icons.png only!
	 * @param xIndex The X index, like in HeartType
	 * @param yIndex The Y index, like in HeartType
	 * @param blinks Whether or not the heart can blink.
	 */
	ExtendedHeartType(int xIndex, int yIndex, boolean blinks) {
		rawTexture = GuiComponent.GUI_ICONS_LOCATION;
		x = getXFromIndex(xIndex, true);
		y = yIndex * 9;
		supportsBlinking = blinks;
		hasHardcoreVariant = true;
		hasHalf = true;
		texWidth = 256;
		texHeight = 256;
		isVanilla = true;
	}
	
	/**
	 * For custom atlases.
	 * @param customAtlas
	 * @param xIndex
	 * @param yIndex
	 * @param canBlink
	 * @param hasHardcore
	 * @param resX
	 * @param resY
	 */
	ExtendedHeartType(ResourceLocation customAtlas, int xIndex, int yIndex, boolean hasHalfVariant, boolean canBlink, boolean hasHardcore, int resX, int resY) {
		rawTexture = customAtlas;
		x = getXFromIndex(xIndex, false);
		y = yIndex * 9;
		supportsBlinking = canBlink;
		hasHardcoreVariant = hasHardcore;
		hasHalf = hasHalfVariant;
		texWidth = resX;
		texHeight = resY;
		isVanilla = false;
		
	}
	
	/**
	 * For single heart images.
	 * @param tex
	 */
	ExtendedHeartType(HeartTexture tex) {
		rawTexture = tex.texture();
		x = 0;
		y = 0;
		supportsBlinking = tex.supportsBlinking();
		hasHardcoreVariant = tex.hasHardcoreVariant();
		hasHalf = tex.hasHalfVariant();
		texWidth = tex.sizeX();
		texHeight = tex.sizeY();
		isVanilla = false;
	}
	
	
	/*
	ExtendedHeartType(ResourceLocation tex) {
		this(tex, true);
	}
	
	ExtendedHeartType(ResourceLocation tex, boolean canBlink) {
		this(tex, canBlink, true);
	}
	
	ExtendedHeartType(ResourceLocation tex, boolean canBlink, boolean canBeHardcore) {
		this(tex, 0, 0, canBlink, canBeHardcore);
	}
	
	ExtendedHeartType(ResourceLocation tex, int indexX, int indexY) {
		this(tex, indexX, indexY, true, true);
	}
	
	ExtendedHeartType(ResourceLocation tex, int indexX, int indexY, boolean canBlink) {
		this(tex, indexX, indexY, canBlink, true);
	}
	
	ExtendedHeartType(ResourceLocation tex, int indexX, int indexY, boolean canBlink, boolean canBeHardcore) {
		this(tex, indexX, indexY, canBlink, canBeHardcore, false);
	}
	
	ExtendedHeartType(ResourceLocation tex, int indexX, int indexY, boolean canBlink, boolean canBeHardcore, boolean isVanilla) {
		texture = tex;
		x = getXFromIndex(indexX, isVanilla);
		y = indexY * 9;
		this.canBlink = canBlink;
		this.canBeHardcore = canBeHardcore;
		this.isVanilla = isVanilla;
	}
	*/
	
	@Override
	public ResourceLocation getTexture() {
		return rawTexture;
	}
	
	@Override
	public int getBaseX() {
		return this.x;
	}
	
	@Override
	public int getBaseY() {
		return this.y;
	}
	
	@Override
	public boolean canBlink() {
		return supportsBlinking;
	}
	
	@Override
	public boolean canBeHardcore() {
		return hasHardcoreVariant;
	}
	
	@Override
	public boolean canHaveHalfHeart() {
		return hasHalf;
	}
	
	
	@Override
	public boolean isVanilla() {
		return this.isVanilla;
	}
	
	@Override
	public int getImageWidth() {
		return this.texWidth;
	}
	
	@Override
	public int getImageHeight() {
		return this.texHeight;
	}
}
