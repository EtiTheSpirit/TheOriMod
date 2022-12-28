package etithespirit.orimod.combat.damage.implementation;

import etithespirit.orimod.combat.damage.IExtendedDamageSource;
import net.minecraft.world.damagesource.DamageSource;

import java.util.HashMap;
import java.util.Map;

public class ExtendedDamageSource extends DamageSource implements IExtendedDamageSource<ExtendedDamageSource> {
	public ExtendedDamageSource(String pMessageId) {
		super(pMessageId);
	}
	
	private final Map<String, Object> lookup = new HashMap<>();
	private boolean locked = false;
	
	@Override
	public ExtendedDamageSource lock() {
		locked = true;
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <TValue> TValue getCustomData(String key) {
		return (TValue)lookup.get(key);
	}
	
	@Override
	public <TValue> ExtendedDamageSource putCustomData(String key, TValue value) {
		if (locked) throw new IllegalStateException("This source is locked.");
		lookup.put(key, value);
		return this;
	}
}
