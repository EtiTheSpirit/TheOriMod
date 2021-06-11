package etithespirit.etimod.valuetypes;

import net.minecraft.entity.EntitySize;
import static etithespirit.etimod.util.ReflectionUtils.modifyFinalField;

import java.lang.reflect.Field;

public class MutableEntitySize extends EntitySize {
	//protected float width;
	//protected float height;
	//protected EntitySize current;
	
	protected static Field widthField;
	protected static Field heightField;
	
	static {
		try {
			widthField = EntitySize.class.getDeclaredField("width");
			heightField = EntitySize.class.getDeclaredField("height");
		} catch (Exception exc) {
			System.err.println(exc);
		}
	}
	

	public MutableEntitySize(float widthIn, float heightIn) {
		super(widthIn, heightIn, false);
		//width = widthIn;
		//height = heightIn;
		//current = new EntitySize(width, height, false);
	}
	
	public MutableEntitySize(EntitySize other) {
		super(other.width, other.height, false);
		//width = other.width;
		//height = other.height;
		//current = new EntitySize(width, height, false);
	}
	
	/**
	 * @return This MutableEntitySize as a {@link net.minecraft.entity.EntitySize}. This will try to return the same object whenever possible. For example, if this MutableEntitySize was constructed with a size of (1, 1), this method will always return a reference to the same EntitySize instance storing (1, 1) no matter how many times it's called, until {@link etithespirit.etimod.util.spirit.MutableEntitySize.setTo} is called, from which case a different EntitySize will be constructed if the new size is actually different. 
	 */
	@Deprecated
	public EntitySize asEntitySize() {
		/*
		if (current == null) {
			// Should never happen but this return value is not nullable so the catch case should be here no matter what.
			current = new EntitySize(width, height, false);
		}
		return current;
		*/
		return this;
	}
	
	/**
	 * Identical to asEntitySize, but this will always return a new instance rather than a cached instance. 
	 */
	@Deprecated
	public EntitySize asNewEntitySize() {
		return new EntitySize(width, height, false);
	}
	
	/**
	 * Sets the width and height of this instance to the other's width and height
	 * @param other The EntitySize containing the new size information.
	 */
	public void setTo(EntitySize other) {
		if (other.width == width && other.height == height) return; // Not changing.
		/*
		width = other.width;
		height = other.height;
		*/
		modifyFinalField(this, widthField, other.width);
		modifyFinalField(this, heightField, other.height);
		//current = new EntitySize(width, height, false);
	}
	
	
	/**
	 * Sets the width and height of this instance to the given width and height.
	 * @param w The new width
	 * @param h The new height
	 * @return
	 */
	public void setTo(float w, float h) {
		if (w == width && h == height) return; // Not changing.
		/*
		width = w;
		height = h;
		*/
		modifyFinalField(this, widthField, width);
		modifyFinalField(this, heightField, height);
		//current = new EntitySize(w, h, false);
	}
	
	
	/**
	 * Returns a new MutableEntitySize instance that has been scaled by the given factor. To modify <em>this</em> MutableEntitySize, use {@code scaleLocal(factor)}.
	 * @param factor The uniform factor (both width and height).
	 * @return A new MutableEntitySize instance that has a size of (width * factor, height * factor).
	 */
	@Override
	public MutableEntitySize scale(float factor) {
		return scale(factor, factor);
	}
	
	/**
	 * Returns a new MutableEntitySize instance that has been scaled by the given factors. To modify <em>this</em> MutableEntitySize, use {@code scaleLocal(x, y)}.
	 * @param x The width multiplier.
	 * @param y The height multiplier.
	 * @return A new MutableEntitySize instance that has a size of (width * x, height * y).
	 */
	@Override
	public MutableEntitySize scale(float x, float y) {
		MutableEntitySize size = new MutableEntitySize(width * x, height * y);
		return size;
	}
	
	/**
	 * Scales THIS MutableEntitySize by the given factor.
	 * @param factor The uniform factor (both width and height).
	 */
	public void scaleLocal(float factor) {
		scaleLocal(factor, factor);
	}
	
	/**
	 * Scales THIS MutableEntitySize by the given factors.
	 * @param x The width multiplier.
	 * @param y The height multiplier.
	 */
	public void scaleLocal(float x, float y) {
		modifyFinalField(this, widthField, width * x);
		modifyFinalField(this, heightField, height * y);
		//width *= x;
		//height *= y;
		//current = new EntitySize(width, height, false);
	}
	
	/**
	 * Using a rough estimate, this returns whether or not the given values are equal to the width and height of this size.
	 * This checks if the two values are super close to each other (within 0.00001 blocks)
	 * @param x The desired width.
	 * @param y The desired height.
	 * @return Whether or not the size of this instance is close enough to X and Y.
	 */
	public boolean sizeIs(float x, float y) {
		return (Math.abs(width - x) < EPSILON && Math.abs(height - y) < EPSILON);
	}
	private static final double EPSILON = 0.00001f;
	
	public String toString() {
		return "MutableEntityDimensions w=" + width + ", h=" + height;
	}
}
