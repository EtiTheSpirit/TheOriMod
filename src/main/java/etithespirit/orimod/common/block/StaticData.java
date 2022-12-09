package etithespirit.orimod.common.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

@SuppressWarnings("PointlessBitwiseExpression")
public final class StaticData {
	
	public static final BlockBehaviour.StatePredicate FALSE_POSITION_PREDICATE = (x, y, z) -> false;
	
	public static final BlockBehaviour.StatePredicate TRUE_POSITION_PREDICATE = (x, y, z) -> true;
	
	/** For use in setBlock() */ public static final int CAUSE_BLOCK_UPDATE = 1 << 0;
	/** For use in setBlock() */ public static final int REPLICATE_CHANGE = 1 << 1;
	/** For use in setBlock() */ public static final int DO_NOT_REDRAW = 1 << 2;
	/** For use in setBlock() */ public static final int REDRAW_ON_MAIN_THREAD = 1 << 3;
	/** For use in setBlock() */ public static final int DO_NOT_NOTIFY_NEIGHBORS = 1 << 4;
	/** For use in setBlock() */ public static final int DO_NOT_MAKE_NEIGHBORS_DROP = 1 << 5;
	/** For use in setBlock() */ public static final int BLOCK_BEING_MOVED = 1 << 6;
	
}
