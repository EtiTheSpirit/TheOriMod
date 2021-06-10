package etithespirit.etimod.item.repair;

import java.util.HashMap;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.util.PlayerDataUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

/**
 * A utility class that assists in tracking and spending player experience to recharge an item.
 * @author Eti
 *
 */
public final class ItemRecharger {
	
	private static final HashMap<ItemStack, ItemRecharger> RECHARGER_CACHE = new HashMap<ItemStack, ItemRecharger>();
	
	private double speedCoefficient = 0.1D;
	private final PlayerEntity player;
	private final ItemStack itemReference;
	private final ISpiritRechargeable rechargeableItem;
	
	private ItemRecharger(PlayerEntity player, ItemStack forStack, ISpiritRechargeable rechargeableItem) {
		if (RECHARGER_CACHE.containsKey(forStack)) {
			EtiMod.LOG.warn("WARNING: A new ItemRecharger was instantiated for {}, but one already existed in the registry! It will be overwritten.", forStack);
		}
		RECHARGER_CACHE.put(forStack, this);
		this.player = player;
		this.itemReference = forStack;
		this.rechargeableItem = rechargeableItem;
	}
	
	/**
	 * Returns a new or existing instance of ItemRecharger associated with the given ItemStack.
	 * @param player
	 * @param stack
	 * @param asRechargeable
	 * @return
	 */
	public static ItemRecharger get(PlayerEntity player, ItemStack stack, ISpiritRechargeable asRechargeable) {
		ItemRecharger instance = rawGet(stack);
		if (instance == null) {
			return new ItemRecharger(player, stack, asRechargeable);
		}
		return instance;
	}
	
	/**
	 * Attempts to return an instance of this type for the given ItemStack, or null if one is not active. Only useful for disposal.
	 * @param stack
	 * @return
	 */
	public static ItemRecharger rawGet(ItemStack stack) {
		if (RECHARGER_CACHE.containsKey(stack)) {
			return RECHARGER_CACHE.get(stack);
		}
		return null;
	}
	
	/**
	 * Intended to be called every tick. This assumes a tick is 1/20th of a second, and does not adjust for lag.
	 */
	public void updateTick() {
		final double deltaTime = 0.05D;
		final int maxCost = rechargeableItem.getExperienceCostToRepair();
		final int repair = rechargeableItem.getDurabilityPerRestoreOp();
		
		speedCoefficient = clamp(speedCoefficient * (1 + deltaTime));
		final double ratio = maxCost * speedCoefficient;
		int expToTake = (int)(ratio);
		int currentExp = player.getExperienceReward(player);
		
		if (expToTake == 0) expToTake = 1;
		if (currentExp >= expToTake && itemReference.isDamaged()) {
			PlayerDataUtils.removeExperiencePoints(player, expToTake);
			itemReference.setDamageValue(itemReference.getDamageValue() - (int)(repair * speedCoefficient));
			currentExp = player.getExperienceReward(player);
		}
	}
	
	private double clamp(double value) {
		return Math.max(0, Math.min(1, value));
	}
	
	/**
	 * Unregisters this ItemRecharger.
	 */
	public void dispose() {
		RECHARGER_CACHE.remove(itemReference);
	}
}
