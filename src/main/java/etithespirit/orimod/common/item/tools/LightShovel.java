package etithespirit.orimod.common.item.tools;

import etithespirit.orimod.combat.ExtendedDamageSource;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.ISpiritLightItem;
import etithespirit.orimod.common.item.data.SpiritTiers;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightShovel extends ShovelItem implements ISpiritLightItem {
	public LightShovel() {
		super(SpiritTiers.COMPLEX_LIGHT, 0.6f, -0.2f, (new Item.Properties()).rarity(Rarity.EPIC).tab(OriModCreativeModeTabs.SPIRIT_TOOLS).fireResistant().stacksTo(1).setNoRepair().durability(SpiritTiers.COMPLEX_LIGHT.getUses()));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		InteractionResultHolder<ItemStack> result = ISpiritLightItem.super.useForSelfRepair(pLevel, pPlayer, pUsedHand);
		return (result != null) ? result : super.use(pLevel, pPlayer, pUsedHand);
	}
	
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		ISpiritLightItem.super.appendDefaultRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
	
	@Override
	public int getBarColor(ItemStack pStack) {
		return ISpiritLightItem.super.getBarColor(pStack);
	}
}
