package etithespirit.etimod.common.block;

import net.minecraft.block.AbstractBlock.IPositionPredicate;

public final class StaticData {
	
	/**
	 * An IPositionPredicate instance that always returns false.
	 */
	public static final IPositionPredicate FALSE_POSITION_PREDICATE = (a, b, c) -> false;
	
	/**
	 * An IPositionPredicate instance that always returns true.
	 */
	public static final IPositionPredicate TRUE_POSITION_PREDICATE = (a, b, c) -> true;

}
