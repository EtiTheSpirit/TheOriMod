package etithespirit.orimod.client.audio;

import etithespirit.orimod.OriMod;
import etithespirit.orimod.annotation.ClientUseOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.TickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.phys.Vec3;

@ClientUseOnly
public class StartLoopEndBlockSound {
	
	public final SoundEvent start;
	public final LoopingLightEnergyBlockSound loop;
	public final SoundEvent end;
	protected boolean terminated = false;
	protected float baseVolume = 0.2f;
	private static final LegacyRandomSource RNG = new LegacyRandomSource(127831789L);
	
	public StartLoopEndBlockSound(SoundEvent start, LoopingLightEnergyBlockSound loop, SoundEvent end) {
		this.start = start;
		this.loop = loop;
		this.end = end;
		
		loop.baseVolume = baseVolume;
		
		loop.onStartup = () -> {
			Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
				start,
				SoundSource.BLOCKS,
				baseVolume,
				1,
				RNG,
				loop.block.getBlockPos()
			));
		};
		loop.onShutdown = () -> {
			Minecraft.getInstance().getSoundManager().play(new SimpleSoundInstance(
				end,
				SoundSource.BLOCKS,
				baseVolume,
				1,
				RNG,
				loop.block.getBlockPos()
			));
			terminated = true;
		};
	}
	
	public void setBaseVolume(float baseVolume) {
		loop.baseVolume = baseVolume;
		this.baseVolume = baseVolume;
	}
	
	public boolean terminated() {
		return terminated;
	}
	
	public void enqueue() {
		terminated = false;
		loop.reset();
		Minecraft.getInstance().getSoundManager().queueTickingSound(loop);
	}
}
