package etithespirit.etimod.common.block.decay.world;

import java.util.function.Supplier;

import etithespirit.etimod.fluid.DecayFluid;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.state.StateContainer;

public class DecayPoisonBlock extends FlowingFluidBlock {
	
	public DecayPoisonBlock() {
		this(() -> DecayFluid.DECAY, Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops());
	}

	public DecayPoisonBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
		super(supplier, properties);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" }) 
	public void fillStateContainer(StateContainer.Builder builder) {
		super.fillStateContainer(builder);
		builder.add(DecayFluid.IS_FULL_DECAY);
	}

}
