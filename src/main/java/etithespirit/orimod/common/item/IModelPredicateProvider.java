package etithespirit.orimod.common.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * An interface that allows items to provide one or more model predicates, like pull in bows, or blocking in shields.
 */
@FunctionalInterface
public interface IModelPredicateProvider {
	/**
	 * This method adds predicates to an item by modifying the map of known predicates from this mod.
	 * @param predicates The map of known predicates created by the custom registry.
	 */
	void getPredicates(Map<ResourceLocation, ItemPropertyFunction> predicates);
}
