package etithespirit.etimod.fluid;

import java.util.Random;

import javax.annotation.Nullable;

import etithespirit.etimod.EtiMod;
import etithespirit.etimod.fluid.tags.DecayFluidTags;
import etithespirit.etimod.registry.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;

public abstract class DecayFluid extends FlowingFluid {
	
	/**
	 * A property that represents the decayed state of this block. False means partially decayed, which serves as a buffer between water and raw decay fluid
	 * True means fully decayed, which instantly replaces all adjacent water blocks with partially decayed water.
	 */
	public static final BooleanProperty IS_FULL_DECAY = BooleanProperty.create("fully_decayed");
	
	public static final FlowingFluid DECAY = new Source();
	public static final FlowingFluid DECAY_FLOWING = new Flowing();
	
	@Override
	public Fluid getFlowing() {
		return DECAY_FLOWING;
	}
	
	@Override
	public Fluid getSource() {
		return DECAY;
	}
	
	@Override
	public Item getBucket() {
		return null;
	}
	
	@Override
	protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder) {
		super.createFluidStateDefinition(builder);
		builder.add(IS_FULL_DECAY);
	}
	
	/**
	 * Returns whether or not this flowing decay fluid is fully decayed (true) or partially decayed (false).
	 * @param state The state to test.
	 * @return Whether or not this state has its {@link #IS_FULL_DECAY} value set.
	 */
	public boolean isFullyDecayed(FluidState state) {
		return state.getValue(IS_FULL_DECAY);
	}
	
	@Override
	public void animateTick(World worldIn, BlockPos pos, FluidState state, Random random) {
		
	}
	
	@Override
	public void randomTick(World worldIn, BlockPos pos, FluidState state, Random random) {
		if (random.nextInt(3) == 0) {
			if (isFullyDecayed(state)) {
				BlockPos[] adj = getAdjacentsTo(pos);
				for (BlockPos neighbor : adj) {
					BlockState bstate = worldIn.getBlockState(neighbor);
					FluidState fstate = bstate.getFluidState();
					if (fstate == null) continue;
					
					if (fstate.isSource() && fstate.is(FluidTags.WATER)) {
						worldIn.setBlockAndUpdate(neighbor, createLegacyBlock(DECAY.defaultFluidState()).setValue(IS_FULL_DECAY, Boolean.FALSE));
					}
				}
			}
		}
	}
	
	private static BlockPos[] getAdjacentsTo(BlockPos pos) {
		BlockPos[] poses = new BlockPos[6];
		poses[0] = pos.above();
		poses[1] = pos.below();
		poses[2] = pos.east();
		poses[3] = pos.west();
		poses[4] = pos.north();
		poses[5] = pos.south();
		return poses;
	}
	
	@Nullable
	@Override
	public IParticleData getDripParticle() {
		return ParticleTypes.MYCELIUM;
	}
	
	@Override
	protected boolean canConvertToSource() {
		return true;
	}
	
	@Override
	protected void beforeDestroyingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
		TileEntity tileentity = state.hasTileEntity() ? worldIn.getBlockEntity(pos) : null;
		Block.dropResources(state, worldIn, pos, tileentity);
	}
	
	@Override
	public int getSlopeFindDistance(IWorldReader worldIn) {
		return 3;
	}
	
	@Override
	public BlockState createLegacyBlock(FluidState state) {
		Boolean value = state.getValue(IS_FULL_DECAY);
		if (value == null) {
			value = Boolean.TRUE;
		}
		return BlockRegistry.DECAY_POISON.get().defaultBlockState()
				.setValue(IS_FULL_DECAY, value);
	}
	
	@Override
	public boolean isSame(Fluid fluidIn) {
		return fluidIn == DECAY || fluidIn == DECAY_FLOWING;
	}
	
	@Override
	public int getDropOff(IWorldReader worldIn) {
		return 2;
	}
	
	@Override
	public int getTickDelay(IWorldReader p_205569_1_) {
		return 12;
	}
	
	@Override
	public boolean canBeReplacedWith(FluidState otherFluidState, IBlockReader blockReader, BlockPos at, Fluid otherFluid, Direction inDirection) {
		return inDirection == Direction.DOWN && !otherFluid.is(DecayFluidTags.DECAY);
	}
	
	@Override
	protected float getExplosionResistance() {
		return 100.0F;
	}
	
	@Override
	protected boolean isRandomlyTicking() {
		return true;
	}
	
	protected FluidAttributes.Builder prepareAttributesBuilder() {
		FluidAttributes.Builder builder = FluidAttributes.builder(
			new ResourceLocation(EtiMod.MODID, "block/decay_poison"), 
			new ResourceLocation(EtiMod.MODID, "block/decay_poison_flowing")
		);
		builder.color(0xD55F0A9B);
		builder.density(1210);
		builder.rarity(Rarity.UNCOMMON);
		builder.viscosity(2400);
		return builder;
	}
	
	public static class Flowing extends DecayFluid {
		
		public Flowing() { }
		
		@Override
		protected FluidAttributes createAttributes() {
			return prepareAttributesBuilder().build(DECAY_FLOWING);
		}
		
		@Override
		protected void createFluidStateDefinition(StateContainer.Builder<Fluid, FluidState> builder) {
			super.createFluidStateDefinition(builder); // adds full decay state
			builder.add(LEVEL);
		}
		
		@Override
		public int getAmount(FluidState state) {
			return state.getValue(LEVEL);
		}
	
		@Override
		public boolean isSource(FluidState state) {
			return false;
		}

		/*
		@Override
		public boolean canDisplace(FluidState thisFluidState, IBlockReader blockReader, BlockPos at, Fluid thisFluid, Direction inDirection) {
			return false;
		}*/
	}
		
	public static class Source extends DecayFluid {
		
		public Source() { }
		
		@Override
		protected FluidAttributes createAttributes() {
			return prepareAttributesBuilder().build(DECAY);
		}
		
		@Override
		public int getAmount(FluidState p_207192_1_) {
			return 8;
		}
		
		@Override
		public boolean isSource(FluidState state) {
			return true;
		}

		/*
		@Override
		public boolean canDisplace(FluidState thisFluidState, IBlockReader blockReader, BlockPos at, Fluid thisFluid, Direction inDirection) {
			return false;
		}*/
	}

}
