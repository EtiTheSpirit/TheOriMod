package etithespirit.orimod.common.capabilities;

import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.chat.ChatHelper;
import etithespirit.orimod.common.chat.ExtendedChatColors;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.spirit.abilities.SpiritDashAbility;
import etithespirit.orimod.spirit.abilities.SpiritJumpAbility;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpiritCapabilities implements ICapabilitySerializable<CompoundTag> {
	public static final Capability<SpiritCapabilities> INSTANCE = CapabilityManager.get(new CapabilityToken<>(){});
	public static final ResourceLocation ID = OriMod.rsrc("spirit_state");
	private final LazyOptional<SpiritCapabilities> holder = LazyOptional.of(() -> this);
	
	/** This many ticks must be delayed before the player can dash again. */
	private static final int DASH_COOLDOWN_DURATION = 10;
	
	/** This many ticks before {@link #DASH_COOLDOWN_DURATION} will mark the appropriate time to restore pre-dash velocity. */
	private static final int DASH_RESTORE_BEFORE_COOLDOWN = 3;
	
	/** This many ticks must be delayed before the player can jump in the air again, as a debounce. */
	private static final int JUMP_COOLDOWN_DURATION = 2;
	
	private boolean isSpiritInternal;
	private SpiritDashAbility dashAbility;
	private SpiritJumpAbility airJumpAbility;
	private boolean canWallJump = false;
	private int trackedJumpsInAir = 0;
	private boolean hasAirDashed = false;
	private boolean wantsToCling = false;
	private int dashCooldownTicksRemaining = 0;
	private int jumpCooldownTicksRemaining = 0;
	private boolean hasSideRestoredPreDashVelocity = false;
	private Vec3 preDashVelocity = Vec3.ZERO;
	
	public SpiritCapabilities() {
		isSpiritInternal = OriModConfigs.DEFAULT_SPIRIT_STATE.get();
		airJumpAbility = OriModConfigs.KNOWN_JUMP_TYPE.get();
		dashAbility = OriModConfigs.KNOWN_DASH_TYPE.get();
		canWallJump = OriModConfigs.KNOW_WALL_JUMP.get();
	}
	
	public SpiritDashAbility getRawDash() {
		return dashAbility;
	}
	
	public SpiritJumpAbility getRawJump() {
		return airJumpAbility;
	}
	
	public boolean getRawCanWallJump() {
		return canWallJump;
	}
	
	/**
	 * Sets whether or not the player wants to be a spirit. Note that this does not necessarily
	 * make the appropriate change, as {@link #isSpirit()} changes its return value based on configs.
	 * @param isSpirit True if they should be a spirit, false if not.
	 */
	public void setSpirit(boolean isSpirit) {
		isSpiritInternal = isSpirit;
	}
	
	/**
	 * Sets the amount of jumps the spirit can perform.
	 * @param jump The jump enum.
	 * @throws ArgumentNullException If the given jump enum is null.
	 */
	public void setAirJumps(SpiritJumpAbility jump) throws ArgumentNullException {
		ArgumentNullException.throwIfNull(jump, "jump");
		airJumpAbility = jump;
	}
	
	/**
	 * Sets the manner in which the spirit can dash.
	 * @param dash The dash enum.
	 * @throws ArgumentNullException If the given dash enum is null.
	 */
	public void setDash(SpiritDashAbility dash) throws ArgumentNullException {
		ArgumentNullException.throwIfNull(dash, "dash");
		dashAbility = dash;
	}
	
	/**
	 * Sets whether or not the player can jump on walls (and by extension, cling to them).
	 * @param canWallJump Whether or not the player can wall jump.
	 */
	public void setCanWallJump(boolean canWallJump) {
		this.canWallJump = canWallJump;
	}
	
	/**
	 * Tells the system whether or not the spirit wants to cling to a wall. Returns true if the value was changed, false if it did not change.
	 * @param wantsToCling Whether or not the spirit wants to cling to a wall.
	 * @return True if the desired value was changed to something new, false if the input parameter was the same as the existing value.
	 */
	public boolean setClingDesire(boolean wantsToCling) {
		boolean retn = wantsToCling != this.wantsToCling;
		this.wantsToCling = wantsToCling;
		return retn;
	}
	
	/**
	 * Returns whether or not this spirit is trying to wall cling. This only works if they know wall jump.
	 * @return True if they can (and should) cling, false if not
	 */
	public boolean isClinging() {
		if (!isSpirit()) return false;
		return canWallJump && wantsToCling;
	}
	
	/**
	 * Tries to do an air jump. This applies no force, but returns whether or not an air jump was counted.
	 * Notably returns false if the player is on the ground.
	 * @param player The player to check.
	 * @return True if the player jumped explicitly while in the air, and while they had enough free jumps.
	 */
	public boolean tryAirJump(Player player) {
		if (!isSpirit()) return false;
		if (player.isOnGround()) {
			OriMod.LOG.debug("Cannot air jump; player is on the ground");
			return false;
		}
		if (jumpCooldownTicksRemaining > 0) {
			OriMod.LOG.debug("Cannot air jump; cooldown not over ({} ticks remaining)", jumpCooldownTicksRemaining);
			return false;
		}
		if (trackedJumpsInAir >= airJumpAbility.airJumps) {
			OriMod.LOG.debug("Cannot air jump; already used all available air jumps");
			return false;
		}
		trackedJumpsInAir++;
		OriMod.LOG.debug("Air jumped, currently at {} jumps in the air", trackedJumpsInAir);
		jumpCooldownTicksRemaining = JUMP_COOLDOWN_DURATION;
		return true;
	}
	
	public boolean tryWallJump(Player player) {
		if (!isSpirit()) return false;
		if (!canWallJump) {
			OriMod.LOG.debug("Cannot wall jump; spirit is not capable.");
			return false;
		}
		// if (player.isOnGround()) return false;
		if (jumpCooldownTicksRemaining > 0) {
			OriMod.LOG.debug("Cannot wall jump; cooldown not over ({} ticks remaining)", jumpCooldownTicksRemaining);
			return false;
		}
		jumpCooldownTicksRemaining = JUMP_COOLDOWN_DURATION;
		trackedJumpsInAir = 0; // Reset this!
		OriMod.LOG.debug("Wall jumped.");
		return true;
	}
	
	public int getAirJumpIndex() {
		return trackedJumpsInAir;
	}
	
	public boolean tryGroundDash(Player player) {
		if (!canDash()) {
			OriMod.LOG.debug("Cannot dash; spirit is incapable.");
			return false;
		}
		if (dashCooldownTicksRemaining > 0) {
			OriMod.LOG.debug("Cannot dash; cooldown not over ({} ticks remaining)", dashCooldownTicksRemaining);
			return false;
		}
		dashCooldownTicksRemaining = DASH_COOLDOWN_DURATION;
		hasSideRestoredPreDashVelocity = false;
		preDashVelocity = player.getDeltaMovement();
		OriMod.LOG.debug("Dashed.");
		return true;
	}
	
	public boolean tryWaterDash(Player player) {
		if (!canWaterDash()) {
			OriMod.LOG.debug("Cannot water dash; spirit is incapable.");
			return false;
		}
		if (dashCooldownTicksRemaining > 0) {
			OriMod.LOG.debug("Cannot dash; cooldown not over ({} ticks remaining)", dashCooldownTicksRemaining);
			return false;
		}
		dashCooldownTicksRemaining = DASH_COOLDOWN_DURATION;
		hasSideRestoredPreDashVelocity = false;
		preDashVelocity = player.getDeltaMovement();
		OriMod.LOG.debug("Water-dashed");
		return true;
	}
	
	public boolean tryAirDash(Player player) {
		if (!canAirDash()) {
			OriMod.LOG.debug("Cannot air dash; spirit is incapable.");
			return false;
		}
		if (hasAirDashed) {
			OriMod.LOG.debug("Cannot air dash; already air dashed, has not yet landed again.");
			return false;
		}
		if (dashCooldownTicksRemaining > 0) {
			OriMod.LOG.debug("Cannot air dash; cooldown not over ({} ticks remaining)", dashCooldownTicksRemaining);
			return false;
		}
		dashCooldownTicksRemaining = DASH_COOLDOWN_DURATION;
		hasAirDashed = true;
		hasSideRestoredPreDashVelocity = false;
		preDashVelocity = player.getDeltaMovement();
		OriMod.LOG.debug("Air-dashed");
		return true;
	}
	
	/**
	 * If necessary, this restores the last known pre-dash velocity.
	 */
	public void restoreDashVelocity(Player player) {
		if (hasSideRestoredPreDashVelocity) return;
		if (dashCooldownTicksRemaining > DASH_RESTORE_BEFORE_COOLDOWN) return;
		hasSideRestoredPreDashVelocity = true;
		double dvY = player.getDeltaMovement().y;
		player.setDeltaMovement(preDashVelocity.x, dvY, preDashVelocity.z);
	}
	
	public void landed() {
		trackedJumpsInAir = 0;
		hasAirDashed = false;
		jumpCooldownTicksRemaining = 0;
	}
	
	public void tick(Player player) {
		if (!isSpirit()) {
			jumpCooldownTicksRemaining = 0;
			dashCooldownTicksRemaining = 0;
			hasAirDashed = false;
			trackedJumpsInAir = 0;
			return;
		}
		if (dashCooldownTicksRemaining > 0) dashCooldownTicksRemaining--;
		if (jumpCooldownTicksRemaining > 0) jumpCooldownTicksRemaining--;
		restoreDashVelocity(player);
	}
	
	/**
	 * Returns whether or not this player is a spirit. This may be different than the internally stored value
	 * depending on server settings.
	 * @return Whether or not the player, given the current settings and state, should be a spirit.
	 */
	public boolean isSpirit() {
		if (OriModConfigs.FORCE_STATE.get()) return OriModConfigs.DEFAULT_SPIRIT_STATE.get();
		return isSpiritInternal;
	}
	
	/**
	 * Returns the number of jumps this player has whilst in the air. 1 is a double jump, 2 is a triple jump.
	 * This will never return a value outside of 0, 1, and 2.
	 * @return The amount of times the spirit can jump in the air.
	 */
	public int getNumberOfInAirJumps() {
		if (!isSpirit()) return 0;
		return airJumpAbility.airJumps;
	}
	
	/**
	 * @return Whether or not the spirit can dash.
	 */
	public boolean canDash() {
		if (!isSpirit()) return false;
		return dashAbility.canDash();
	}
	
	/**
	 * @return Whether or not the spirit can dash whilst in the air. Always returns false if {@link #canDash()} returns false.
	 */
	public boolean canAirDash() {
		return canDash() && dashAbility.canDashInAir();
	}
	
	/**
	 * @return Whether or not the spirit can dash whilst underwater. Always returns false if {@link #canDash()} returns false.
	 */
	public boolean canWaterDash() {
		return canDash() && dashAbility.canDashInWater();
	}
	
	/**
	 * Returns the capabilities for the given player.
	 * @param plr The player to check.
	 * @return The capabilities of this player as an optional.
	 */
	public static Optional<SpiritCapabilities> getCaps(Player plr) {
		return plr.getCapability(INSTANCE).resolve();
	}
	
	/**
	 * Returns the spirit capabilities of the given player, or null if they do not have these capabilities.
	 * @param plr
	 * @return
	 */
	public static @Nullable SpiritCapabilities getNullableCaps(Player plr) {
		Optional<SpiritCapabilities> caps = getCaps(plr);
		return caps.orElse(null);
	}
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
		return (cap == INSTANCE) ? INSTANCE.orEmpty(cap, holder) : LazyOptional.empty();
	}
	
	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		tag.putBoolean("isSpirit", isSpiritInternal);
		tag.putByte("airJumps", airJumpAbility.toByte());
		tag.putByte("dashFlags", dashAbility.toByte());
		tag.putBoolean("canWallJump", canWallJump);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		isSpiritInternal = nbt.getBoolean("isSpirit");
		airJumpAbility = SpiritJumpAbility.fromByte(nbt.getByte("airJumps"));
		dashAbility = SpiritDashAbility.fromByte(nbt.getByte("dashFlags"));
		canWallJump = nbt.getBoolean("canWallJump");
	}
	
	public Component dumpToComponent() {
		if (isSpirit()) {
			return Component.literal("SpiritCapabilities[").withStyle(ChatFormatting.WHITE)
				.append(ChatHelper.keyToValue("isSpirit", ChatHelper.ofBoolean(isSpirit())))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("jumpSettings", airJumpAbility.dumpToComponent(canWallJump)))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("dashSettings", dashAbility.dumpToComponent()))
				.append(Component.literal("]").withStyle(ChatFormatting.WHITE));
		} else {
			return Component.literal("SpiritCapabilities[").withStyle(ChatFormatting.WHITE)
				.append(ChatHelper.keyToValue("isSpirit", ChatHelper.ofBoolean(isSpirit())))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("jumpSettings", Component.literal("[N/A]").withStyle(ChatFormatting.DARK_GRAY)))
				.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				.append(ChatHelper.keyToValue("dashSettings", Component.literal("[N/A]").withStyle(ChatFormatting.DARK_GRAY)))
				.append(Component.literal("]").withStyle(ChatFormatting.WHITE));
		}
	}
}
