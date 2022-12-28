package etithespirit.orimod.combat.damage.implementation;

import etithespirit.orimod.combat.damage.IExtendedDamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class ExtendedEntityDamageSource extends EntityDamageSource implements IExtendedDamageSource<ExtendedEntityDamageSource> {
	
	public ExtendedEntityDamageSource(String pDamageTypeId, Entity pEntity) {
		super(pDamageTypeId, pEntity);
	}
	
	private final Map<String, Object> lookup = new HashMap<>();
	private boolean locked = false;
	
	@Override
	public ExtendedEntityDamageSource lock() {
		locked = true;
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <TValue> TValue getCustomData(String key) {
		return (TValue)lookup.get(key);
	}
	
	@Override
	public <TValue> ExtendedEntityDamageSource putCustomData(String key, TValue value) {
		if (locked) throw new IllegalStateException("This source is locked.");
		lookup.put(key, value);
		return this;
	}
}
