package etithespirit.etimod.item.tools;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMultimap.Builder;

import etithespirit.etimod.common.block.decay.IDecayBlockIdentifier;
import etithespirit.etimod.item.repair.ISpiritRechargeable;
import etithespirit.etimod.item.repair.ItemRecharger;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SpiritOmniTool extends ToolItem implements ISpiritRechargeable {
	/** Modifiers applied when the item is in the mainhand of a user. */
	private final Multimap<Attribute, AttributeModifier> attributeModifiers;
	private final Supplier<Integer> expPerRepair;
	private final Supplier<Integer> repairIncrement;
	
	private static final Set<Block> NO_BLOCKS = Sets.newHashSet();
	
	public SpiritOmniTool(float attackDamageIn, float attackSpeedIn, IItemTier tier, Properties builderIn, Supplier<Integer> expPerRepair, Supplier<Integer> repairIncrement) {
		super(attackDamageIn, attackSpeedIn, tier, NO_BLOCKS, builderIn);
		Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
		builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.getAttackDamage(), AttributeModifier.Operation.ADDITION));
		builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)attackSpeedIn, AttributeModifier.Operation.ADDITION));
		attributeModifiers = builder.build();
		this.expPerRepair = expPerRepair;
		this.repairIncrement = repairIncrement;
	}

	public float getDestroySpeed(ItemStack stack, BlockState state) {
		return this.speed;
	}

	/**
	* Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
	* the damage on the stack.
	*/
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, (entity) -> {
			entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
		});
		return true;
	}

	/**
	* Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
	*/
	public boolean mineBlock(ItemStack stack, World worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
		if (state.getDestroySpeed(worldIn, pos) != 0.0F) {
			stack.hurtAndBreak(1, entityLiving, (entity) -> {
				entity.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
			});
		}

		return true;
	}

	/**
	* Check whether this Item can harvest the given Block
	*/
	public boolean isCorrectToolForDrops(BlockState blockIn) {
		if (blockIn.getBlock() instanceof IDecayBlockIdentifier) {
			return false;
		}
		return true;
	}

	/**
	* Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
	*/
	public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType equipmentSlot) {
		return equipmentSlot == EquipmentSlotType.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(equipmentSlot);
	}
	
	/**
	* Called each tick as long the item is on a player inventory. Uses by maps to check if is on a player hand and
	* update it's contents.
	*/
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		if (!isSelected) return;
		if (!(entityIn instanceof PlayerEntity)) return;
		
		final PlayerEntity playerIn = (PlayerEntity)entityIn;
		if (!playerIn.isUsingItem()) return;
		final ItemStack item = playerIn.inventory.getItem(itemSlot);
		
		if (item.isDamaged() && (playerIn.getExperienceReward(playerIn) > 0 || playerIn.abilities.instabuild)) {
			ItemRecharger instance = ItemRecharger.get(playerIn, stack, this);
			instance.updateTick();
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.etimod.focuslight.1"));
		tooltip.add(new TranslationTextComponent("tooltip.etimod.focuslight.2"));
		tooltip.add(new TranslationTextComponent("tooltip.etimod.focuslight.3"));
	}
	
	@Override
	public void releaseUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		ItemRecharger instance = ItemRecharger.rawGet(stack);
		if (instance != null) instance.dispose();
	}

	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand handIn) {
		final ItemStack item = playerIn.getItemInHand(handIn);
		// final int expCost = getExperienceCostToRepair();
		if (item.isDamaged() && (playerIn.getExperienceReward(playerIn) > 0 || playerIn.abilities.instabuild)) {
			playerIn.startUsingItem(handIn);
			return ActionResult.pass(item);
		}
		return ActionResult.fail(item);
	}
	
	/**
	* How long it takes to use or consume an item
	*/
	@Override
	public int getUseDuration(ItemStack stack) {
		return 72000;
	}

	/**
	* returns the action that specifies what animation to play when the item is being used
	*/
	@Override
	public UseAction getUseAnimation(ItemStack stack) {
		return UseAction.BOW;
	}
	
	@Override
	public int getExperienceCostToRepair() {
		return expPerRepair.get().intValue();
	}
	
	@Override
	public int getDurabilityPerRestoreOp() {
		return repairIncrement.get().intValue();
	}

}
