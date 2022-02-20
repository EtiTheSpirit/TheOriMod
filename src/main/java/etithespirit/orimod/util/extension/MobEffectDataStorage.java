package etithespirit.orimod.util.extension;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * A very hacky method of reading and writing data in a single effect instance without creating a custom instance type.
 * This oddball method that is alarmingly compatible with vanilla code was given to me by diesieben07, so kudos to him!
 *
 * @author Eti
 */
public final class MobEffectDataStorage {
	
	private MobEffectDataStorage() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	public static final String DATA_STORAGE_TAG_NAME = "customData";
	
	/**
	 * Gets a {@link CompoundNBT} storing data for the given {@link MobEffectInstance}. This also creates new data if the instance doesn't have it.<br/>
	 * <strong>Behaviorally, this adds air as a curative item (though this does nothing).</strong> Modifying the curative items array MUST retain this dummy air item, or else the data will be wiped or malformed.
	 * @param instance The {@link MobEffectInstance} that the data pertains to.
	 * @return A {@link CompoundNBT} with the data for the given {@link MobEffectInstance}
	 */
	public static CompoundTag accessData(MobEffectInstance instance) {
		for (ItemStack stack : instance.getCurativeItems()) {
			if (stack.getItem().equals(Items.AIR)) {
				return stack.getOrCreateTagElement(DATA_STORAGE_TAG_NAME);
			}
		}
		
		List<ItemStack> curatives = new ArrayList<>();
		curatives.addAll(instance.getCurativeItems());
		
		ItemStack dummy = new ItemStack(Items.AIR);
		curatives.add(dummy);
		instance.setCurativeItems(curatives);
		return dummy.getOrCreateTagElement(DATA_STORAGE_TAG_NAME);
	}
	
	/**
	 * An alias method that returns a custom max duration value, or, the duration this effect started with.
	 * @param instance The effect to look at.
	 * @return A value representing the maximum duration, or 0 if it was never registered.
	 */
	public static int getMaxDuration(MobEffectInstance instance) {
		CompoundTag data = accessData(instance);
		if (data.contains("maxDuration")) {
			return data.getInt("maxDuration");
		}
		return 0;
	}
	
	/**
	 * An alias method that sets a custom max duration value, or, the duration this effect started with.
	 * @param instance The effect to modify.
	 * @param maxDuration The maximum duration of this effect instance itself (how much time it had when instantiated)
	 */
	public static void setMaxDuration(MobEffectInstance instance, int maxDuration) {
		CompoundTag data = accessData(instance);
		data.putInt("maxDuration", maxDuration);
	}
	
	/**
	 * An alias method that adds a custom max duration value, or, the duration this effect started with. Does nothing if this does not have the data registered.
	 * @param instance The effect to modify.
	 * @param offset An increase to the maximum duration of this effect instance itself (how much time it had when instantiated). If the effect's duration has been expanded, then this should go up.
	 */
	public static void addMaxDuration(MobEffectInstance instance, int offset) {
		setMaxDuration(instance, getMaxDuration(instance) + offset);
		// If it doesn't have a duration, this is identical to calling
		// set with the given offset value (since get returns 0 for missing data)
	}
	
}
