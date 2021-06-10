package etithespirit.etimod.common.block.light;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraftforge.common.ToolType;

import static etithespirit.etimod.common.block.StaticData.FALSE_POSITION_PREDICATE;
import static etithespirit.etimod.common.block.StaticData.TRUE_POSITION_PREDICATE;

import etithespirit.etimod.common.block.ExtendedMaterial;

import net.minecraft.block.AbstractBlock.Properties;

public class BlockHardLight extends Block implements ILightBlockIdentifier {
	
	public BlockHardLight() {
		this(
			Properties.of(ExtendedMaterial.LIGHT)
			.isViewBlocking(FALSE_POSITION_PREDICATE)
			.emissiveRendering(TRUE_POSITION_PREDICATE)
			.isSuffocating(FALSE_POSITION_PREDICATE)
			.harvestTool(ToolType.PICKAXE) // but don't require a tool
			.strength(10, 1000) // Make it absurdly blast resistant.
			.lightLevel((state) -> { return 15; })
			.sound(SoundType.GLASS)
		);
	}

	public BlockHardLight(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}

}
