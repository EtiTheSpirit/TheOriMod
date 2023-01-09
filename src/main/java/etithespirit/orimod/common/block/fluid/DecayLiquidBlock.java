package etithespirit.orimod.common.block.fluid;

import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.decay.IDecayBlockCommon;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.registry.world.FluidRegistry;
import etithespirit.orimod.util.level.StateHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidInteractionRegistry;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static etithespirit.orimod.common.block.decay.DecayCommon.ALL_ADJACENT_ARE_DECAY;
import static etithespirit.orimod.common.block.decay.DecayCommon.DECAY_REPLACEMENT_TARGETS;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_DETECTION_RARITY;
import static etithespirit.orimod.common.block.decay.DecayCommon.EDGE_TEST_MINIMUM_CHANCE;

/**
 * A decay poison liquid block. Note that this does not use conventional Decay spread behavior as it is incompatible with fluids.
 * This instead uses {@link FluidInteractionRegistry}
 */
public class DecayLiquidBlock extends LiquidBlock {
	/**
	 * @param fluid      A fluid supplier such as {@link RegistryObject < FlowingFluid >}
	 * @param properties The properties of this fluid.
	 */
	public DecayLiquidBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
		super(fluid, properties);
	}
	
	@Override
	public MutableComponent getName() {
		return SpiritItemCustomizations.getNameAsDecay(super.getName());
	}
	
}
