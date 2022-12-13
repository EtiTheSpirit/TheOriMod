package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.annotation.ClientUseOnly;
import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.client.audio.LightTechLooper;
import etithespirit.orimod.client.render.hud.LightRepairDeviceMenu;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.item.OriModItemTags;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class LightRepairBoxTile extends LightEnergyStorageTile implements MenuProvider, Container, IAmbientSoundEmitter, IServerUpdatingTile, LightEnergyStorageTile.ILuxenConsumer {
	
	public static final float CONSUMPTION_RATE = 0.25f;
	public static final int CONTAINER_SIZE = 9;
	private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	private final LightTechLooper SOUND;
	private int debounceSoundOffTicksRemaining = 0;
	private int nextItem = 0;
	
	private @ServerUseOnly boolean continuePlayingSound = false;
	private @ServerUseOnly boolean neededToRepairItemLastTick = false;
	private @ServerUseOnly boolean failedToRepairItemLastTick = false;
	private @ClientUseOnly int ticksThatWouldPlaySound = 0;
	
	public LightRepairBoxTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_REPAIR_BOX.get(), pWorldPosition, pBlockState, new PersistentLightEnergyStorage(null, CONSUMPTION_RATE, CONSUMPTION_RATE, 0));
		SOUND = new LightTechLooper(
			this,
			SoundRegistry.get("tile.light_tech.generic.activate"),
			SoundRegistry.get("tile.light_tech.generic.active_loop"),
			SoundRegistry.get("tile.light_tech.generic.deactivate")
		);
		// SOUND.setRange(4);
		SOUND.setBaseVolume(0.3f);
	}
	
	@Override
	public int getContainerSize() {
		return CONTAINER_SIZE;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public ItemStack getItem(int pSlot) {
		if (pSlot < CONTAINER_SIZE) {
			return items.get(pSlot).copy();
		}
		return ItemStack.EMPTY;
	}
	
	@Override
	public ItemStack removeItem(int pIndex, int pCount) {
		ItemStack itemstack = ContainerHelper.removeItem(items, pIndex, pCount);
		if (!itemstack.isEmpty()) {
			this.setChanged();
		}
		
		return itemstack;
	}
	
	@Override
	public ItemStack removeItemNoUpdate(int pIndex) {
		return ContainerHelper.takeItem(items, pIndex);
	}
	
	@Override
	public void setItem(int pIndex, ItemStack pStack) {
		items.set(pIndex, pStack);
		if (pStack.getCount() > this.getMaxStackSize()) {
			pStack.setCount(this.getMaxStackSize());
		}
		
		this.setChanged();
	}
	
	
	// This must exist due to some interface garbage.
	@Override
	public void setChanged() {
		super.setChanged();
	}
	
	public boolean stillValid(Player pPlayer) {
		if (level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(pPlayer.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}
	
	@Override
	public void clearContent() {
		items.clear();
	}
	
	@Override
	public Component getDisplayName() {
		return Component.translatable("container.orimod.light_repair_device");
	}
	
	@Override
	public void load(CompoundTag pTag) {
		super.load(pTag);
		items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(pTag, items);
	}
	
	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		ContainerHelper.saveAllItems(pTag, items);
	}
	
	@Nullable
	@Override
	public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
		return new LightRepairDeviceMenu(pContainerId, pPlayerInventory, this);
	}
	
	@Override
	public boolean skipAutomaticPoweredBlockstate() {
		return true;
	}
	
	/**
	 * Iterates over all items in the container and spends energy to try to repair them.
	 */
	@Deprecated(forRemoval = true)
	protected void tryRepairAllItems() {
		boolean didSomething = false;
		neededToRepairItemLastTick = false;
		for (int index = 0; index < CONTAINER_SIZE; index++) {
			ItemStack item = items.get(index);
			if (item.isEmpty()) continue;
			if (!item.is(OriModItemTags.LIGHT_REPAIRABLE)) continue;
			if (!item.isDamaged()) continue;
			
			if (trySpendEnergy(CONSUMPTION_RATE, false)) {
				item.setDamageValue(item.getDamageValue() - 1);
				didSomething = true;
			}
			neededToRepairItemLastTick = true;
		}
		failedToRepairItemLastTick = neededToRepairItemLastTick && !didSomething;
		continuePlayingSound = didSomething || debounceSoundOffTicksRemaining > 1;
	}
	
	protected void tryRepairNextItem() {
		tryRepairNextItem(0);
	}
	
	private void tryRepairNextItem(int reEntry) {
		ItemStack item = items.get(nextItem);
		boolean didSomething = false;
		nextItem++;
		if (nextItem >= CONTAINER_SIZE) nextItem = 0;
		neededToRepairItemLastTick = false;
		
		boolean mustSkip = item.isEmpty() || !item.is(OriModItemTags.LIGHT_REPAIRABLE) || !item.isDamaged();
		if (mustSkip) {
			if (reEntry >= CONTAINER_SIZE) {
				failedToRepairItemLastTick = false;
				continuePlayingSound = debounceSoundOffTicksRemaining > 1;
				return;
			}
			tryRepairNextItem(++reEntry);
			return;
		}
		
		neededToRepairItemLastTick = true;
		if (trySpendEnergy(CONSUMPTION_RATE, false)) {
			item.setDamageValue(item.getDamageValue() - 1);
			didSomething = true;
		}
		failedToRepairItemLastTick = neededToRepairItemLastTick && !didSomething;
		continuePlayingSound = didSomething || debounceSoundOffTicksRemaining > 1;
	}
	
	@Override
	public void updateServer(Level inLevel, BlockPos at, BlockState current) {
		//tryRepairAllItems();
		tryRepairNextItem();
		if (current.getValue(ForlornAppearanceMarshaller.POWERED) != continuePlayingSound) {
			inLevel.setBlock(at, current.setValue(ForlornAppearanceMarshaller.POWERED, continuePlayingSound), StaticData.REPLICATE_CHANGE);
			// No neighbor updates! This is just for the sound.
		}
		debounceSoundOffTicksRemaining--;
		if (debounceSoundOffTicksRemaining < 0) debounceSoundOffTicksRemaining = 0;
	}
	
	// This fixes a bug that causes the sound to be spammed in low energy conditions.
	@Override
	public float receiveLight(float maxReceive, boolean simulate) {
		if (!neededToRepairItemLastTick)
			return 0; // When there's nothing to do, do not consume power.
		
		float amount = super.receiveLight(maxReceive, simulate);
		if (!simulate) {
			if (amount > 0) {
				if (getLightStored() >= CONSUMPTION_RATE) {
					debounceSoundOffTicksRemaining += 2; // Add 2 so that it has to fight against the constant decrease
					if (debounceSoundOffTicksRemaining > 10) debounceSoundOffTicksRemaining = 10;
				}
			}
		}
		return amount;
	}
	
	/**
	 * Returns a reference to the sound(s) that this emits. This should be cached on creation of the Block Entity,
	 * and then returned here. Do not create a new instance when this is called.
	 *
	 * @return A reference to the sound that this emits.
	 */
	@Override
	public LightTechLooper getSoundInstance() {
		return SOUND;
	}
	
	/**
	 * Whether or not the system believes the sound should be playing right now.
	 *
	 * @return Whether or not the sound should be playing right now.
	 */
	@Override
	public boolean soundShouldBePlaying() {
		boolean isPowered = getBlockState().getValue(ForlornAppearanceMarshaller.POWERED);
		if (isPowered) {
			ticksThatWouldPlaySound++;
			if (ticksThatWouldPlaySound > 10) ticksThatWouldPlaySound = 10;
		} else {
			ticksThatWouldPlaySound--;
			if (ticksThatWouldPlaySound < 0) ticksThatWouldPlaySound = 0;
		}
		return ticksThatWouldPlaySound > 4;
		// And check greater than 1 here so that the +2 -1 pattern doesn't cause spam.
	}
	
	@Override
	public float getLuxConsumedPerTick() {
		if (neededToRepairItemLastTick && !failedToRepairItemLastTick) {
			// It needed to and did not fail. Great!
			return CONSUMPTION_RATE;
		}
		// Failed.
		return 0;
	}
	
	@Override
	public boolean isOverdrawn() {
		return failedToRepairItemLastTick;
	}
}