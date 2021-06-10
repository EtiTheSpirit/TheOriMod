package etithespirit.etimod.common.block.light;

import etithespirit.etimod.common.tile.light.TileEntityLightCapacitor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class BlockLightCapacitor extends Block implements ILightBlockIdentifier {
	
	public BlockLightCapacitor() {
		this(Properties.create(Material.ROCK)
			.harvestTool(ToolType.PICKAXE) // but don't require a tool
			.sound(SoundType.STONE)
		);
	}

	public BlockLightCapacitor(Properties properties) {
		super(properties);
		
	}
	
	@Override
	public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
		
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityLightCapacitor();
	}

}
