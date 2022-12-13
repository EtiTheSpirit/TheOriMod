package etithespirit.orimod.networking.player;

public final class MovementReplicationPacket {
	
	public int impulseLeft;
	public int impulseForward;
	public boolean jumped;
	public int setClinging;
	
	public boolean isActionToSwitchCling() {
		return setClinging != 0;
	}
	public boolean setClingTo() {
		if (setClinging == 1) return false;
		if (setClinging == 2) return true;
		throw new IllegalStateException("This packet does not intend to set clinging to true or false.");
	}
	
	
}
