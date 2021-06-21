package etithespirit.etimod.util;

/**
 * 
 * Represents a type (physical/logical) of side (server/client).
 * @author Eti
 */
public enum SideType {
	
	/** A literal Minecraft game client app or server app. */
	PHYSICAL,
	
	/** A virtual representation of a side, which is not guaranteed to be the same as what program is running (for instance, singleplayer worlds have a logical server in them) */
	LOGICAL
	
}
