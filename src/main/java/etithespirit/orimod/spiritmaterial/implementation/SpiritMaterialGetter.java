package etithespirit.orimod.spiritmaterial.implementation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import etithespirit.exception.ArgumentNullException;
import etithespirit.orimod.api.spiritmaterial.SpiritMaterial;
import etithespirit.orimod.spiritmaterial.data.SpiritMaterialContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import oshi.util.tuples.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class SpiritMaterialGetter {
	
	private SpiritMaterialGetter() {}
	
	private static ImmutableMap<String, SpiritMaterialContainer> ALL_CONTAINERS_IMMUTABLE;
	private static final Map<StatePair, SpiritMaterialContainer> LAST_KNOWN_CONTAINERS = new HashMap<>();
	private static final Map<StatePair, SpiritMaterial> LAST_KNOWN_RESULTS = new HashMap<>();
	
	/**
	 * Using all known lookups for custom materials, this returns the appropriate material given an entity, the block it is standing on, and the block it is standing inside of.
	 * This attempts to cache as much of the result as possible for the sake of performance.
	 * @param entity The entity to check for.
	 * @param standingOn The block the entity is standing on top of.
	 * @param standingIn The block the entity is standing inside of.
	 * @return The appropriate material to use given the context of the entity and the blocks it is on top of and within.
	 */
	public static SpiritMaterial getMaterialFor(Entity entity, @Nonnull BlockPos standingOn, @Nonnull BlockPos standingIn) {
		ArgumentNullException.throwIfNull(entity, "entity");
		ArgumentNullException.throwIfNull(standingOn, "standingOn");
		ArgumentNullException.throwIfNull(standingIn, "standingIn");
		
		Level world = entity.getCommandSenderWorld();
		BlockState on = world.getBlockState(standingOn);
		BlockState in = world.getBlockState(standingIn);
		final StatePair these = new StatePair(on, in);
		
		if (ALL_CONTAINERS_IMMUTABLE == null) {
			ALL_CONTAINERS_IMMUTABLE = SpiritMaterialContainer.getAllContainers();
		}
		
		SpiritMaterial result = LAST_KNOWN_RESULTS.get(these);
		if (result != null) {
			return result;
		}
		
		SpiritMaterialContainer materialContainer = LAST_KNOWN_CONTAINERS.get(these);
		if (materialContainer != null) {
			return materialContainer.getMaterialFor(entity, standingOn, standingIn);
		}
		
		result = SpiritMaterial.INHERITED;
		for (String modId : ALL_CONTAINERS_IMMUTABLE.keySet()) {
			materialContainer = ALL_CONTAINERS_IMMUTABLE.get(modId);
			if (materialContainer == null) throw new NullPointerException();
			if (!materialContainer.shouldHandle(on, in)) continue;
			
			// Optimization: If the system reports that the material required no conditional handling (it was just
			// a lookup), then we can store the lookup here statically to prevent having to iterate every known handler.
			// This marginally reduces the cost of this function, at the expense of using more memory to keep track of
			// the results in a HashMap.
			result = materialContainer.getMaterialFor(entity, standingOn, standingIn);
			if (result != SpiritMaterial.INHERITED) {
				boolean canUseQuickCache = Boolean.FALSE.equals(materialContainer.requiresFullContext(on, in));
				
				if (canUseQuickCache) {
					LAST_KNOWN_RESULTS.put(these, result);
				} else {
					LAST_KNOWN_CONTAINERS.put(these, materialContainer);
				}
				
				return result;
			}
		}
		
		LAST_KNOWN_RESULTS.put(these, result);
		return result;
	}
	
	private record StatePair(BlockState first, BlockState second) {
		
		@Override
		public boolean equals(Object obj) {
			if (obj == this) return true;
			if (obj instanceof StatePair otherPair) {
				return Objects.equals(first, otherPair.first) && Objects.equals(second, otherPair.second);
			}
			return false;
		}
		
	}
	
	
}
