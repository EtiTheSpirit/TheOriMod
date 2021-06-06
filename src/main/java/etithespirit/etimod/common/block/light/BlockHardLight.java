package etithespirit.etimod.common.block.light;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;

import static etithespirit.etimod.common.block.StaticData.FALSE_POSITION_PREDICATE;
import static etithespirit.etimod.common.block.StaticData.TRUE_POSITION_PREDICATE;

import etithespirit.etimod.common.block.ExtendedMaterial;

public class BlockHardLight extends Block implements ILightBlockIdentifier {
	
	public BlockHardLight() {
		this(
			Properties.create(ExtendedMaterial.LIGHT)
			.setBlocksVision(FALSE_POSITION_PREDICATE)
			.setEmmisiveRendering(TRUE_POSITION_PREDICATE)
			.setSuffocates(FALSE_POSITION_PREDICATE)
			.harvestTool(ToolType.PICKAXE) // but don't require a tool
			.hardnessAndResistance(10, 1000) // Make it absurdly blast resistant.
			.setLightLevel((state) -> { return 15; })
			.sound(SoundType.GLASS)
		);
	}

	public BlockHardLight(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

}
