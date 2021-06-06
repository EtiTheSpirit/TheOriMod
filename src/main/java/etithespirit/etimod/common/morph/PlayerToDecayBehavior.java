package etithespirit.etimod.common.morph;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;

/**
 * Maps players to specific decay behavior for how to acquire the effect.
 * @author Eti
 *
 */
public class PlayerToDecayBehavior {
	
	/**
	 * Maps player IDs to an Byte value describing how Decay should affect them.
	 * 0bLLCCCCBM = (L = effect level, range from 0 to 3, C = chance, range from 0 to 15, B = infect in biome, M = infect in mycelium)
	 */
	public static final Map<UUID, Byte> bindings = new HashMap<UUID, Byte>();
	
	public static final byte MASK_EFFECT_LEVEL = (byte) 0b11000000;
	
	public static final byte MASK_CHANCE = (byte) 0b00111100;
	
	public static final byte MASK_BIOME = (byte) 0b00000010;
	
	public static final byte MASK_BLOCK = (byte) 0b00000001;
	
	/**
	 * Shifts the bits of the input chance value to match the location of those in the conglomerate byte value. It will be clamped between 0 and 15.
	 * @param chance
	 * @return
	 */
	public static byte chanceToEncoded(byte chance) {
		int c = Math.min(Math.max(chance, 0), 15);
		c = c << 2;
		return (byte)c;
	}
	
	public static byte encodedToChance(byte encoded) {
		int v = (encoded & 0b00111100) >> 2;
		return (byte)v;
	}
	
	/**
	 * Shifts the bits of the input level to match the location of those in the conglomerate byte value. It will be clamped between 0 and 3.
	 * @param level
	 * @return
	 */
	public static byte effectLevelToEncoded(byte level) {
		int l = Math.min(Math.max(level, 0), 3);
		l = l << 6;
		return (byte)l;
	}
	
	public static byte encodedToEffectLevel(byte encoded) {
		int v = (encoded & 0b11000000) >> 6;
		return (byte)v;
	}
	
	public static byte biomeToEncoded(boolean infectOnBiome) {
		return (byte)(infectOnBiome ? 2 : 0);
	}
	
	public static boolean encodedToBiome(byte encoded) {
		return (encoded & 2) == 2;
	}
	
	public static byte blockToEncoded(boolean infectOnBlock) {
		return (byte)(infectOnBlock ? 1 : 0);
	}
	
	public static boolean encodedToBlock(byte encoded) {
		return (encoded & 1) == 1;
	}
	
	/**
	 * Sets the associated values for the player's data. Set any parameters to null to inherit their existing value.
	 * @param player
	 * @param infectInBiome
	 * @param infectInBlock
	 * @param infectChance
	 * @param infectLevel
	 */
	public static void put(PlayerEntity player, Boolean infectInBiome, Boolean infectInBlock, Byte infectChance, Byte infectLevel) {
		put(player.getUniqueID(), infectInBiome, infectInBlock, infectChance, infectLevel);
	}

	public static void put(UUID id, Boolean infectInBiome, Boolean infectInBlock, Byte infectChance, Byte infectLevel) {
		byte existingValue = rawGet(id);
		
		boolean biome = infectInBiome != null ? infectInBiome.booleanValue() : encodedToBiome(existingValue);
		boolean block = infectInBlock != null ? infectInBlock.booleanValue() : encodedToBlock(existingValue);
		byte chance = infectChance != null ? infectChance.byteValue() : encodedToChance(existingValue);
		byte level = infectLevel != null ? infectLevel.byteValue() : encodedToEffectLevel(existingValue);
		
		int newValue = biomeToEncoded(biome) | blockToEncoded(block) | chanceToEncoded(chance) | effectLevelToEncoded(level);
		rawPut(id, (byte)newValue);
	}
	
	
	public static void rawPut(UUID id, byte value) {
		if (id == null) return;
		bindings.put(id, value);
	}
	
	public static void rawPut(PlayerEntity player, byte value) {
		if (player == null) return;
		rawPut(player.getUniqueID(), value);
	}
	
	public static byte rawGet(PlayerEntity player) {
		if (player == null) return 0;
		return rawGet(player.getUniqueID());
	}
	
	public static byte rawGet(UUID id) {
		if (id == null) return 0;
		if (bindings.containsKey(id)) return bindings.get(id);
		return 0;
	}

}
