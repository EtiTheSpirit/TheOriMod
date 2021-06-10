package etithespirit.etimod.util;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.loading.FMLEnvironment;

public class EtiUtils {
	
	/**
	 * True if this is the development environment and false if this is the live game.
	 */
	public static final boolean IS_DEV_ENV = FMLEnvironment.production;
	
	/**
	 * Given a mod's ID, this will attempt to locate the mod's user-friendly display name through Forge's mod list. Throws an exception if the mod couldn't be found.
	 * @param modid
	 * @return
	 */
	public static String getModName(String modid) {
		if (modid == "minecraft") return "Minecraft";
		return ModList.get().getModContainerById(modid).get().getModInfo().getDisplayName();
	}
	
	/**
	 * Returns {@code true} if this is running on a physical game client (the game you play on).<br/>
	 * This is identical to calling {@code IsClient(SideType.Physical)}
	 * @return
	 */
	@Deprecated
	public static boolean isClient() {
		return FMLEnvironment.dist == Dist.CLIENT;
	}
	
	/**
	 * Returns {@code true} if this is running on a literal server app of Minecraft (the server jar).<br/>
	 * This is identical to calling {@code IsServer(SideType.Physical)}
	 * @return
	 */
	@Deprecated
	public static boolean isServer() {
		return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
	}
	
	/**
	 * Returns {@code true} if this is running on the client side (determined by the input {@code SideType}.)<br/>
	 * This method is not ideal in that it attempts to guess from the current thread if type is Logical
	 * @param type
	 * @return
	 */
	@Deprecated
	public static boolean isClient(SideType type) {
		if (type == SideType.PHYSICAL) return isClient();
		return Thread.currentThread().getThreadGroup() == SidedThreadGroups.CLIENT;
	}
	
	/**
	 * Returns {@code true} if this is running on the server side (determined by the input {@code SideType}.)<br/>
	 * This method is not ideal in that it attempts to guess from the current thread if type is Logical
	 * @param type
	 * @return
	 */
	@Deprecated
	public static boolean isServer(SideType type) {
		if (type == SideType.PHYSICAL) return isServer();
		return Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER;
	}
	
	/**
	 * The most reliable and the best way to acquire the side, this checks the isRemote status of the input world.
	 * @param worldIn
	 * @return
	 */
	public static boolean isClient(World worldIn) {
		return worldIn.isRemote;
	}
	
	/**
	 * The most reliable and the best way to acquire the side, this checks the isRemote status of the input world.
	 * @param worldIn
	 * @return
	 */
	public static boolean isServer(World worldIn) {
		return !worldIn.isRemote;
	}
	
	/**
	 * Returns whether or not this entity exists on the client side by checking its world.
	 * @param entityIn
	 * @return
	 */
	public static boolean isClient(Entity entityIn) {
		return isClient(entityIn.getEntityWorld());
	}

	/**
	 * Returns whether or not this entity exists on the server side by checking its world.
	 * @param entityIn
	 * @return
	 */
	public static boolean isServer(Entity entityIn) {
		return isServer(entityIn.getEntityWorld());
	}
	
	/**
	 * Returns true if {@code (value & flag) == flag} (or, {@code value} has the bits defined by {@code flag} all set to 1). 
	 * @param value
	 * @param flag
	 * @return
	 */
	public static boolean hasFlag(int value, int flag) {
		return (value & flag) == flag;
	}
	
}
