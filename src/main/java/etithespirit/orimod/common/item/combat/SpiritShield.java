package etithespirit.orimod.common.item.combat;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.client.audio.SpiritSoundProvider;
import etithespirit.orimod.client.render.item.SpiritShieldModel;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import etithespirit.orimod.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.client.IItemRenderProperties;

import java.util.function.Consumer;

public class SpiritShield extends ShieldItem {
	
	public SpiritShield() {
		this(
			new Item.Properties()
				.rarity(Rarity.RARE)
				.tab(CreativeModeTab.TAB_COMBAT)
		);
	}
	
	public SpiritShield(Properties builder) {
		super(builder);
	}
	
	@Override
	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
		consumer.accept(new IItemRenderProperties() {
			@Override
			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
				return new SpiritShield.SpiritShieldRenderer();
			}
		});
	}
	
	static {
		EntityEmittedSoundEventProvider.registerHandler(event -> {
			Entity ent = event.getEntity();
			if (ent instanceof Player) {
				Player player = (Player)ent;
				if (player.isUsingItem()) {
					ItemStack mainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
					ItemStack offHand = player.getItemInHand(InteractionHand.OFF_HAND);
					ItemStack lightShield = new ItemStack(ItemRegistry.LIGHT_SHIELD.get());
					if (mainHand.equals(lightShield,false) || offHand.equals(lightShield, false)) {
						if (event.getSound().equals(SoundEvents.SHIELD_BLOCK)) {
							// Player is holding a light shield and just blocked. Override the sound!
							event.setSound(SpiritSoundProvider.getSpiritShieldImpactSound(false));
							event.setPitch(SpiritSoundPlayer.getRandomPitch());
						} else if (event.getSound().equals(SoundEvents.SHIELD_BREAK)) {
							// Player is holding a light shield and just blocked. Override the sound!
							event.setSound(SpiritSoundProvider.getSpiritShieldImpactSound(true));
							event.setPitch(SpiritSoundPlayer.getRandomPitch());
						}
					}
				}
			}
		});
	}
	
	
	public static class SpiritShieldRenderer extends BlockEntityWithoutLevelRenderer {
		
		private final SpiritShieldModel spiritShieldModel = new SpiritShieldModel();
		
		private static final ResourceLocation SHIELD_MATERIAL = new ResourceLocation(OriMod.MODID, "textures/item/light_shield.png");
		
		public SpiritShieldRenderer() {
			this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		}
		
		public SpiritShieldRenderer(BlockEntityRenderDispatcher p_172550_, EntityModelSet p_172551_) {
			super(p_172550_, p_172551_);
		}
		
		@Override
		public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
			matrixStack.pushPose();
			matrixStack.scale(1.0F, -1.0F, -1.0F);
			matrixStack.translate(0, -1.5, 0);
			VertexConsumer solidEntityBuffer = buffer.getBuffer(RenderType.entityTranslucent(SHIELD_MATERIAL));
			spiritShieldModel.renderToBuffer(matrixStack, solidEntityBuffer, combinedLight, combinedOverlay, 1, 1, 1, 1);
			matrixStack.popPose();
		}
		
	}
}
