package etithespirit.etimod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.NoSuchElementException;

@SuppressWarnings("unused")
public final class EtiUtils {
	
	private EtiUtils() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * True if this is the development environment and false if this is the live game.
	 */
	public static final boolean IS_DEV_ENV = !FMLEnvironment.production;
	
	/**
	 * A value that can be passed into packed light parameters to indicate full brightness.
	 */
	public static final int FULL_BRIGHT_LIGHT = 0xF000F0;
	
	/**
	 * Given a mod's ID, this will attempt to locate the mod's user-friendly display name through Forge's mod list. Throws an exception if the mod couldn't be found.
	 * @param modid The ID of the mod to locate, or "minecraft"
	 * @return The user-friendly display name associated with the mod ID.
	 * @throws NoSuchElementException If no mod was found with that ID.
	 */
	public static String getModName(String modid) {
		if (modid.equals("minecraft")) return "Minecraft";
		return ModList.get().getModContainerById(modid).get().getModInfo().getDisplayName(); // Let the exception through.
	}
	
	/**
	 * Client only. Whether or not the local player is showing the debug menu (F3)
	 * @return Whether or not the local player is showing the debug menu (F3)
	 */
	public static boolean isPlayerViewingDebugMenu() {
		return Minecraft.getInstance().options.renderDebug;
	}
	
	/**
	 * Client only. Alias method to get the instance of the local player.
	 * @return A reference to the local player.
	 */
	public static PlayerEntity getLocalPlayer() {
		return Minecraft.getInstance().player;
	}
	
	/**
	 * <strong>This will still return true on an integrated server or LAN server.</strong>
	 * @return {@code true} if this build of the game is the playable client.<br/>
	 */
	public static boolean isGameClient() {
		return FMLEnvironment.dist == Dist.CLIENT;
	}
	
	/**
	 * @return {@code true} if this build of the game is the dedicated server build.
	 */
	public static boolean isDedicatedServer() {
		return FMLEnvironment.dist == Dist.DEDICATED_SERVER;
	}
	
	/**
	 * The most reliable and the best way to acquire the side, this checks the isRemote status of the input world.
	 * @param worldIn The world to test.
	 * @return True if the world is clientside, false if it is not.
	 */
	public static boolean isClient(World worldIn) {
		return worldIn.isClientSide;
	}
	
	/**
	 * The most reliable and the best way to acquire the side, this checks the isRemote status of the input world.
	 * @param worldIn The world to test.
	 * @return True if the world is serverside, false if it is not.
	 */
	public static boolean isServer(World worldIn) {
		return !worldIn.isClientSide;
	}
	
	/**
	 * Returns whether or not this entity exists on the client side by checking its world.
	 * @param entityIn The entity to use to acquire the side.
	 * @return True if the given entity exists on the server.
	 */
	public static boolean isClient(Entity entityIn) {
		return isClient(entityIn.getCommandSenderWorld());
	}

	/**
	 * Returns whether or not this entity exists on the server side by checking its world.
	 * @param entityIn The entity to use to acquire the side.
	 * @return True if the given entity exists on the server.
	 */
	public static boolean isServer(Entity entityIn) {
		return isServer(entityIn.getCommandSenderWorld());
	}
	
	/**
	 * Returns true if {@code (value & flag) == flag} (or, {@code value} has the bits defined by {@code flag} all set to 1). 
	 * @param value The value to test.
	 * @param flag The bits of value that should be equal to 1.
	 * @return Whether or not the given value has the given bits set (as defined in flag)
	 */
	public static boolean hasFlag(int value, int flag) {
		return (value & flag) == flag;
	}
	
}
