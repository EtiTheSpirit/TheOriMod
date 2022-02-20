package etithespirit.mixin.mixins;


import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.potion.DecayEffect;
import etithespirit.orimod.registry.PotionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * O TWENTY-TWO HOLY GODS OF FORGE ABOVE
 * I PRAY THAT THOU SHALL NOT SMITE THEE FOR THE EGREGIOUS SIN OF APPLYING A MIXIN TO NATIVE FORGE CODE
 * SPARE ME YOUR MERCY, AND UNDERSTAND TWAS NECESSARY FOR THE CLEANLINESS OF THE CODE
 * AMEN
 */
@Mixin(ForgeIngameGui.class)
public abstract class RedirectForgeIngameGuiDrawHealth extends Gui {
	
	private static final ResourceLocation DECAY_HEALTH_ICONS = new ResourceLocation(OriMod.MODID, "textures/hud/spiriticons.png");
	
	public RedirectForgeIngameGuiDrawHealth(Minecraft pMinecraft) {
		super(pMinecraft);
	}
	
	@ModifyArg(
		method = "renderHealth(IILcom/mojang/blaze3d/vertex/PoseStack;)V",
		remap = false,
		at = @At(
			value="INVOKE",
			target="net/minecraftforge/client/gui/ForgeIngameGui.bind(Lnet/minecraft/resources/ResourceLocation;)V",
			ordinal=0
		)
	)
	public ResourceLocation renderHealth$customRsrc(ResourceLocation res) {
		Minecraft minecraft = Minecraft.getInstance();
		if (minecraft.player != null) {
			if (minecraft.player.hasEffect(PotionRegistry.get(DecayEffect.class))) {
				return DECAY_HEALTH_ICONS;
			}
		}
		return res;
	}
	
}
