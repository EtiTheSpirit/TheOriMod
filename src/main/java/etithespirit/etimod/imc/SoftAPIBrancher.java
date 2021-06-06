package etithespirit.etimod.imc;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import org.spongepowered.asm.mixin.injection.invoke.arg.ArgumentCountException;

import com.mojang.datafixers.util.Function3;

import etithespirit.etimod.util.blockmtl.BlockToMaterialBinding;
import etithespirit.etimod.util.blockmtl.SpiritMaterialModState;
import etithespirit.etimod.util.spirit.SpiritIdentifier;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public final class SoftAPIBrancher {
	
	private static final BiFunction<String, Object[], Object> GENERIC_INTERFACE = (mtd, params) -> {
		if (mtd.equals("isSpirit")) {
			if (params.length != 1) {
				throw new ArgumentCountException(params.length, 1, "isSpirit takes in one argument, which is a PlayerEntity or UUID.");
			}
			Object playerRef = params[0];
			if (playerRef instanceof PlayerEntity) {
				return isPlayerSpirit((PlayerEntity)playerRef);
			} else if (playerRef instanceof UUID) {
				return isUUIDSpirit((UUID)playerRef);
			} else if (playerRef instanceof String) {
				UUID id = UUID.fromString((String)playerRef);
				// ^ Just let it throw if it fails.
				return isUUIDSpirit(id);
			}
		}
		throw new InvalidMethodException(mtd);
	};
	
	private static final BiConsumer<ResourceLocation, Function3<Entity, BlockPos, BlockPos, String>> COMPLEX_MATERIAL_REGISTRY = (blockRsrc, cndFunc) -> {
		final RegistryObject<Block> block = RegistryObject.of(blockRsrc, ForgeRegistries.BLOCKS);
		BlockToMaterialBinding.setSubMaterialConditionFor(block.get(), ForGenericStringFunc(cndFunc));
	};
	
	/**
	 * Transforms Function3&lt;Entity, BlockPos, BlockPos, String&gt; into Function3&lt;Entity, BlockPos, BlockPos, SpiritMaterialModState&gt; by mutating the return type.
	 * @param generic
	 * @return
	 */
	private static final Function3<Entity, BlockPos, BlockPos, SpiritMaterialModState> ForGenericStringFunc(Function3<Entity, BlockPos, BlockPos, String> generic) {
		return (entity, blockOnPos, blockInPos) -> {
			String raw = generic.apply(entity, blockOnPos, blockInPos);
			return SpiritMaterialModState.valueOf(raw);
		};
	}
	
	/**
	 * Returns a BiFunction that serves as the interface for EtiCore and any cooperative mods.
	 * @return
	 */
	public static BiFunction<String, Object[], Object> getGenericInterface() {
		return GENERIC_INTERFACE;
	}
	
	public static BiConsumer<ResourceLocation, Function3<Entity, BlockPos, BlockPos, String>> getComplexMaterialRegistry() {
		return COMPLEX_MATERIAL_REGISTRY;
	}
	
	private static boolean isPlayerSpirit(PlayerEntity player) {
		return isUUIDSpirit(player.getUniqueID());
	}
	
	private static boolean isUUIDSpirit(UUID id) {
		return SpiritIdentifier.isIDSpirit(id);
	}

}
