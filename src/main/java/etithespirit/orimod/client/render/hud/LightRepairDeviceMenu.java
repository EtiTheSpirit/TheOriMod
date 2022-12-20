package etithespirit.orimod.client.render.hud;

import etithespirit.orimod.registry.MenuRegistry;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * The menu managing the Luxen Reconstructor block.
 */
public class LightRepairDeviceMenu extends AbstractContainerMenu {
	private static final int SLOT_COUNT = 9;
	private static final int INV_SLOT_START = 9;
	private static final int USE_ROW_SLOT_END = 45;
	private final Container device;
	
	public static MenuType<LightRepairDeviceMenu> createContainerType() {
		return new MenuType<>(LightRepairDeviceMenu::new);
	}
	
	public LightRepairDeviceMenu(int ctrId, Inventory plrInv) {
		this(ctrId, plrInv, new SimpleContainer(SLOT_COUNT));
	}
	
	public LightRepairDeviceMenu(int ctrId, Inventory plrInv, Container ctr) {
		super(MenuRegistry.LIGHT_REPAIR_DEVICE.get(), ctrId);
		checkContainerSize(ctr, SLOT_COUNT);
		device = ctr;
		ctr.startOpen(plrInv.player);
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 3; x++) {
				this.addSlot(new Slot(ctr, x + y * 3, 62 + x * 18, 17 + y * 18));
			}
		}
		
		for(int y = 0; y < 3; y++) {
			for(int x = 0; x < 9; x++) {
				this.addSlot(new Slot(plrInv, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
		
		for(int x = 0; x < 9; x++) {
			this.addSlot(new Slot(plrInv, x, 8 + x * 18, 142));
		}
	}
	
	@Override
	public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
		ItemStack returnStack = ItemStack.EMPTY;
		Slot slot = slots.get(pIndex);
		if (slot.hasItem()) {
			ItemStack stackInSlot = slot.getItem();
			returnStack = stackInSlot.copy();
			if (pIndex < 9) {
				if (!this.moveItemStackTo(stackInSlot, 9, 45, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(stackInSlot, 0, 9, false)) {
				return ItemStack.EMPTY;
			}
			
			if (stackInSlot.isEmpty()) {
				slot.set(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
			
			if (stackInSlot.getCount() == returnStack.getCount()) {
				return ItemStack.EMPTY;
			}
			
			slot.onTake(pPlayer, stackInSlot);
		}
		
		return returnStack;
	}
	
	@Override
	public boolean stillValid(Player pPlayer) {
		return device.stillValid(pPlayer);
	}
	
	@Override
	public void removed(Player pPlayer) {
		super.removed(pPlayer);
		device.stopOpen(pPlayer);
	}
}
