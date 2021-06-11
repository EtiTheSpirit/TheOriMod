package etithespirit.etimod.util.autoeffect;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;

public abstract class SimpleEffect extends Effect {
	
	protected EffectType type;
	protected int liquidColor;
	
	private boolean hasGottenType = false;
	private boolean hasGottenColor = false;
	private EffectType storedType = EffectType.NEUTRAL;
	private int storedColor = 0;
	
	public SimpleEffect() {
		super(EffectType.NEUTRAL, 0);
	}

	private SimpleEffect(EffectType typeIn, int liquidColorIn) {
		super(typeIn, liquidColorIn);
	}
	
	@Override
	public EffectType getCategory() {
		if (!hasGottenType) {
			storedType = getType();
			this.type = storedType;
			hasGottenType = true;
		}
		return storedType;
	}
	
	@Override
	public int getColor() {
		if (!hasGottenColor) {
			storedColor = getCustomColor();
			this.liquidColor = storedColor;
			hasGottenColor = true;
		}
		return storedColor;
	}
	
	/**
	 * Returns the default type for this effect. This value is cached and this method will only be called once.
	 * @return
	 */
	public abstract EffectType getType();
	
	/**
	 * Returns the default color for this effect. This value is cached and this method will only be called once.
	 * @return
	 */
	public abstract int getCustomColor();

}
