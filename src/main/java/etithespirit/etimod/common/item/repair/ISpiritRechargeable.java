package etithespirit.etimod.common.item.repair;

/**
 * Denotes an item that can be "recharged" by a spirit player to repair it. This enforces the implementation of a number of methods present on Item.
 * @author Eti
 *
 */
public interface ISpiritRechargeable {
	
	/**
	 * Returns the amount of experience required to restore one durability point on this item relative to the % of one level.
	 * @return The cost in experience points to repair this item.
	 */
	default int getExperienceCostToRepair() {
		return 30;
	}
	
	/**
	 * For every time that the given amount of experience points (from {@link #getExperienceCostToRepair}) is spent, up to this many durability points are restored.
	 * @return The durabity repaired per repair operation.
	 */
	default int getDurabilityPerRestoreOp() {
		return 30;
	}
	
}