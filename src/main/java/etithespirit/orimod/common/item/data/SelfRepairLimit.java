package etithespirit.orimod.common.item.data;

public enum SelfRepairLimit {
	NOT_ALLOWED(false, false),
	ALLOW_BUT_PREVENT_SUICIDE(true, false),
	ALLOW_WITHOUT_SAFEGUARDS(true, true);
	
	public final boolean canKillSelf;
	public final boolean allowed;
	
	SelfRepairLimit(boolean allow, boolean kill) {
		canKillSelf = kill;
		allowed = allow;
	}
}