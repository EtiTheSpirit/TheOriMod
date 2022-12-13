package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.client.audio.LightTechLooper;
import etithespirit.orimod.common.block.StaticData;
import etithespirit.orimod.common.block.light.decoration.ForlornAppearanceMarshaller;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.common.tile.IClientUpdatingTile;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.common.tile.light.implementations.LightRepairBoxTile;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A universal ticker for all Light-based tile entities.
 * @param <T> tea (real) (confirmed bri'ish)
 */
public abstract class LightEnergyTicker<T extends BlockEntity> implements BlockEntityTicker<T> {
	
	/** The ticker that operates on the clientside. */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final LightEnergyTicker<? extends BlockEntity> CLIENT = new Client();
	
	/** The ticker that operates on the serverside. */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final LightEnergyTicker<? extends BlockEntity> SERVER = new Server();
	
	private LightEnergyTicker() { }
	
	static class Server<T extends BlockEntity> extends LightEnergyTicker<T> {
		
		private Server() { }
		
		@Override
		public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity be) {
			int remainingBranches = OriModConfigs.MAX_ASSEMBLY_ITERATIONS.get();
			
			if (be instanceof LightEnergyTile eTile) {
				eTile.neighbors(); // Populate!
			}
			
			if (be instanceof LightEnergyStorageTile thisStorage) {
				boolean rx = thisStorage.canReceiveLight();
				boolean tx = thisStorage.canExtractLightFrom();
				LightEnergyTile[] tiles = thisStorage.getAllConnectedStorage(remainingBranches);
				if (rx && tx) {
					// can do both, try to reach equilibrium
					for (LightEnergyTile tile : tiles) {
						if (tile == be) continue; // Skip if this is affecting itself.
						
						if (tile instanceof LightEnergyStorageTile otherStorage) {
							boolean otherRx = otherStorage.canReceiveLight();
							boolean otherTx = otherStorage.canExtractLightFrom();
							float otherDifference = otherStorage.getLightStored() - thisStorage.getLightStored();
							if (otherRx && otherTx) {
								// Other can do both, nice
								// Cut in half to reach eq
								otherDifference /= 2;
								if (otherDifference > 0) {
									// other has more, I take some
									ILightEnergyStorage.transferLight(otherStorage, thisStorage, otherDifference, false);
								} else if (otherDifference < 0) {
									// I have more, I give some (invert otherDifference)
									ILightEnergyStorage.transferLight(thisStorage, otherStorage, -otherDifference, false);
								}
							} else if (otherRx) {
								// other can receive, transfer iff other has less
								if (otherDifference < 0) {
									// I have more, I give some (invert otherDifference)
									ILightEnergyStorage.transferLight(thisStorage, otherStorage, -otherDifference, false);
								}
							} else if (otherTx) {
								// other can transmit, transfer iff I have less
								if (otherDifference > 0) {
									// other has more, I take some
									ILightEnergyStorage.transferLight(otherStorage, thisStorage, otherDifference, false);
								}
							}
						}
					}
				} else if (rx) {
					// can only receive
					for (LightEnergyTile tile : tiles) {
						if (tile instanceof LightEnergyStorageTile otherStorage) {
							if (otherStorage.canExtractLightFrom()) {
								// We want to receive, other wants to transmit. We will be greedy and try to take everything.
								ILightEnergyStorage.transferLight(otherStorage, thisStorage, otherStorage.getLightStored(), false);
							}
						}
					}
				} else if (tx) {
					// can only transmit
					for (LightEnergyTile tile : tiles) {
						if (tile instanceof LightEnergyStorageTile otherStorage) {
							if (otherStorage.canReceiveLight()) {
								// It's first come first serve
								ILightEnergyStorage.transferLight(thisStorage, otherStorage, thisStorage.getLightStored(), false);
							}
						}
					}
				}
				
				if (!thisStorage.skipAutomaticPoweredBlockstate()) {
					boolean appearsPowered = be.getBlockState().getValue(ForlornAppearanceMarshaller.POWERED);
					boolean isActuallyPowered = thisStorage.getLightStored() > 0;
					if (appearsPowered != isActuallyPowered) {
						level.setBlock(be.getBlockPos(), be.getBlockState().setValue(ForlornAppearanceMarshaller.POWERED, isActuallyPowered), StaticData.REPLICATE_CHANGE | StaticData.DO_NOT_NOTIFY_NEIGHBORS);
					}
				}
			}
			
			
			if (be instanceof IServerUpdatingTile updating) {
				updating.updateServer(level, blockPos, blockState);
			}
		}
	}
	
	static class Client<T extends BlockEntity> extends LightEnergyTicker<T> {
		
		private Client() { }
		
		@Override
		public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity be) {
			
			if (be instanceof IClientUpdatingTile updating) {
				updating.updateClient(level, blockPos, blockState);
			}
			
			// TODO: Fix issue where players can spam the shit out of these blocks and blast really loud looping audio
			if (be instanceof IAmbientSoundEmitter cap) {
				LightTechLooper sound = cap.getSoundInstance();
				if (cap.soundShouldBePlaying()) {
					sound.play();
				} else {
					sound.stop();
				}
			}
		}
	}
	
}
