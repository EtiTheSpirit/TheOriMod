package etithespirit.orimod.common.tile.light.implementations;

import etithespirit.orimod.client.audio.LightTechLooper;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.common.tile.light.LightEnergyStorageTile;
import etithespirit.orimod.common.tile.light.PersistentLightEnergyStorage;
import etithespirit.orimod.registry.SoundRegistry;
import etithespirit.orimod.registry.TileEntityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class LightCapacitorTile extends LightEnergyStorageTile {//implements IAmbientSoundEmitter {
	
	//private LightTechLooper SOUND;
	
	public LightCapacitorTile(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TileEntityRegistry.LIGHT_ENERGY_STORAGE_TILE.get(), pWorldPosition, pBlockState, new PersistentLightEnergyStorage(null, 100, 20, 20));
	}
	
	@Override
	public void setLevel(Level pLevel) {
		super.setLevel(pLevel);
		//SOUND = new LightTechLooper(
		//	this,
		//	SoundRegistry.get("tile.light_tech.generic.activate"),
		//	SoundRegistry.get("tile.light_tech.generic.active_loop"),
		//	SoundRegistry.get("tile.light_tech.generic.deactivate"),
		//	SoundSource.BLOCKS
		//);
		//SOUND.setBaseVolume(0.3f);
	}
	
	public void dbg_ChangeEnergy() {
		if (getLightStored() > 0) {
			extractLightFrom(20, false);
		} else {
			receiveLight(20, false);
		}
	}
	
	/*
	 * Returns a reference to the sound(s) that this emits.
	 *
	 * @return A reference to the sound that this emits.
	 */
	//@Override
	//public LightTechLooper getSoundInstance() {
	//	return SOUND;
	//}
	
	/*
	 * Whether or not the system believes the sound should be playing right now.
	 *
	 * @return Whether or not the sound should be playing right now.
	 */
	//@Override
	//public boolean soundShouldBePlaying() {
	//	return this.getLightStored() > 0;
	//}
	
	@Override
	public float receiveLight(float maxReceive, boolean simulate) {
		float prev = this.getLightStored();
		float amt = super.receiveLight(maxReceive, simulate);
		if (prev == 0 && this.getLightStored() > 0) {
			// was not powered, now it is
			if (level != null) {
				level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, true), StaticData.REPLICATE_CHANGE | StaticData.DO_NOT_NOTIFY_NEIGHBORS);
			}
		}
		return amt;
	}
	
	@Override
	public float extractLightFrom(float maxExtract, boolean simulate) {
		float prev = this.getLightStored();
		float amt = super.extractLightFrom(maxExtract, simulate);
		if (prev > 0 && this.getLightStored() == 0) {
			// was powered, now it is not
			if (level != null) {
				level.setBlock(worldPosition, getBlockState().setValue(BlockStateProperties.POWERED, false), StaticData.REPLICATE_CHANGE | StaticData.DO_NOT_NOTIFY_NEIGHBORS);
			}
		}
		return amt;
	}
}
