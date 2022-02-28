package etithespirit.orimod.common.item.combat;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.ISpiritLightItem;
import etithespirit.orimod.registry.SoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class SpiritArc extends BowItem implements ISpiritLightItem {
	
	private static final String IS_CHARGED_KEY = "isCharged";
	
	private static final int USE_TIME = 33;
	private static final int READY_TO_FIRE_AFTER_TICKS = USE_TIME - 5;
	private static final int CHARGED_AFTER_TICKS = USE_TIME - 20;
	
	public SpiritArc() {
		this(new Properties().rarity(Rarity.EPIC).stacksTo(1).durability(1561).tab(OriModCreativeModeTabs.SPIRIT_COMBAT).setNoRepair());
	}
	
	private SpiritArc(Properties p_40660_) {
		super(p_40660_);
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
		pLivingEntity.playSound(SoundRegistry.get("item.spirit_arc.shot.base"), 0.4f, 1f);
		if (isCharged(pStack)) {
			pLivingEntity.playSound(SoundRegistry.get("item.spirit_arc.shot.charge_overlay"), 0.3f, 1f);
			setCharged(pStack, false);
		}
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
	
	public int getUseDuration() {
		return getUseDuration(null);
	}
	
	@Override
	public boolean canRepairAtLuxForge(ItemStack stack) {
		return true;
	}
}
