package etithespirit.orimod.common.item.tools.hardlight;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.ISpiritLightRepairableItem;
import etithespirit.orimod.common.item.data.SpiritTiers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LightHoe extends HoeItem implements ISpiritLightRepairableItem {
	public LightHoe() {
		super(SpiritTiers.SIMPLE_LIGHT, 1, -0.2f, (new Item.Properties()).rarity(Rarity.EPIC).tab(OriModCreativeModeTabs.SPIRIT_TOOLS).fireResistant().stacksTo(1).setNoRepair().durability(SpiritTiers.COMPLEX_LIGHT.getUses()));
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
		InteractionResultHolder<ItemStack> result = ISpiritLightRepairableItem.super.useForSelfRepair(pLevel, pPlayer, pUsedHand);
		return (result != null) ? result : super.use(pLevel, pPlayer, pUsedHand);
	}
	
	@Override
	public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
		super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
		ISpiritLightRepairableItem.super.appendDefaultRepairHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
	}
	
	@Override
	public int getBarColor(ItemStack pStack) {
		return ISpiritLightRepairableItem.super.getBarColor(pStack);
	}
	
	@Override
	public Component getName(ItemStack pStack) {
		return StaticData.getNameAsLight(super.getName(pStack));
	}
}
