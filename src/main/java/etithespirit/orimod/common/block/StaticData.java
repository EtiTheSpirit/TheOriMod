package etithespirit.orimod.common.block;

import etithespirit.orimod.common.chat.ExtendedChatColors;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.state.BlockBehaviour;

@SuppressWarnings("PointlessBitwiseExpression")
public final class StaticData {
	
	public static final BlockBehaviour.StatePredicate ALWAYS_FALSE = (x, y, z) -> false;
	
	public static final BlockBehaviour.StatePredicate ALWAYS_TRUE = (x, y, z) -> true;
	
	/** For use in setBlock() */ public static final int CAUSE_BLOCK_UPDATE = 1 << 0;
	/** For use in setBlock() */ public static final int REPLICATE_CHANGE = 1 << 1;
	/** For use in setBlock() */ public static final int DO_NOT_REDRAW = 1 << 2;
	/** For use in setBlock() */ public static final int REDRAW_ON_MAIN_THREAD = 1 << 3;
	/** For use in setBlock() */ public static final int DO_NOT_NOTIFY_NEIGHBORS = 1 << 4;
	/** For use in setBlock() */ public static final int DO_NOT_MAKE_NEIGHBORS_DROP = 1 << 5;
	/** For use in setBlock() */ public static final int BLOCK_BEING_MOVED = 1 << 6;
	
	
	/**
	 * An alias for use in all Decay-related objects that colors the name to match the theme.
	 * @param superGetName The result of the call to <code>super.getName</code>
	 * @return The same result, but colored differently.
	 */
	public static MutableComponent getNameAsDecay(Component superGetName) {
		return ((MutableComponent)superGetName).withStyle(ExtendedChatColors.DECAY);
	}
	
	/**
	 * An alias for use in all Light-related objects that colors the name to match the theme.
	 * @param superGetName The result of the call to <code>super.getName</code>
	 * @return The same result, but colored differently.
	 */
	public static MutableComponent getNameAsLight(Component superGetName) {
		return ((MutableComponent)superGetName).withStyle(ExtendedChatColors.LIGHT);
	}
	
}
