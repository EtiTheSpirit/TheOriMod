package etithespirit.etimod.info.spirit.capabilities;

import etithespirit.etimod.util.nbt.ISimpleNBTSerializable;
import net.minecraft.nbt.CompoundNBT;

/**
 * Every spirit ability.
 */
public final class SpiritGameAbilities implements ISimpleNBTSerializable {
	
	/** Whether or not the player can jump up walls. */
	public boolean wallJump;
	
	/** The amount of jumps this player can perform whilst in the air. 0 means none, 1 means double jump, 2 means triple jump. */
	public byte airJump;
	
	/** Whether or not the player can use the Stomp ability. This has not been implemented. */
	@Deprecated public final boolean stomp = false;
	
	// Lock stomp and launch to be false under final so that it's really cemented in that they don't do anything.
	
	/** Whether or not the player can use charge jump / launch. This has not been implemented. */
	@Deprecated public final boolean launch = false;
	
	/** Whether or not the player can dash. */
	public boolean dash;
	
	
	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag.putBoolean("wallJump", wallJump);
		tag.putByte("airJump", airJump);
		// stomp
		// launch
		tag.putBoolean("dash", dash);
		return tag;
	}
	
	@Override
	public void readFromNBT(CompoundNBT tag) {
		wallJump = tag.getBoolean("wallJump");
		airJump = tag.getByte("airJump");
		// stomp
		// launch
		dash = tag.getBoolean("dash");
	}
	
	public void copyTo(SpiritGameAbilities other) {
		other.wallJump = this.wallJump;
		other.airJump = this.airJump;
		other.dash = this.dash;
	}
	
}
