package etithespirit.orimod.common.block.light.decoration;

import etithespirit.orimod.common.block.IToolRequirementProvider;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.ILightBlockIdentifier;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.material.ExtendedMaterials;
import etithespirit.orimod.registry.ItemRegistry;
import etithespirit.orimod.registry.util.IBlockItemPropertiesProvider;
import etithespirit.orimod.util.PresetBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;

import java.util.List;
import java.util.Set;

import static etithespirit.orimod.common.block.StaticData.FALSE_POSITION_PREDICATE;
import static etithespirit.orimod.common.block.StaticData.TRUE_POSITION_PREDICATE;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;

/***/
@SuppressWarnings("unused")
public class HardLightBlock extends Block implements ILightBlockIdentifier, IToolRequirementProvider, IBlockItemPropertiesProvider {
	/***/
	public HardLightBlock() {
		super(
			Properties.of(ExtendedMaterials.LIGHT)
				.isViewBlocking(FALSE_POSITION_PREDICATE)
				.emissiveRendering(TRUE_POSITION_PREDICATE)
				.isSuffocating(FALSE_POSITION_PREDICATE)
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
		return PresetBlockTags.PICKAXE_ONLY;
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
