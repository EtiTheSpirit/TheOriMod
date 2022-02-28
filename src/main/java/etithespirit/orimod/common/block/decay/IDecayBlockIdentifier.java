package etithespirit.orimod.common.block.decay;

import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import net.minecraft.world.item.Item;

/**
 * An empty interface for the purpose of identifying Decay blocks. Anything that implements this <strong>is</strong> a Decay block, but <strong>is not</strong> guaranteed to have the associated
 * spreading methods or other functionality - this is exclusively for identity and identity alone.<br>
 * <br>
 * To check if a Decay block has higher level functions, check for the implementation of {@link etithespirit.orimod.common.block.decay.IDecayBlock}
 * @author Eti
 *
 */
public interface IDecayBlockIdentifier extends IBlockItemPropertiesProvider {
	
	@Override
	default Item.Properties getPropertiesOfItem() {
		return (new Item.Properties()).tab(OriModCreativeModeTabs.DECAY);
	}
	
}
