package etithespirit.orimod.common.item.combat;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.client.audio.SpiritSoundPlayer;
import etithespirit.orimod.client.audio.SpiritSoundProvider;
import etithespirit.orimod.client.render.item.SpiritShieldModel;
import etithespirit.orimod.combat.damage.OriModDamageSources;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.IModelPredicateProvider;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.event.EntityEmittedSoundEventProvider;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import org.jetbrains.annotations.Nullable;

public class SpiritShield extends ShieldItem implements IModelPredicateProvider {
	
	public SpiritShield() {
		this(
			new Item.Properties()
				.rarity(Rarity.RARE)
				.tab(OriModCreativeModeTabs.SPIRIT_COMBAT)
				.stacksTo(1)
				.fireResistant()
				.setNoRepair()
		);
	}
	
	private SpiritShield(Properties builder) {
		super(builder);
	}
	
	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}
	
	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return new SpiritShield.SpiritShieldRenderer();
			}
		});
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		SpiritItemCustomizations.appendDefaultLightToolRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack pToRepair, ItemStack pRepair) {
		return false;
	}
	
	@Override
	public int getBarColor(ItemStack pStack) {
		return SpiritItemCustomizations.getLightToolBarColor(pStack);
	}
	
	@Override
	public Component getName(ItemStack pStack) {
		return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
	}
	
	@Override
	public int getMaxDamage(ItemStack pStack) {
		return 700;
	}
	
	@Override
	public void getPredicates(Map<ResourceLocation, ItemPropertyFunction> predicates) {
		predicates.put(OriMod.rsrc("blocking"), ((pStack, pLevel, pEntity, pSeed) -> {
			if (pStack.is(SpiritShield.this) && pEntity != null && pEntity.isUsingItem()) {
				return 1;
			}
			return 0;
		}));
	}
	
	
	public static class SpiritShieldRenderer extends BlockEntityWithoutLevelRenderer {
		
		private final SpiritShieldModel spiritShieldModel = new SpiritShieldModel();
		
		private static final ResourceLocation SHIELD_MATERIAL = new ResourceLocation(OriMod.MODID, "textures/item/light_shield.png");
		
		public SpiritShieldRenderer() {
			this(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
		}
		
		public SpiritShieldRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
			super(pBlockEntityRenderDispatcher, pEntityModelSet);
		}
		
		@Override
		public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
			matrixStack.pushPose();
			matrixStack.scale(1.0F, -1.0F, -1.0F);
			matrixStack.translate(0, -1.5, 0);
			VertexConsumer solidEntityBuffer = buffer.getBuffer(RenderType.entityTranslucentCull(SHIELD_MATERIAL));
			spiritShieldModel.renderToBuffer(matrixStack, solidEntityBuffer, combinedLight, combinedOverlay, 1, 1, 1, 1);
			matrixStack.popPose();
		}
		
	}
}
