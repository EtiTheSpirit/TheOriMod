package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.material.ExtendedMaterials;
import etithespirit.orimod.registry.gameplay.ItemRegistry;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.common.tags.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;

import java.util.List;

import static etithespirit.orimod.common.block.StaticData.ALWAYS_FALSE;
import static etithespirit.orimod.common.block.StaticData.ALWAYS_TRUE;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/***/
@SuppressWarnings("unused")
public class HardLightBlock extends Block implements IToolRequirementProvider, IBlockItemPropertiesProvider {
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
	@SuppressWarnings("deprecation")
	public List<ItemStack> getDrops(BlockState pState, LootContext.Builder pBuilder) {
		return List.of(new ItemStack(ItemRegistry.getBlockItemOf(this)));
	}
}
