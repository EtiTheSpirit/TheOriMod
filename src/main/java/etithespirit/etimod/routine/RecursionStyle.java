package etithespirit.etimod.routine;

/**
 * Describes the manner in which recursion is performed.
 */
@Deprecated
public enum RecursionStyle {
	
	/**
	 * Recursion is round robin. Each entry point at the current depth is tested, and all potential children are added to a list.
	 * This process is repeated through that list, where only the children themselves are tested, and all test candidates are added to a list.
	 */
	ROUND_ROBIN,
	
	/**
	 * Simple recursion that navigates down to the lowest child it can.
	 */
	SEQUENTIAL
	
}
