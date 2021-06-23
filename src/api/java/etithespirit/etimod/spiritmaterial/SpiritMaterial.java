package etithespirit.etimod.spiritmaterial;

import javax.annotation.Nullable;

/**
 * All materials that are associated with Spirits walking on something. Their little hooves tend to make a wide array
 * of unique sounds in the world around them.
 *
 * @author Eti.
 */
public enum SpiritMaterial {
	
	/** Corresponds to no material (or Air) */
	NULL(false),

	/** Corresponds to the vanilla material. This is used to tell the sound system to not bother with overrides. */
	INHERITED(true),
	
	/** Corresponds to ash or basalt. Has a deep, soft impact sound. Think stepping on a charred log in real life. */
	ASH(),
	
	/** Corresponds to bones. Sounds like a pile of bones clattering together mixed with that solid surface of bone. */
	BONE(),
	
	/** Corresponds to solid ceramics, like bricks or hardened clay. */
	SOLID_CERAMICS("ceramic.solid"),
	
	/** Corresponds to broken or cracked ceramics, like pottery or sandstone. */
	CRACKED_CERAMICS("ceramic.broken"),
	
	/** Corresponds to chitin, the solid material making up bug exoskeletons. */
	CHITIN(),
	
	/** Corresponds to glassy materials. This causes it to use the same sound as the hardlight platforms in the <em>Black Root Burrows</em>, so it's more suitable for that. */
	GLASS(),
	
	/** Corresponds to crispy foliage materials. */
	GRASS_CRISP("grass.hard"),
	
	/** Corresponds to soft foliage materials. */
	GRASS_SOFT("grass.soft"),
	
	/** Corresponds to materials akin to gravel or loose pebbles. */
	GRAVEL_DRY("gravel.dry"),
	
	/** Corresponds to mushy wet gravel inside of muddy ground. Lots of water. */
	GRAVEL_WET("gravel.wet"),
	
	/** Corresponds to gravel with a layer of snow atop it. */
	GRAVEL_SNOWY("gravel.snowy"),
	
	/** Corresponds to ice. */
	ICE(),
	
	/** Corresponds to metal. */
	METAL(),
	
	/** Corresponds to walking on insects. Squishy and gross. */
	INSECT("organic"),
	
	/** Corresponds to solid rock and stone. */
	ROCK(),
	
	/** Corresponds to sand, kind of sounds like a carpet. */
	SAND(),
	
	/** Corresponds to fungus or other wet, mushy, but not really squishy stuff. */
	SHROOM(),
	
	/** Corresponds to slimy materials, like mud or (obviously) slime. */
	SLIMY(),
	
	/** Corresponds to snow. */
	SNOW(),
	
	/** Corresponds to dry, crisp wood. */
	WOOD_DRY("wood.dry"),
	
	/** Corresponds to woody materials covered in moss or other soft plant matter. */
	WOOD_MOSSY("wood.mossy"),
	
	/** Corresponds to woody materials covered in snow. */
	WOOD_SNOWY("wood.snowy"),
	
	/** Corresponds to wood with a thin layer of water atop it, good for use in rain. */
	WOOD_WET("wood.wet"),
	
	/** Corresponds to shallow water like puddles. */
	WATER_SHALLOW("water.shallow"),
	
	/** Corresponds to walkable deep water, think ankle deep. */
	WATER_DEEP("water.deep"),
	
	/** Corresponds to clothy materials. Derived from the WotW sand step sounds that I was able to find prior to the much larger datamining haul. */
	WOOL();
	
	/** If true, the vanilla sound for this material should be used instead. The vanilla sound in question is able to be determined via BlockToMaterialBinding. */
	public final boolean useVanillaInstead;
	
	/** The sound used when falling onto this material, or null if the step sound should be used. */
	public final @Nullable String fallSoundKey;
	
	/** The sound used when stepping on this material. Only null in {@link #INHERITED}. */
	public final @Nullable String stepSoundKey;
	
	SpiritMaterial() {
		this(false, null);
	}
	
	SpiritMaterial(String nameOverride) {
		this(false, nameOverride);
	}
	
	SpiritMaterial(boolean hasUniqueFall, @Nullable String nameOverride) {
		if (nameOverride == null) nameOverride = name().toLowerCase();
		useVanillaInstead = false;
		stepSoundKey = "entity.spirit.step." + nameOverride;
		if (hasUniqueFall) {
			fallSoundKey = "entity.spirit.fall." + nameOverride;
		} else {
			fallSoundKey = stepSoundKey;
		}
	}
	
	/**
	 * Only for use in NULL (false) and INHERITED (true).
	 * @param useVanillaInstead The sounds should use vanilla sounds instead.
	 */
	SpiritMaterial(boolean useVanillaInstead) {
		this.useVanillaInstead = useVanillaInstead;
		fallSoundKey = useVanillaInstead ? null : "nullsound";
		stepSoundKey = useVanillaInstead ? null : "nullsound";
	}
}
