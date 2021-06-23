package etithespirit.etimod.common.player;

import java.util.ArrayList;
import etithespirit.etimod.common.potion.SpiritEffect;
import etithespirit.etimod.info.spirit.SpiritData;
import etithespirit.etimod.registry.PotionRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

public class EffectEnforcement {
	
	/** An empty ArrayList of items. This is used to define the list of objects that can cure the potion, which should be nothing. */
	private static final ArrayList<ItemStack> EMPTY_LIST = new ArrayList<>();

	public static void enforceEffects(PlayerTickEvent event) {
		SpiritEffect spiritEffect = (SpiritEffect) PotionRegistry.get(SpiritEffect.class);
		EffectInstance instance = event.player.getEffect(spiritEffect);
		if (SpiritData.isSpirit(event.player)) {
			if (instance == null) {
				EffectInstance spiritTag = spiritEffect.constructInfiniteEffect();
				spiritTag.setCurativeItems(EMPTY_LIST);
				event.player.addEffect(spiritTag);
			}
		} else {
			if (instance != null) {
				event.player.removeEffect(spiritEffect);
			}
		}
	}
	
}
