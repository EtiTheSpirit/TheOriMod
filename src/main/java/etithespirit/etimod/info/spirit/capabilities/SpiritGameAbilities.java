package etithespirit.etimod.info.spirit.capabilities;

import etithespirit.etimod.util.nbt.ISimpleNBTSerializable;
import etithespirit.etimod.valuetypes.Trilean;
import net.minecraft.nbt.CompoundNBT;

/**
 * Every spirit ability.
 */
public final class SpiritGameAbilities implements ISimpleNBTSerializable {
	
	// spirit flame (probably not)
	public boolean wallJump;
	// charge flame (probably not)
	public Trilean airJump;
	// bash (probably not gonna be a thing)
	// stomp (idk)
	// wall climb (probably not gonna be a thing)
	// charge jump (or launch?)
	public boolean dash;
	// light burst (idk)
	
	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag.putBoolean("wallJump", wallJump);
		tag.putByte("airJump", airJump.toByte());
		tag.putBoolean("dash", dash);
		return tag;
	}
	
	@Override
	public void readFromNBT(CompoundNBT tag) {
		wallJump = tag.getBoolean("wallJump");
		airJump = Trilean.fromByte(tag.getByte("airJump"));
		dash = tag.getBoolean("dash");
	}
	
	public void copyTo(SpiritGameAbilities other) {
		other.wallJump = this.wallJump;
		other.airJump = this.airJump;
		other.dash = this.dash;
	}
	
}
