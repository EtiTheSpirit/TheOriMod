package etithespirit.orimod.common.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public final class StaticData {
	
	public static final BlockBehaviour.StatePredicate FALSE_POSITION_PREDICATE = (x, y, z) -> false;
	
	public static final BlockBehaviour.StatePredicate TRUE_POSITION_PREDICATE = (x, y, z) -> true;
	
}
