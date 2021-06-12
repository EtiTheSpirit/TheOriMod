package etithespirit.etimod.routine;

/**
 * What to do for a given child in recursion.
 */
@Deprecated
public enum RecursionAction {
	
	/**
	 * This child satisfies some condition to stop, or is invalid. Terminate recursion for this child.
	 */
	TERMINATE,
	
	/**
	 * This child does not meet any conditions, but must be searched for more information. Continue onward.
	 */
	CONTINUE,
	
	/**
	 * This child satisfies a condition that is favorable. Store this in the result array.
	 */
	SUCCESS,
	
}