package etithespirit.orimod.api.spiritmaterial;

import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

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
	ASH,
	
	/** Corresponds to bones. Sounds like a pile of bones clattering together mixed with that solid surface of bone. */
	BONE,
	
	/** Corresponds to solid ceramics, like bricks or hardened clay. */
	CERAMIC_SOLID,
	
	/** Corresponds to broken, brittle, or cracked ceramics, like pottery or sandstone.*/
	CERAMIC_BROKEN,
	
	/** Corresponds to chitin, the solid material making up bug exoskeletons. */
	CHITIN,
	
	/** Corresponds to materials made from Spirit Light. This causes it to use the same sound as the hardlight platforms in the <em>Black Root Burrows</em>. */
	HARDLIGHT_GLASS("hardlight_glass"), // The name must be explicitly defined since it replaced _ with a dot by default unless a name override is declared.
	
	/** Corresponds to crispy foliage materials. */
	GRASS_HARD,
	
	/** Corresponds to soft foliage materials. */
	GRASS_SOFT,
	
	/** Corresponds to materials akin to gravel or loose pebbles. */
	GRAVEL_DRY,
	
	/** Corresponds to mushy wet gravel inside of muddy ground. Lots of water. */
	GRAVEL_WET,
	
	/** Corresponds to gravel with a layer of snow atop it. */
	GRAVEL_SNOWY,
	
	/** Corresponds to ice. */
	ICE,
	
	/** Corresponds to metal. */
	METAL,
	
	/** Corresponds to squishy, fleshy materials. */
	ORGANIC,
	
	/** Corresponds to solid rock and stone. */
	ROCK,
	
	/** Corresponds to sand, kind of sounds like a carpet. */
	SAND,
	
	/** Corresponds to fungus or other wet, mostly solid materials. For wet and soft/squishy materials, consider using {@link #ORGANIC}. */
	SHROOM,
	
	/** Corresponds to slimy materials, like mud or (obviously) slime. */
	SLIMY,
	
	/** Corresponds to snow. */
	SNOW,
	
	/** Corresponds to dry, crisp wood. */
	WOOD_DRY,
	
	/** Corresponds to woody materials covered in moss or other soft plant matter. */
	WOOD_MOSSY,
	
	/** Corresponds to woody materials covered in snow. */
	WOOD_SNOWY,
	
	/** Corresponds to wood with a thin layer of water atop it, good for use in rain. */
	WOOD_WET,
	
	/** Corresponds to shallow water like puddles. */
	WATER_SHALLOW,
	
	/** Corresponds to walkable deep water, think ankle deep. */
	WATER_DEEP,
	
	/** Corresponds to clothy materials. Derived from the WotW sand step sounds that I was able to find prior to the much larger datamining haul. */
	WOOL;
	
	/** If true, the vanilla sound for this material should be used instead. The vanilla sound in question is able to be determined via BlockToMaterialBinding. */
	public final boolean useVanillaInstead;
	
	/** The sound used when falling onto this material. This will probably be identical to {@link #stepSoundKey}, but is defined for some materials. */
	public final @Nullable String fallSoundKey;
	
	/** The sound used when stepping on this material. Only null in {@link #INHERITED}. */
	public final @Nullable String stepSoundKey;
	
	int isDeprecated = -1;
	
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
	
	/**
	 * Mostly for use in configuration builders, this determines if the sound is deprecated. This can be used by anyone for all I care though.
	 * @return True if this option is deprecated, false if it is not.
	 */
	public boolean deprecated() {
		if (isDeprecated == -1) {
			try {
				Field f = SpiritMaterial.class.getDeclaredField(name());
				if (f.getAnnotation(Deprecated.class) != null) {
					isDeprecated = 1;
				} else {
					isDeprecated = 0;
				}
			} catch (Exception ignored) {
				isDeprecated = 0;
			}
		}
		return isDeprecated == 1;
	}
	
	private static String toHumanFriendly(String strIn) {
		int length = strIn.length();
		if (length >= 2) {
			return strIn.substring(0, 1).toUpperCase() + strIn.substring(1).toLowerCase();
		}
		return strIn.toUpperCase();
	}
	
	@Override
	public String toString() {
		String baseName = name();
		if (baseName.contains("_")) {
			String[] components = baseName.split("_", 2);
			if (components.length == 2) {
				return toHumanFriendly(components[1]) + ' ' + toHumanFriendly(components[0]);
			} else {
				throw new IllegalStateException("Eti screwed up and had an enum name with more than one underscore in it for SpiritMaterial. Point and laugh! Also please report this bug at https://github.com/EtiTheSpirit/TheOriMod/issues/new?assignees=&labels=bug&template=bug-report.md&title=SpiritMaterial%20enum%20has%20an%20invalid%20name");
			}
		}
		return toHumanFriendly(baseName);
	}
}