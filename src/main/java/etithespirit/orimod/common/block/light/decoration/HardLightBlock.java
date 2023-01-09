package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IBlockTagProvider;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.common.material.ExtendedMaterials;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import static etithespirit.orimod.common.block.StaticData.ALWAYS_FALSE;
import static etithespirit.orimod.common.block.StaticData.ALWAYS_TRUE;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/***/
@SuppressWarnings("unused")
public class HardLightBlock extends Block implements IBlockTagProvider, IBlockItemPropertiesProvider {
	/***/
	public HardLightBlock() {
		super(
			Properties.of(ExtendedMaterials.LIGHT)
				.isViewBlocking(ALWAYS_FALSE)
				.emissiveRendering(ALWAYS_TRUE)
				.isSuffocating(ALWAYS_FALSE)
				.strength(10, 1000) // Make it absurdly blast resistant.
				.lightLevel((state) -> 15)
				.sound(SoundType.GLASS)
				.noOcclusion()
		);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
		return pAdjacentBlockState.is(this) || super.skipRendering(pState, pAdjacentBlockState, pSide);
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext) {
		return Shapes.empty();
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return 1.0F;
	}
	
	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return true;
	}
	
	
	@Override
	public Iterable<TagKey<Block>> getTagsForBlock() {
		return PresetBlockTags.PICKAXE_ONLY_AND_LIGHT;
	}
	
	/**
	 * Returns the properties for this ItemBlock for use in forge registries.
	 *
	 * @return The properties for this ItemBlock.
	 */
	@Override
	public Item.Properties getPropertiesOfItem() {
		return new Item.Properties().tab(OriModCreativeModeTabs.SPIRIT_DECORATION);
	}
	
	@Override
	public MutableComponent getName() {
		return SpiritItemCustomizations.getNameAsLight(super.getName());
	}
}
