package etithespirit.etimod.info.spirit.capabilities;

import net.minecraft.nbt.CompoundNBT;

/**
 * Represents the actual capabilities of the player as a spirit, such as whether or not they are a spirit in the first
 * place, and what abilities they have or have not unlocked.
 *
 * @author Eti
 */
public final class SpiritCapabilities implements ISpiritCapabilities {
	
	private boolean isSpirit;
	
	private final SpiritGameAbilities abilities = new SpiritGameAbilities();
	
	@Override
	public void setIsSpirit(boolean isSpirit) {
		this.isSpirit = isSpirit;
	}
	
	@Override
	public boolean getIsSpirit() {
		return isSpirit;
	}
	
	@Override
	public SpiritGameAbilities getSpiritAbilities() {
		return abilities;
	}
	
	@Override
	public void copyTo(ISpiritCapabilities other) {
		other.setIsSpirit(isSpirit);
		abilities.copyTo(other.getSpiritAbilities());
	}
	
	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putBoolean("isSpirit", isSpirit);
		abilities.writeToNBT(tag);
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		isSpirit = nbt.getBoolean("isSpirit");
		abilities.readFromNBT(nbt);
	}
}
