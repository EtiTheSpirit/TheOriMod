package etithespirit.mixin;

import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(World.class)
public abstract class InjectBlockEntityUpdates extends net.minecraftforge.common.capabilities.CapabilityProvider<World> {
	protected InjectBlockEntityUpdates(Class<World> baseClass) { super(baseClass); }
	
	
}
