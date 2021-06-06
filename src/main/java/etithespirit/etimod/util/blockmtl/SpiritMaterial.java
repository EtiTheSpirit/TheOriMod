package etithespirit.etimod.util.blockmtl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import etithespirit.etimod.registry.SoundRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public enum SpiritMaterial {
	
	/** Corresponds to no material (or Air) */
	NULL((Void)null),

	/** Corresponds to the vanilla material. This is used to tell the sound system to not bother with overrides. */
	INHERITED(),
	
	/** Corresponds to ash or basalt. */
	ASH(false),
	
	/** Corresponds to bones */
	BONE(false),
	
	/** Corresponds to solid ceramics, like bricks or hardened clay. */
	SOLID_CERAMICS(false, "ceramic.solid"),
	
	/** Corresponds to broken or cracked ceramics. */
	CRACKED_CERAMICS(false, "ceramic.broken"),
	
	/** Corresponds to chitin, the solid material making up bug exoskeletons. */
	CHITIN(false),
	
	/** Corresponds to glassy materials. This causes it to use the same sound as the hardlight platforms in the <em>Black Root Burrows</em>. */
	GLASS(false),
	
	/** Corresponds to crispy foliage materials. */
	GRASS_CRISP(false, "grass.hard"),
	
	/** Corresponds to soft foliage materials. */
	GRASS_SOFT(false, "grass.soft"),
	
	/** Corresponds to materials akin to gravel or loose pebbles. */
	GRAVEL_DRY(false, "gravel.dry"),
	
	/** Corresponds to mushy wet gravel inside of muddy ground. */
	GRAVEL_WET(false, "gravel.wet"),
	
	/** Corresponds to gravel with a layer of snow atop it. */
	GRAVEL_SNOWY(false, "gravel.snowy"),
	
	/** Corresponds to conditional gravel, which selects the appropriate gravel sound conditionally. */
	CONDITIONAL_GRAVEL(new SpiritMaterial[] {GRAVEL_DRY, GRAVEL_WET, GRAVEL_SNOWY}, new SpiritMaterialModState[] { SpiritMaterialModState.DRY, SpiritMaterialModState.WET, SpiritMaterialModState.SNOWY }),
	
	/** Corresponds to ice. */
	ICE(false),
	
	/** Corresponds to metal. */
	METAL(false),
	
	/** Corresponds to walking on insects. Squishy and gross. */
	INSECT(false, "organic"),
	
	/** Corresponds to solid rock and stone. */
	ROCK(false),
	
	/** Corresponds to sand, kind of sounds like a carpet. */
	SAND(false),
	
	/** Corresponds to fungus or other wet, mushy garbage. */
	SHROOM(false),
	
	/** Corresponds to slimy materials, like mud. */
	SLIMY(false),
	
	/** Corresponds to snow. */
	SNOW(false),
	
	/** Corresponds to dry, crisp wood. */
	WOOD_DRY(false, "wood.dry"),
	
	/** Corresponds to woody materials covered in moss or other soft plant matter. */
	WOOD_MOSSY(false, "wood.mossy"),
	
	/** Corresponds to woody materials covered in snow. */
	WOOD_SNOWY(false, "wood.snowy"),
	
	/** Corresponds to wood with a thin layer of water atop it, good for use in rain. */
	WOOD_WET(false, "wood.wet"),
	
	/** Corresponds to conditional wood, which will automatically select from the internal wood variants as needed. */
	CONDITIONAL_WOOD(new SpiritMaterial[] {WOOD_DRY, WOOD_MOSSY, WOOD_SNOWY, WOOD_WET}, new SpiritMaterialModState[] {SpiritMaterialModState.DRY, SpiritMaterialModState.MOSSY, SpiritMaterialModState.SNOWY, SpiritMaterialModState.WET}),
	
	/** Corresponds to shallow water like puddles. */
	WATER_SHALLOW(false, "water.shallow"),
	
	/** Corresponds to walkable deep water, think ankle deep. */
	WATER_DEEP(false, "water.deep"),
	
	/** Corresponds to conditional water, which will automatically select from shallow or deep water. */
	CONDITIONAL_WATER(new SpiritMaterial[] {WATER_SHALLOW, WATER_DEEP}, new SpiritMaterialModState[] {SpiritMaterialModState.SHALLOW, SpiritMaterialModState.DEEP}),
	
	/** Corresponds to clothy materials. Derived from the WotW sand step sounds (that I was able to find). */
	WOOL(false);
	
	/** If true, the vanilla sound for this material should be used instead. The vanilla sound in question is able to be determined via BlockToMaterialBinding. */
	public final boolean useVanillaInstead;
	
	/** If true, this sound is conditional, meaning getSound cannot return a singular sound event, and special processing needs to be done. */
	public final boolean conditional;
	
	/** The sound used when falling onto this material, or null if the step sound should be used. */
	@Deprecated public final @Nullable String fallSoundKey;
	
	/** The sound used when stepping on this material. */
	public final @Nullable String stepSoundKey;
	
	/** A lookup from a condition to a spirit material to use for said condition. Exists for conditional sounds only, and will be null otherwise. */
	public final @Nullable Map<SpiritMaterialModState, SpiritMaterial> materialLookup;
	
	/** If getSound has a null entity passed in, this is the material to return. */
	private final @Nullable SpiritMaterial materialIfLookupUnusable;
	
	/**
	 * Returns the registered sound event for this material. If isFallingOn is true but there's no fall sound, this will return null.
	 * @param isFallingOn
	 * @return The registered sound event associated with this material, or null if isFallingOn is true and there is no associated fall sound.
	 */
	@Deprecated
	public SoundEvent getSound(boolean isFallingOn) {
		if (conditional) throw new IllegalStateException("Cannot call getSound(boolean) on a sound classified as conditional! Reference materialLookup instead, or better yet, use the replacement getSound method.");
		if (isFallingOn) {
			if (fallSoundKey == null) {
				return null;
			} else {
				return SoundRegistry.get(fallSoundKey);
			}
		} else {
			return SoundRegistry.get(stepSoundKey);
		}
	}
	
	/**
	 * Returns the best-suited registered sound event for this material.
	 * @param forEntity This can be null if the material is not conditional. This is the entity to test.
	 * @param onBlock This can be null if the material is not conditional. This is the position of the block the entity is walking on.
	 * @param inBlock This can be null if the material is not conditional. This is the position of the block the entity is walking in.
	 * @return
	 */
	public SoundEvent getSound(Entity forEntity, BlockPos onBlock, BlockPos inBlock) {
		if (conditional) {
			if (forEntity == null) {
				// Return the first sound, which is the default.
				// Do a direct ref to eliminate the potential for a stack overflow.
				return SoundRegistry.get(materialIfLookupUnusable.stepSoundKey);
			} else {
				SpiritMaterialModState state = BlockToMaterialBinding.getStateWhen(forEntity, onBlock, inBlock);
				SpiritMaterial target = materialLookup.getOrDefault(state, materialIfLookupUnusable);
				return target.getSound(forEntity, onBlock, inBlock);
			}
		} else {
			return SoundRegistry.get(stepSoundKey);
		}
	}
	
	private SpiritMaterial(boolean hasUniqueFall) {
		this(hasUniqueFall, null);
	}
	
	private SpiritMaterial(boolean hasUniqueFall, String nameOverride) {
		if (nameOverride == null) nameOverride = name().toLowerCase();
		useVanillaInstead = false;
		materialIfLookupUnusable = null;
		conditional = false;
		materialLookup = null;
		stepSoundKey = "entity.spirit.step." + nameOverride;
		if (hasUniqueFall) {
			fallSoundKey = "entity.spirit.fall." + nameOverride;
		} else {
			fallSoundKey = stepSoundKey;
		}
	}
	
	private SpiritMaterial() {
		materialLookup = null;
		useVanillaInstead = true;
		materialIfLookupUnusable = null;
		conditional = false;
		fallSoundKey = null;
		stepSoundKey = null;
	}
	
	private SpiritMaterial(Void o) {
		materialLookup = null;
		useVanillaInstead = false;
		materialIfLookupUnusable = null;
		conditional = false;
		fallSoundKey = "nullsound";
		stepSoundKey = "nullsound";
	}
	
	private SpiritMaterial(SpiritMaterial[] materialList, SpiritMaterialModState[] stateList) {
		if (materialList.length != stateList.length) throw new IllegalArgumentException("List size mismatch for material list and state list.");
		useVanillaInstead = false;
		conditional = true;
		fallSoundKey = "nullsound";
		stepSoundKey = "nullsound";
		materialIfLookupUnusable = materialList[0];
		HashMap<SpiritMaterialModState, SpiritMaterial> lookup = new HashMap<SpiritMaterialModState, SpiritMaterial>();
		for (int index = 0; index < materialList.length; index++) {
			SpiritMaterial material = materialList[index];
			SpiritMaterialModState modState = stateList[index];
			lookup.put(modState, material);
		}
		materialLookup = lookup;
	}
	
}
