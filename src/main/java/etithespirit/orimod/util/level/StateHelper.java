package etithespirit.orimod.util.level;

import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.util.TypeErasure;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;

public final class StateHelper {
	
	private StateHelper() {}
	
	/**
	 * Sets the given {@link StateHolder}, which is expected to be a {@link BlockState} or {@link FluidState}, in the world at the given position.
	 * @param world The world to modify.
	 * @param at The location in the world to modify.
	 * @param state The new block or fluid.
	 */
	public static void setBlockAndUpdateIn(Level world, BlockPos at, StateHolder<?, ?> state) {
		if (state instanceof BlockState block) {
			world.setBlockAndUpdate(at, block);
		} else if (state instanceof FluidState fluid) {
			world.setBlockAndUpdate(at, fluid.createLegacyBlock());
		} else {
			throw new UnsupportedOperationException("The given StateHolder instance is not a BlockState or FluidState, and has not been designed to handle this.");
		}
	}
	
	/**
	 * If the given location in the given world has a fluid, this returns its {@link FluidState}. Otherwise, this returns the {@link BlockState}.
	 * @param world The world to read from.
	 * @param at The location to read at.
	 * @return The fluid at that location, or the block if no fluid is present there.
	 */
	public static StateHolder<?, ?> getFluidOrBlock(BlockGetter world, BlockPos at) {
		FluidState fluid = world.getFluidState(at);
		if (fluid.isEmpty()) {
			return world.getBlockState(at);
		}
		return fluid;
	}
	
	/**
	 * A utility to chain setValue calls on an anonymous {@link StateHolder} into a single method call. For compliance with the method in use, values must be something that implements {@link Comparable} (which all properties do).
	 * @param state The {@link StateHolder} to modify.
	 * @param keysAndValues An ordered list of keys and values, in the order key0, value0, key1, value1, ...
	 * @return The {@link StateHolder} this was called on
	 */
	public static StateHolder<?, ?> setManyValues(StateHolder<?, ?> state, Object... keysAndValues) throws IllegalArgumentException, ArgumentNullException {
		if ((keysAndValues.length & 1) != 0) throw new IllegalArgumentException("The length of the keysAndValues array is not even.");
		for (int index = 0; index < keysAndValues.length; index += 2) {
			Object key = keysAndValues[index];
			Object value = keysAndValues[index + 1];
			state.setValue((Property<?>)key, TypeErasure.eraseAndTreatAsGeneric(value));
		}
		return state;
	}
	
	/**
	 * A utility to chain setValue calls on an anonymous {@link StateHolder} into a single method call. For compliance with the method in use, values must be something that implements {@link Comparable} (which all properties do).
	 * @param state The {@link StateHolder} to modify.
	 * @param key The first key
	 * @param value The first value
	 * @return The {@link StateHolder} this was called on
	 */
	public static StateHolder<?, ?> setManyValues(StateHolder<?, ?> state, Property<?> key, Comparable<?> value) {
		state.setValue(key, TypeErasure.eraseAndTreatAsGeneric(value));
		return state;
	}
	
}
