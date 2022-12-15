package etithespirit.mixin.mixins;

import etithespirit.mixin.helpers.ISelfProvider;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin intercepts the canEat method to prevent Spirits from eating meat.
 */

//@Mixin(Item.class)
public abstract class ItemIsEdible implements ItemLike, net.minecraftforge.common.extensions.IForgeItem, ISelfProvider {
	
	//@Inject(method="isEdible()Z", at=@At("HEAD"), cancellable = true)
	public void isEdible$WhenLimitedByDiet(boolean canAlwaysEat, CallbackInfoReturnable<Boolean> ci) {
		if (canAlwaysEat) {
			ci.setReturnValue(true);
			return;
		}
		Item thisItem = selfProvider$item();
		//if (OriModConfigs.ONLY_EAT_PLANTS.get()) {
			// TODO: Better solution than this. This is not guaranteed to work with any modded food items.
			FoodProperties props = thisItem.getFoodProperties();
			if (props != null && props.isMeat()) {
				ci.setReturnValue(false);
				return;
			}
		//}
	}
}
