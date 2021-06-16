package etithespirit.etimod.common.block.light;

import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.common.tile.IWorldUpdateListener;
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

public class LightCapacitorBlock extends ConnectableLightTechBlock implements ILightBlockIdentifier {
	
	public LightCapacitorBlock() {
		this(Properties.of(Material.STONE)
			 .harvestTool(ToolType.PICKAXE) // but don't require a tool
		     .sound(SoundType.STONE)
		);
	}

	public LightCapacitorBlock(Properties properties) {
		super(properties);
		ConnectableLightTechBlock.autoRegisterDefaultState(this::registerDefaultState, this.stateDefinition);
	}
	
	@Override
	public boolean connectsFromAnySideAlways() {
		return true;
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onPlace(BlockState state, World world, BlockPos at, BlockState old, boolean isMoving) {
	
	}
	
	@Override
	public void connectionStateChanged(BlockState originalState, BlockState newState) { }
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new TileEntityLightCapacitor();
	}
	
}
