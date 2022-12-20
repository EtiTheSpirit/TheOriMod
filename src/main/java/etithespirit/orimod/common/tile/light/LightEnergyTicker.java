package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.client.audio.LightTechLooper;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.common.tile.IClientUpdatingTile;
import etithespirit.orimod.common.tile.IServerUpdatingTile;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.energy.ILightEnergyConsumer;
import etithespirit.orimod.energy.ILightEnergyGenerator;
import etithespirit.orimod.energy.ILightEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * A universal ticker for all Light-based tile entities.
 * @param <T> tea (real) (confirmed bri'ish)
 */
public abstract class LightEnergyTicker<T extends BlockEntity> implements BlockEntityTicker<T> {
	private LightEnergyTicker() { }
	
	/** The ticker that operates on the clientside. */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final LightEnergyTicker<? extends BlockEntity> CLIENT = new Client();
	
	/** The ticker that operates on the serverside. */
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static final LightEnergyTicker<? extends BlockEntity> SERVER = new Server();
	
	
	static class Server<T extends BlockEntity> extends LightEnergyTicker<T> {
		
		private Server() { }
		
		private static @Nullable ILightEnergyGenerator asGenerator(LightEnergyHandlingTile tile) {
			if (tile instanceof ILightEnergyGenerator generator) return generator;
			return null;
		}
		
		private static @Nullable ILightEnergyConsumer asConsumer(LightEnergyHandlingTile tile) {
			if (tile instanceof ILightEnergyConsumer consumer) return consumer;
			return null;
		}
		
		private static @Nullable ILightEnergyStorage asStorage(LightEnergyHandlingTile tile) {
			if (tile instanceof ILightEnergyStorage storage) return storage;
			return null;
		}
		
		@Override
		public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity be) {
			int remainingBranches = OriModConfigs.MAX_ASSEMBLY_ITERATIONS.get();
			
			if (be instanceof LightEnergyTile eTile) {
				eTile.neighbors(); // Populate!
				eTile.updateVisualPoweredAppearance();
			}
			
			if (be instanceof LightEnergyHandlingTile thisTile) {
				ILightEnergyGenerator generator = asGenerator(thisTile);
				ILightEnergyConsumer consumer = asConsumer(thisTile);
				ILightEnergyStorage storage = asStorage(thisTile);
				LightEnergyTile[] tiles = thisTile.getAllConnectedHandlers(remainingBranches);
				
				if (generator != null && consumer != null) {
					throw new UnsupportedOperationException();
				}
				
				if (storage != null) {
					for (LightEnergyTile tile : tiles) {
						if (tile == be) continue; // Skip if this is affecting itself.
						
						if (tile instanceof LightEnergyHandlingTile otherTile) {
							if (otherTile instanceof ILightEnergyStorage otherStorage) {
								float difference = otherStorage.getLightStored() - storage.getLightStored();
								difference /= 2;
								if (difference > 0) {
									ILightEnergyStorage.transferLight(otherStorage, storage, difference, false);
								} else if (difference < 0) {
									ILightEnergyStorage.transferLight(storage, otherStorage, -difference, false);
								}
							} else if (otherTile instanceof ILightEnergyGenerator otherGenerator) {
								ILightEnergyStorage.storeFromGenerator(otherGenerator, storage, Float.POSITIVE_INFINITY, false);
							} else if (otherTile instanceof ILightEnergyConsumer otherConsumer) {
								ILightEnergyStorage.consumeFromStorage(storage, otherConsumer, Float.POSITIVE_INFINITY, false);
							}
						}
					}
				} else if (generator != null) {
					for (LightEnergyTile tile : tiles) {
						if (tile == be) continue; // Skip if this is affecting itself.
						
						if (tile instanceof LightEnergyHandlingTile otherTile) {
							if (otherTile instanceof ILightEnergyStorage otherStorage) {
								ILightEnergyStorage.storeFromGenerator(generator, otherStorage, Float.POSITIVE_INFINITY, false);
							} else if (otherTile instanceof ILightEnergyConsumer otherConsumer) {
								ILightEnergyStorage.consumeFromGenerator(generator, otherConsumer, Float.POSITIVE_INFINITY, false);
							}
						}
					}
				} else if (consumer != null) {
					for (LightEnergyTile tile : tiles) {
						if (tile == be) continue; // Skip if this is affecting itself.
						
						if (tile instanceof LightEnergyHandlingTile otherTile) {
							if (otherTile instanceof ILightEnergyStorage otherStorage) {
								ILightEnergyStorage.consumeFromStorage(otherStorage, consumer, Float.POSITIVE_INFINITY, false);
							} else if (otherTile instanceof ILightEnergyGenerator otherGenerator) {
								ILightEnergyStorage.consumeFromGenerator(otherGenerator, consumer, Float.POSITIVE_INFINITY, false);
							}
						}
					}
				}
			}
			
			/*
			if (be instanceof LightEnergyHandlingTile thisStorage) {
				boolean rx = thisStorage.canReceiveLight();
				boolean tx = thisStorage.canExtractLightFrom();
				LightEnergyTile[] tiles = thisStorage.getAllConnectedStorage(remainingBranches);
				if (rx && tx) {
					// can do both, try to reach equilibrium
					for (LightEnergyTile tile : tiles) {
						if (tile == be) continue; // Skip if this is affecting itself.
						
						if (tile instanceof LightEnergyHandlingTile otherStorage) {
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
						if (tile instanceof LightEnergyHandlingTile otherStorage) {
							if (otherStorage.canExtractLightFrom()) {
								// We want to receive, other wants to transmit. We will be greedy and try to take everything.
								ILightEnergyStorage.transferLight(otherStorage, thisStorage, otherStorage.getLightStored(), false);
							}
						}
					}
				} else if (tx) {
					// can only transmit
					for (LightEnergyTile tile : tiles) {
						if (tile instanceof LightEnergyHandlingTile otherStorage) {
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
			 */
			
			
			if (be instanceof IServerUpdatingTile updating) {
				updating.updateServer(level, blockPos, blockState);
			}
		}
	}
	
	static class Client<T extends BlockEntity> extends LightEnergyTicker<T> {
		
		private Client() { }
		
		@Override
		public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity be) {
			
			// TODO: Fix issue where players can spam the shit out of these blocks and blast really loud looping audio
			if (be instanceof IAmbientSoundEmitter cap) {
				LightTechLooper sound = cap.getSoundInstance();
				if (cap.soundShouldBePlaying()) {
					sound.play();
				} else {
					sound.stop();
				}
			}
			
			if (be instanceof IClientUpdatingTile updating) {
				updating.updateClient(level, blockPos, blockState);
			}
		}
	}
	
}
