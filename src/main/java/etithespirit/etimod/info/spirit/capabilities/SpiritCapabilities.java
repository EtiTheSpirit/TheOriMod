package etithespirit.etimod.info.spirit.capabilities;

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
	
}
