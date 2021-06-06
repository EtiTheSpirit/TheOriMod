package etithespirit.etimod.util.spirit;

/**
 * Contains three identifying values:
 * <ul>
 * <li>FROM_PLAYER_MODEL, which checks if the given entity is a player and their UUID is registered as a spirit.</li>
 * <li>FROM_ENTITY, which checks if the actual entity's class is a spirit.</li>
 * <li>FROM_POTION_EFFECT, which checks if the entity has the spirit identification effect.</li>
 * </ul>
 * These values can be OR'd together e.g. {@code FROM_PLAYER_MODEL | FROM_ENTITY} will check both of those flags.
 * @author Eti
 *
 */
public class SpiritIdentificationType {
	
	public static final int FROM_PLAYER_MODEL = 1 << 0;
	
	public static final int FROM_ENTITY = 1 << 1;
	
	public static final int FROM_POTION_EFFECT = 1 << 2;
	
}
