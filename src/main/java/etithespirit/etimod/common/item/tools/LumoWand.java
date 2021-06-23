package etithespirit.etimod.common.item.tools;

import etithespirit.etimod.common.block.light.LightConduitBlock;
import etithespirit.etimod.common.block.light.connection.ConnectableLightTechBlock;
import etithespirit.etimod.registry.SoundRegistry;
import etithespirit.etimod.util.blockstates.SixSidedUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.BooleanProperty;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * The Lumo-Wand, a tool designed for Light-based circuitry.
 *
 * In the off chance that any other devs reading this code find this, yes, it is from SkySaga c:
 */
public class LumoWand extends Item {
	
	public LumoWand() {
		this(
			new Item.Properties()
			.rarity(Rarity.RARE)
			.tab(ItemGroup.TAB_TOOLS)
		);
	}
	
	public LumoWand(Properties props) {
		super(props);
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext ctx) {
		World world = ctx.getLevel();
		BlockPos at = ctx.getClickedPos();
		BlockState block = world.getBlockState(at);
		if (ConnectableLightTechBlock.isInstance(block)){
			boolean targetState;
			if (ctx.isSecondaryUseActive()) {
				targetState = !block.getValue(LightConduitBlock.AUTO);
				BlockState oppositeAuto = block.setValue(LightConduitBlock.AUTO, targetState);
				world.setBlockAndUpdate(ctx.getClickedPos(), oppositeAuto);
				if (!world.isClientSide()) {
					message(ctx.getPlayer(), "info.etimod.lumowand.auto" + (targetState ? "on" : "off"));
				}
			} else {
				
				// The block connects no matter what, so toggling a side isn't possible.
				if (ConnectableLightTechBlock.connectsFromAnySideAlways(block)) {
					return ActionResultType.FAIL;
				}
				
				Vector3d clickPos = ctx.getClickLocation();
				BooleanProperty blockFaceState = SixSidedUtils.getBlockStateFromEvidentFace(at, clickPos);
				Direction dir = SixSidedUtils.getNearestDirectionForBlock(at, clickPos);
				BlockState other = world.getBlockState(at.offset(dir.getNormal()));
				targetState = !block.getValue(blockFaceState);
				
				// Important that this is run FIRST so that it triggers a block update if needed.
				world.setBlockAndUpdate(at, block.setValue(blockFaceState, targetState));
				
				if (ConnectableLightTechBlock.isInstance(other) && targetState) {
					// we want to connect, the other is already facing us can connect in that direction
					BooleanProperty isConnectedToMeProp = SixSidedUtils.oppositeState(blockFaceState);
					boolean isConnectedToMe = other.getValue(isConnectedToMeProp);
					boolean willBeConnectedToMe = !isConnectedToMe && other.getValue(LightConduitBlock.AUTO);
					if (isConnectedToMe || willBeConnectedToMe) {
						world.playSound(null, at, SoundRegistry.get("item.lumo_wand.swapconduitauto"), SoundCategory.BLOCKS, 0.2f, 1f);
					}
				}
			}
			
			world.playSound(null, at, SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 0.2f, targetState ? 0.6f : 0.5f);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		tooltip.add(new TranslationTextComponent("tooltip.etimod.lumowand.1"));
		tooltip.add(new TranslationTextComponent("tooltip.etimod.lumowand.2"));
		tooltip.add(new TranslationTextComponent("tooltip.etimod.lumowand.3"));
		tooltip.add(new TranslationTextComponent("tooltip.etimod.lumowand.4"));
		tooltip.add(new TranslationTextComponent("tooltip.etimod.lumowand.5"));
	}
	
	private static void message(PlayerEntity player, String message) {
		((ServerPlayerEntity)player).sendMessage(new TranslationTextComponent(message), ChatType.GAME_INFO, Util.NIL_UUID);
	}
}
