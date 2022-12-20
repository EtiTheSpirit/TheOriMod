package etithespirit.mixin.mixins;

import etithespirit.orimod.common.tile.WorldUpdateListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Stack;

/**
 * This mixin was created to optimize the behavior that is used by conduits so that they may more accurately determine when/if they should (re)connect
 * to neighbors by providing context about what specific state used to exist before a change, and what state exists after a change.
 */
@Mixin(Level.class)
public abstract class InjectSetBlockAndUpdate extends net.minecraftforge.common.capabilities.CapabilityProvider<Level> {
	private InjectSetBlockAndUpdate(Class<Level> baseClass) {
		super(baseClass);
	}
	
	// TODO: This is a terrible idea in general and I need a way to keep track of the block from before the change.
	// Is there any sensible way to do this?
	private static final Stack<BlockState> BEFORE_CACHE = new Stack<>();
	
	@Shadow
	public abstract BlockState getBlockState(BlockPos at);
	
	@Shadow
	public abstract boolean setBlock(BlockPos at, BlockState newState, int flags, int recursion);
	
	/**
	 * Intercepts calls to setBlock in worlds and allows objects to react to the change before it occurs. Does not allow canceling.<br/>
	 * This strictly executes if the flags are for a client replication and a block update (as well as any other flags on top of those two).
	 * @param pPos The position to update.
	 * @param pNewState The replacement state.
	 * @param pFlags The update flags. For more information on these, see {@link Level#setBlock(BlockPos, BlockState, int)}.
	 * @param cir Mixin callback info
	 */
	@Inject (
		method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
		at = @At("HEAD")
	)
	public void setBlock$notifyBefore(BlockPos pPos, BlockState pNewState, int pFlags, CallbackInfoReturnable<Boolean> cir) {
		if ((pFlags & 3) == 3) {
			WorldUpdateListener.notifyOfUpdatingBlockChangeBefore(pPos, BEFORE_CACHE.push(this.getBlockState(pPos)), pNewState);
		}
	}
	
	/**
	 * Intercepts calls to setBlock in worlds and allows objects to react to the change after it occurs. Does not allow canceling.<br/>
	 * This strictly executes if the flags are for a client replication and a block update (as well as any other flags on top of those two).
	 * @param pPos The position to update.
	 * @param pNewState The replacement state.
	 * @param pFlags The update flags. For more information on these, see {@link Level#setBlock(BlockPos, BlockState, int)}.
	 * @param cir Mixin callback info
	 */
	@Inject (
		method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z",
		at = @At("RETURN")
	)
	public void setBlock$notifyAfter(BlockPos pPos, BlockState pNewState, int pFlags, CallbackInfoReturnable<Boolean> cir) {
		if ((pFlags & 3) == 3) {
			WorldUpdateListener.notifyOfUpdatingBlockChangeAfter(pPos, BEFORE_CACHE.pop(), pNewState);
		}
	}
	
}
