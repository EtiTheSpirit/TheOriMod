package etithespirit.orimod.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraftforge.client.EffectRenderer;

/**
 * This class allows for overriding the rendered text color of the status effect. It also allows rendering custom icons trivially easily.
 */
public abstract class RichEffect extends MobEffect {
	
	
	private static final EffectRenderer RENDERER = new EffectRenderer() {
		
		@Override
		public void renderInventoryEffect(MobEffectInstance effect, EffectRenderingInventoryScreen<?> gui, PoseStack mStack, int x, int y, float z) {
			if (!(effect.getEffect() instanceof RichEffect)) {
				return;
			}
			RichEffect rich = (RichEffect)effect.getEffect();
			int foreColor = rich.getTextMainColor();
			int backColor = rich.getTextShadowColor();
			
			Component component = gui.getEffectName(effect);
			gui.font.drawShadow(mStack, component, (float)(x + 10 + 18), (float)(y + 6), foreColor);
			String s = MobEffectUtil.formatDuration(effect, 1.0F);
			gui.font.drawShadow(mStack, s, (float)(x + 10 + 18), (float)(y + 6 + 10), backColor);
		}
		
		@Override
		public void renderHUDEffect(MobEffectInstance effect, GuiComponent gui, PoseStack mStack, int x, int y, float z, float alpha) {
		
		}
		
		@Override
		public boolean shouldRenderInvText(MobEffectInstance effect) {
			return !(effect.getEffect() instanceof RichEffect);
		}
		
		@Override
		public boolean shouldRenderHUD(MobEffectInstance effect) {
			return !(effect.getEffect() instanceof RichEffect);
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
