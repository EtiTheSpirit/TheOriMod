package etithespirit.etimod.common.block.light;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;

import static etithespirit.etimod.common.block.StaticData.FALSE_POSITION_PREDICATE;
import static etithespirit.etimod.common.block.StaticData.TRUE_POSITION_PREDICATE;

import etithespirit.etimod.common.block.ExtendedMaterial;

@SuppressWarnings("unused")
public class HardLightBlock extends Block implements ILightBlockIdentifier {
	
	public HardLightBlock() {
		this(
			Properties.of(ExtendedMaterial.LIGHT)
			.isViewBlocking(FALSE_POSITION_PREDICATE)
			.emissiveRendering(TRUE_POSITION_PREDICATE)
			.isSuffocating(FALSE_POSITION_PREDICATE)
			.harvestTool(ToolType.PICKAXE) // but don't require a tool
			.strength(10, 1000) // Make it absurdly blast resistant.
			.lightLevel((state) -> 15)
			.sound(SoundType.GLASS)
		);
	}

	public HardLightBlock(Properties properties) {
		super(properties);
	}

}
