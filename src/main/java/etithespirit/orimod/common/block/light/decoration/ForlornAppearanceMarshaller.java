package etithespirit.orimod.common.block.light.decoration;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.function.Consumer;

/**
 * This utility class provides the methods that a Forlorn-style block uses to render, including its color (blue vs. orange) and powered state.
 */
public final class ForlornAppearanceMarshaller {
	
	public static final int LIGHT_LEVEL = 3;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty IS_BLUE = BooleanProperty.create("blue");
	
	
	public static void autoRegisterDefaultState(Consumer<BlockState> initializer, StateDefinition<Block, BlockState> stateContainer) {
		initializer.accept(stateContainer.any().setValue(POWERED, false).setValue(IS_BLUE, false));
	}
	
	public static StateDefinition.Builder<Block, BlockState> autoCreateBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(POWERED, IS_BLUE);
		return builder;
	}
}
