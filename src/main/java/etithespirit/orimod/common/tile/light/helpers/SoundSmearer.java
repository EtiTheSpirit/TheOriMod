package etithespirit.orimod.common.tile.light.helpers;

import etithespirit.orimod.annotation.ServerUseOnly;

/**
 * This utility class is intended for Light devices that consume or generate power and emit a sound while doing so.
 * It keeps track of a duration of time based on ticks that keeps the device's sound active a short time after it loses power, so that
 * when it pulsates, it doesn't spam on/off sounds.
 */
public final class SoundSmearer {
	private final int tickThreshold;
	private int ticksSinceLastAction = 0;
	private int ticksSinceLastNonAction = 0;
	private int dualBiasTickCount = 0;
	private final SmearDirection direction;
	
	public SoundSmearer(SmearDirection direction, int tickThreshold) {
		this.direction = direction;
		this.tickThreshold = tickThreshold;
	}
	
	public void tick(boolean hadAction) {
		if (hadAction) {
			if (ticksSinceLastNonAction < tickThreshold) ticksSinceLastNonAction++;
			if (dualBiasTickCount < tickThreshold) dualBiasTickCount++;
			ticksSinceLastAction = 0;
			
		} else {
			if (ticksSinceLastAction < tickThreshold) ticksSinceLastAction++;
			if (dualBiasTickCount > -tickThreshold) dualBiasTickCount--;
			ticksSinceLastNonAction = 0;
			
		}
	}
	
	public boolean shouldSoundPlay() {
		if (direction == SmearDirection.DELAY_TURNING_ON) {
			return ticksSinceLastNonAction >= tickThreshold;
		} else if (direction == SmearDirection.DELAY_TURNING_OFF) {
			return ticksSinceLastAction < tickThreshold;
		} else {
			return dualBiasTickCount >= 0;
		}
	}
	
	public enum SmearDirection {
		DELAY_TURNING_ON,
		DELAY_TURNING_OFF,
		DELAY_BOTH
	}
	
}
