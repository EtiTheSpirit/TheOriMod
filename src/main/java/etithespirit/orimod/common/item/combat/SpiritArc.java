package etithespirit.orimod.common.item.combat;

import etithespirit.orimod.combat.projectile.SpiritArrow;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.ISpiritLightItem;
import etithespirit.orimod.registry.EntityRegistry;
import etithespirit.orimod.registry.SoundRegistry;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import java.util.function.Predicate;

import net.minecraft.world.item.Item.Properties;

public class SpiritArc extends BowItem implements ISpiritLightItem {
	
	private static final String IS_CHARGED_KEY = "isCharged";
	
	private static final int USE_TIME = 33;
	private static final int READY_TO_FIRE_AFTER_TICKS = USE_TIME - 5;
	private static final int CHARGED_AFTER_TICKS = USE_TIME - 20;
	
	public SpiritArc() {
		this(new Properties().rarity(Rarity.EPIC).stacksTo(1).durability(1000).tab(OriModCreativeModeTabs.SPIRIT_COMBAT).setNoRepair());
	}
	
	private SpiritArc(Properties pProperties) {
		super(pProperties);
	}
	
	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}
	
	protected static void setCharged(ItemStack onStack, boolean isCharged) {
		onStack.getOrCreateTag().putBoolean(IS_CHARGED_KEY, isCharged);
	}
	
	protected static boolean isCharged(ItemStack onStack) {
		return onStack.getOrCreateTag().getBoolean(IS_CHARGED_KEY); // Returns false if the key does not exist.
	}
	
	@Override
	public void releaseUsing(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity, int pTimeLeft) {
		if (pTimeLeft > READY_TO_FIRE_AFTER_TICKS) {
			return;
		}
		boolean chargedShot = false;
		pLivingEntity.playSound(SoundRegistry.get("item.spirit_arc.shot.base"), 0.4f, 1f);
		if (isCharged(pStack)) {
			pLivingEntity.playSound(SoundRegistry.get("item.spirit_arc.shot.charge_overlay"), 0.3f, 1f);
			setCharged(pStack, false);
			chargedShot = true;
		}
		
		if (!pLevel.isClientSide()) {
			SpiritArrow projectile = new SpiritArrow(EntityRegistry.SPIRIT_ARROW.get(), pLevel);
			
			float damage = chargedShot ? 5 : 2;
			int power = pStack.getEnchantmentLevel(Enchantments.POWER_ARROWS);
			if (power > 0) {
				damage += (power / 2f) + 0.5f;
			}
			
			int punch = pStack.getEnchantmentLevel(Enchantments.PUNCH_ARROWS);
			if (punch > 0) {
				projectile.setKnockback(punch);
			}
			
			if (pStack.getEnchantmentLevel(Enchantments.FLAMING_ARROWS) > 0) {
				projectile.setSecondsOnFire(100);
			}
			
			projectile.setBaseDamage(damage);
			projectile.setCritArrow(chargedShot);
			projectile.setPos(pLivingEntity.getX(), pLivingEntity.getEyeY() - 0.1, pLivingEntity.getZ());
			projectile.shootFromRotation(pLivingEntity, pLivingEntity.getXRot(), pLivingEntity.getYRot(), 0.0F, chargedShot ? 5f : 1.6f, chargedShot ? 0 : 0.1f);
			pLevel.addFreshEntity(projectile);
		}
		if (pLivingEntity instanceof Player player) player.awardStat(Stats.ITEM_USED.get(this));
		pStack.hurtAndBreak(chargedShot ? 1 : 2, pLivingEntity, item -> {});
	}
	
	@Override
	public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pRemainingUseDuration) {
		if (pRemainingUseDuration <= CHARGED_AFTER_TICKS) {
			if (!isCharged(pStack)) {
				setCharged(pStack, true);
				pLivingEntity.playSound(SoundRegistry.get("item.spirit_arc.charge"), 0.4f, 1f);
			}
		} else if (pRemainingUseDuration == getUseDuration()) {
			setCharged(pStack, false);
			pLivingEntity.playSound(SoundRegistry.get("item.spirit_arc.start"), 0.4f, 1f);
		}
	}
	
	
	@Override
	public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
		this.releaseUsing(pStack, pLevel, pLivingEntity, 0);
		return super.finishUsingItem(pStack, pLevel, pLivingEntity);
	}
	
	@Override
	public int getUseDuration(ItemStack pStack) {
		// equip
		// ready to fire in 10 ticks
		// fully charged in 30 ticks (total)
		return USE_TIME;
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		pPlayer.startUsingItem(pHand);
		return InteractionResultHolder.success(itemstack);
	}
	
	public int getUseDuration() {
		return getUseDuration(null);
	}
	
	@Override
	public int getDefaultProjectileRange() {
		return 10;
	}
	
	@Override
	public int getBarColor(ItemStack pStack) {
		return ISpiritLightItem.super.getBarColor(pStack);
	}
	
}
