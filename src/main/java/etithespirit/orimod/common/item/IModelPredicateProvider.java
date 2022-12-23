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
 * This item provides a model predicate, like pull in bows, or blocking in shields.
 */
@FunctionalInterface
public interface IModelPredicateProvider {
	
	void getPredicates(Map<ResourceLocation, ItemPropertyFunction> predicates);
}
