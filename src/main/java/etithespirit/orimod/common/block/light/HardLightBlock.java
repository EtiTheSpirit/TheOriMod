package etithespirit.orimod.common.block.light;

import etithespirit.orimod.common.block.ExtendedMaterial;
import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.util.PresetBlockTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.Set;

import static etithespirit.orimod.common.block.StaticData.FALSE_POSITION_PREDICATE;
import static etithespirit.orimod.common.block.StaticData.TRUE_POSITION_PREDICATE;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

/***/
@SuppressWarnings("unused")
public class HardLightBlock extends Block implements ILightBlockIdentifier, IToolRequirementProvider {
	/***/
	public HardLightBlock() {
		super(
			Properties.of(ExtendedMaterial.LIGHT)
				.isViewBlocking(FALSE_POSITION_PREDICATE)
				.emissiveRendering(TRUE_POSITION_PREDICATE)
				.isSuffocating(FALSE_POSITION_PREDICATE)
				.strength(10, 1000) // Make it absurdly blast resistant.
				.lightLevel((state) -> 15)
				.sound(SoundType.GLASS)
		);
	}
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY;
	}
}
