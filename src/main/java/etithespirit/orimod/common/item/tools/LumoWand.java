package etithespirit.orimod.common.item.tools;


import etithespirit.orimod.GeneralUtils;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.block.light.decoration.IForlornBlueOrangeBlock;
import etithespirit.orimod.common.creative.OriModCreativeModeTabs;
import etithespirit.orimod.common.item.data.SpiritItemCustomizations;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.world.level.block.state.BlockState;

/**
 * The Lumo-Wand, a tool designed for Light-based circuitry.
 *
 * In the off chance that any other devs reading this code find this, yes, it is from SkySaga c:
 */
public class LumoWand extends Item {
	/***/
	public LumoWand() {
		this(
			new Item.Properties()
				.rarity(Rarity.RARE)
				.tab(OriModCreativeModeTabs.SPIRIT_TOOLS)
		);
	}
	/**
	 * @param props The properties of this item.
	 */
	public LumoWand(Properties props) {
		super(props);
	}
	

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return 1;
	}
	
	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		Level world = ctx.getLevel();
		BlockPos at = ctx.getClickedPos();
		BlockState block = world.getBlockState(at);
		if (block.getBlock() instanceof IForlornBlueOrangeBlock forlornBlock) {
			forlornBlock.switchLuxenColor(world, at);
			if (!world.isClientSide()) {
				GeneralUtils.Server.message((ServerPlayer)ctx.getPlayer(), "info.orimod.lumowand.colorswap." + (block.getValue(ForlornAppearanceMarshaller.IS_BLUE) ? "orange" : "blue")); // purposely inverted!
			}
			return InteractionResult.SUCCESS;
		}
		/*
		BlockEntity ent = world.getBlockEntity(at);
		if (ent instanceof LightCapacitorTile capTile) {
			if (!world.isClientSide()) {
				GeneralUtils.message((ServerPlayer) ctx.getPlayer(), "info.orimod.lumowand.dbg_charge");
				capTile.dbg_ChangeEnergy();
			}
		}
		*/
		/*
		Level world = ctx.getLevel();
		BlockPos at = ctx.getClickedPos();
		BlockState block = world.getBlockState(at);
		if (ConnectableLightTechBlock.isConnectableBlock(block)){
			boolean targetState;
			if (ctx.isSecondaryUseActive()) {
				targetState = !block.getValue(LightConduitBlock.AUTO);
				BlockState oppositeAuto = block.setValue(LightConduitBlock.AUTO, targetState);
				world.setBlockAndUpdate(ctx.getClickedPos(), oppositeAuto);
				if (!world.isClientSide()) {
					GeneralUtils.message((ServerPlayer)ctx.getPlayer(), "info.orimod.lumowand.auto" + (targetState ? "on" : "off"));
				}
			} else {
				
				// The block connects no matter what, so toggling a side isn't possible.
				if (ConnectableLightTechBlock.alwaysConnectsWhenPossible(block)) {
					BlockEntity ent = world.getBlockEntity(at);
					if (ent instanceof TileEntityLightCapacitor capacitor) {
						double current = capacitor.getLightStored();
						double max = capacitor.getMaxLightStored();
						if (current > 0) {
							capacitor.extractLight(current, false);
						} else {
							capacitor.receiveLight(max, false);
						}
						OriMod.LOG.info("Current energy: {}", capacitor.getLightStored());
						return InteractionResult.SUCCESS;
					}
					return InteractionResult.FAIL;
				}
				
				Vec3 clickPos = ctx.getClickLocation();
				BooleanProperty blockFaceState = SixSidedUtils.getBlockStateFromEvidentFace(at, clickPos);
				Direction dir = SixSidedUtils.getDirectionBetweenBlocks(at, clickPos);
				BlockState other = world.getBlockState(at.offset(dir.getNormal()));
				targetState = !block.getValue(blockFaceState);
				
				// Important that this is run FIRST so that it triggers a block update if needed.
				world.setBlockAndUpdate(at, block.setValue(blockFaceState, targetState));
				
				if (ConnectableLightTechBlock.isConnectableBlock(other) && targetState) {
					// we want to connect, the other is already facing us can connect in that direction
					BooleanProperty isConnectedToMeProp = SixSidedUtils.oppositeState(blockFaceState);
					boolean isConnectedToMe = other.getValue(isConnectedToMeProp);
					boolean willBeConnectedToMe = !isConnectedToMe && other.getValue(LightConduitBlock.AUTO);
					if (isConnectedToMe || willBeConnectedToMe) {
						world.playSound(null, at, SoundRegistry.get("item.lumo_wand.swapconduitauto"), SoundSource.BLOCKS, 0.2f, 1f);
					}
				}
			}
			
			world.playSound(null, at, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.2f, targetState ? 0.6f : 0.5f);
			return InteractionResult.SUCCESS;
		}*/
		return InteractionResult.PASS;
	}
	
	
	
	@Override
	public void appendHoverText(@Nullable ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, @Nullable TooltipFlag flagIn) {
		tooltip.add(Component.translatable("tooltip.orimod.lumowand.1").withStyle(ChatFormatting.BLUE));
		tooltip.add(Component.translatable("tooltip.orimod.lumowand.2").withStyle(ChatFormatting.BLUE));
		tooltip.add(Component.translatable("tooltip.orimod.lumowand.3").withStyle(ChatFormatting.BLUE));
		tooltip.add(Component.translatable("tooltip.orimod.lumowand.4").withStyle(ChatFormatting.BLUE));
	}
	
	@Override
	public Component getName(ItemStack pStack) {
		return SpiritItemCustomizations.getNameAsLight(super.getName(pStack));
	}
}