package etithespirit.orimod.modinterop.jade;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.common.block.decay.flora.DecayLogBase;
import etithespirit.orimod.common.block.light.LightCapacitorBlock;
import etithespirit.orimod.common.block.light.connection.ConnectableLightTechBlock;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class WAILADisplayCuredDecayObjects implements IComponentProvider {
	
	private static final ResourceLocation SHOW_DECAY_STATE = new ResourceLocation(OriMod.MODID, "show_decay_state");
	
	
	public void initialize(IRegistrar hwyla) {
		hwyla.addConfig(SHOW_DECAY_STATE, true);
		hwyla.registerComponentProvider(this, TooltipPosition.BODY, DecayLogBase.class);
	}

	@Override
	public void appendTooltip(ITooltip info, BlockAccessor blockAccessor, IPluginConfig config) {
		if (blockAccessor.getBlock() instanceof DecayLogBase) {
			// This should be
			if (config.get(SHOW_DECAY_STATE)) {
				boolean isAuto = blockAccessor.getBlockState().getValue(DecayLogBase.IS_SAFE);
				addComponentYN(info, "waila.orimod.decaysafe", isAuto);
			}
		}
	}
	
	private void addComponentYN(ITooltip info, String trsKeyPrefix, boolean state) {
		info.add(new TranslatableComponent(trsKeyPrefix + (state ? "true" : "false")));
	}
}
