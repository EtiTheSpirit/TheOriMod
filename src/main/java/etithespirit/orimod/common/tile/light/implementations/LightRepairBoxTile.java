package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.annotation.ServerUseOnly;
import etithespirit.orimod.client.audio.LightTechLooper;
import etithespirit.orimod.client.render.hud.LightRepairDeviceMenu;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.item.ISpiritLightRepairableItem;
import etithespirit.orimod.common.tags.OriModItemTags;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.common.tile.IClientUpdatingTile;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.LightEnergyHandlingTile;
import etithespirit.orimod.common.tile.light.helpers.EnergyReservoir;
import etithespirit.orimod.common.tile.light.helpers.SoundSmearer;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.world.TileEntityRegistry;
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

public class LightRepairBoxTile extends LightEnergyHandlingTile implements MenuProvider, Container, IAmbientSoundEmitter, IServerUpdatingTile, IClientUpdatingTile, ILightEnergyConsumer {
	
	public static final float CONSUMPTION_RATE = 0.25f;
	public static final int CONTAINER_SIZE = 9;
	private NonNullList<ItemStack> items = NonNullList.withSize(CONTAINER_SIZE, ItemStack.EMPTY);
	private final LightTechLooper SOUND;
	private int nextItem = 0;
	
	private final @ServerUseOnly EnergyReservoir consumerHelper = new EnergyReservoir(Float.POSITIVE_INFINITY); // Unlike other devices, this has a conditional cost, so it can store a theoretical unlimited amount of power
	// This also allows it to accommodate for items that want more power to repair.
	
	private final @ServerUseOnly SoundSmearer soundSmearer = new SoundSmearer(SoundSmearer.SmearDirection.DELAY_BOTH, 10);
	private @ServerUseOnly boolean lastTickHadEnough = true;
	private @ServerUseOnly boolean thereWasSomethingToRepair = false;
	private @ServerUseOnly boolean failedToRepairItemLastTick = false;
	
	public LightRepairBoxTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_REPAIR_BOX.get(), pWorldPosition, pBlockState);
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
	public void updateVisualPoweredAppearance() {
		BlockState currentState = getBlockState();
		boolean targetPower = soundShouldBePlaying();
		boolean currentPower = currentState.getValue(ForlornAppearanceMarshaller.POWERED);
		if (targetPower != currentPower) {
			super.utilSetPoweredStateTo(targetPower);
		}
	}
	
	/**
	 * The Block Entity keeps track of the last inventory slot it repaired. This will make it try to repair that slot, such that
	 * only one slot is repaired per tick.
	 */
	protected void tryRepairNextItem() {
		thereWasSomethingToRepair = false;
		failedToRepairItemLastTick = false;
		for (int limit = 0; limit < CONTAINER_SIZE; limit++) {
			ItemStack item = items.get(nextItem++);
			if (nextItem >= CONTAINER_SIZE) nextItem = 0;
			
			boolean canBeRepaired = !item.isEmpty() && item.is(OriModItemTags.LIGHT_REPAIRABLE) && item.isDamaged();
			if (canBeRepaired) {
				thereWasSomethingToRepair = true;
				if (trySpendEnergyForTick(item)) {
					item.setDamageValue(item.getDamageValue() - 1);
				} else {
					failedToRepairItemLastTick = true;
				}
				break;
			}
		}
		lastTickHadEnough = !thereWasSomethingToRepair || !failedToRepairItemLastTick;
		// Above comes out to "There was nothing to repair, or, there was something to repair (implicit) and no items failed to repair last tick.
	}
	
	@Override
	public void updateServer(Level inLevel, BlockPos at, BlockState current) {
		tryRepairNextItem();
		soundSmearer.tick(thereWasSomethingToRepair && !failedToRepairItemLastTick);
	}
	
	@Override
	public void updateClient(Level inLevel, BlockPos at, BlockState current) {
		BlockState state = getBlockState();
		soundSmearer.tick(state.getValue(ForlornAppearanceMarshaller.POWERED));
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
		return soundSmearer.shouldSoundPlay();
	}
	
	/**
	 * Attempts to spend the energy needed to repair something by one durability point.
	 * @return Whether or not enough energy was there to spend.
	 */
	private boolean trySpendEnergyForTick(ItemStack item) {
		float rate = CONSUMPTION_RATE;
		if (item.getItem() instanceof ISpiritLightRepairableItem spiritLightItem) {
			rate *= spiritLightItem.getRepairEnergyCostMultiplier();
			if (rate <= 0) return true;
		}
		lastTickHadEnough = consumerHelper.tryConsume(rate, false);
		return lastTickHadEnough;
	}
	
	@Override
	public float consumeEnergy(float desiredAmount, boolean simulate) {
		if (!thereWasSomethingToRepair) return 0; // Do not spend energy if there's nothing to repair!
		
		float realAmount = desiredAmount / 2f;
		return consumerHelper.stash(realAmount, simulate);
	}
	
	@Override
	public float getMaximumDrawnAmountForDisplay() {
		return thereWasSomethingToRepair ? CONSUMPTION_RATE : 0;
	}
	
	@Override
	public boolean hadTooLittlePowerLastForDisplay() {
		return !lastTickHadEnough;
	}
}