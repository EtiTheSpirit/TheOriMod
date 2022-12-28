package etithespirit.orimod.combat.damage.implementation;

import etithespirit.orimod.combat.damage.IExtendedDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ExtendedIndirectEntityDamageSource extends IndirectEntityDamageSource implements IExtendedDamageSource<ExtendedIndirectEntityDamageSource> {
	
	public ExtendedIndirectEntityDamageSource(String pDamageTypeId, Entity pSource, @Nullable Entity pIndirectEntity) {
		super(pDamageTypeId, pSource, pIndirectEntity);
	}
	
	private final Map<String, Object> lookup = new HashMap<>();
	private boolean locked = false;
	
	@Override
	public ExtendedIndirectEntityDamageSource lock() {
		locked = true;
		return this;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <TValue> TValue getCustomData(String key) {
		return (TValue)lookup.get(key);
	}
	
	@Override
	public <TValue> ExtendedIndirectEntityDamageSource putCustomData(String key, TValue value) {
		if (locked) throw new IllegalStateException("This source is locked.");
		lookup.put(key, value);
		return this;
	}
}
