package etithespirit.orimod.common.tile.light;

import etithespirit.orimod.client.audio.StartLoopEndBlockSound;
import etithespirit.orimod.common.tile.IAmbientSoundEmitter;
import etithespirit.orimod.lighttech.Assembly;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;

public abstract class LightEnergyTicker<T extends BlockEntity> implements BlockEntityTicker<T> {
	
	public static final LightEnergyTicker<? extends BlockEntity> CLIENT = new Client();
	public static final LightEnergyTicker<? extends BlockEntity> SERVER = new Server();
	
	private LightEnergyTicker() { }
	
	public static class Server<T extends BlockEntity> extends LightEnergyTicker<T> {
		
		private Server() { }
		
		@Override
		public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity be) {
			if (be instanceof AbstractLightEnergyHub hub) {
				if (hub.assembly == null) {
					// Should always have an assembly.
					hub.assembly = Assembly.getAssemblyFor(hub);
				}
			}
		}
	}
	
	public static class Client<T extends BlockEntity> extends LightEnergyTicker<T> {
		
		private Client() { }
		
		@Override
		public void tick(Level level, BlockPos blockPos, BlockState blockState, BlockEntity be) {
			if (be instanceof AbstractLightEnergyHub hub) {
				if (hub.assembly == null) {
					// Should always have an assembly.
					hub.assembly = Assembly.getAssemblyFor(hub);
				}
			}
			
			if (be instanceof IAmbientSoundEmitter cap) {
				StartLoopEndBlockSound sound = cap.getSoundInstance();
				if (sound.terminated() && cap.soundShouldBePlaying()) {
					sound.enqueue();
				}
			}
		}
	}
	
}
