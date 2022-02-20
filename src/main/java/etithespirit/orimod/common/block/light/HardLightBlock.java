package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.ExtendedMaterial;
import etithespirit.orimod.common.block.IToolRequirementProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.Set;

import static etithespirit.orimod.common.block.StaticData.FALSE_POSITION_PREDICATE;
import static etithespirit.orimod.common.block.StaticData.TRUE_POSITION_PREDICATE;
/***/
@SuppressWarnings("unused")
public class HardLightBlock extends Block implements ILightBlockIdentifier, IToolRequirementProvider {
	/***/
	public HardLightBlock() {
		this(
			Properties.of(ExtendedMaterial.LIGHT)
				.isViewBlocking(FALSE_POSITION_PREDICATE)
				.emissiveRendering(TRUE_POSITION_PREDICATE)
				.isSuffocating(FALSE_POSITION_PREDICATE)
				.strength(10, 1000) // Make it absurdly blast resistant.
				.lightLevel((state) -> 15)
				.sound(SoundType.GLASS)
		);
	}
	/***/
	public HardLightBlock(Properties properties) {
		super(properties);
	}
	
	@Override
	public Iterable<Tag.Named<Block>> getTagsForBlock() {
		return Set.of(BlockTags.MINEABLE_WITH_PICKAXE);
	}
}
