package etithespirit.orimod.api.spiritmaterial;

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
	CERAMIC_SOLID(),
	
	/** Corresponds to broken, brittle, or cracked ceramics, like pottery or sandstone.*/
	CERAMIC_BROKEN(),
	
	/** Corresponds to chitin, the solid material making up bug exoskeletons. */
	CHITIN(),
	
	/** Corresponds to glassy materials. This causes it to use the same sound as the hardlight platforms in the <em>Black Root Burrows</em>, so it's more suitable for that. */
	GLASS(),
	
	/** Corresponds to crispy foliage materials. */
	GRASS_CRISP(),
	
	/** Corresponds to soft foliage materials. */
	GRASS_SOFT(),
	
	/** Corresponds to materials akin to gravel or loose pebbles. */
	GRAVEL_DRY(),
	
	/** Corresponds to mushy wet gravel inside of muddy ground. Lots of water. */
	GRAVEL_WET(),
	
	/** Corresponds to gravel with a layer of snow atop it. */
	GRAVEL_SNOWY(),
	
	/** Corresponds to ice. */
	ICE(),
	
	/** Corresponds to metal. */
	METAL(),
	
	/** Corresponds to squishy, fleshy materials. */
	ORGANIC(),
	
	/** Corresponds to solid rock and stone. */
	ROCK(),
	
	/** Corresponds to sand, kind of sounds like a carpet. */
	SAND(),
	
	/** Corresponds to fungus or other wet, mostly solid materials. For wet and soft/squishy materials, consider using {@link #ORGANIC}. */
	SHROOM(),
	
	/** Corresponds to slimy materials, like mud or (obviously) slime. */
	SLIMY(),
	
	/** Corresponds to snow. */
	SNOW(),
	
	/** Corresponds to dry, crisp wood. */
	WOOD_DRY(),
	
	/** Corresponds to woody materials covered in moss or other soft plant matter. */
	WOOD_MOSSY(),
	
	/** Corresponds to woody materials covered in snow. */
	WOOD_SNOWY(),
	
	/** Corresponds to wood with a thin layer of water atop it, good for use in rain. */
	WOOD_WET(),
	
	/** Corresponds to shallow water like puddles. */
	WATER_SHALLOW(),
	
	/** Corresponds to walkable deep water, think ankle deep. */
	WATER_DEEP(),
	
	/** Corresponds to clothy materials. Derived from the WotW sand step sounds that I was able to find prior to the much larger datamining haul. */
	WOOL();
	
	/** If true, the vanilla sound for this material should be used instead. The vanilla sound in question is able to be determined via BlockToMaterialBinding. */
	public final boolean useVanillaInstead;
	
	/** The sound used when falling onto this material. This will probably be identical to {@link #stepSoundKey}, but is defined for some materials. */
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
		if (nameOverride == null) nameOverride = name().toLowerCase().replace('_', '.');
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