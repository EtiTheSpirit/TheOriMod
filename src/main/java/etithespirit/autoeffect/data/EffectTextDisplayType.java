package etithespirit.autoeffect.data;

/**
 * A preset inventory display type for potion effects. This dictates how the banner on the left side of the screen is rendered while the player's inventory is open,
 * and offers an easy way of providing rendering styles that are beneficial for a number of cases.<br/>
 * <br/>
 * These options affect the text and timer. This condition is tested in the default render methods, so if you override them, you will need to do those checks yourself.
 * @author Eti
 *
 */
public enum EffectTextDisplayType {
	
	/** No special rendering will be applied. The text and timer will render just like vanilla code, with the exception of using custom colors if you have defined them. */
	STOCK,
	
	/** The system will draw the name only. The timer will be omitted, and the name text will be offset to be centered within the rectangular frame. */
	NAME_ONLY,
	
	/** The system will draw the time only. The name will be omitted, and the timer text will be offset to be centered within the rectangular frame. If GetSubtitle is overridden, only the subtitle will be displayed. */
	TIME_ONLY,
	
	/** Completely omit all text, leaving at as icon and the rectangle. This will probably look really awkward unless you add something to fill the space or replace the rectangle with a square that fits the icon, something like that. */
	NO_TEXT
	
}
