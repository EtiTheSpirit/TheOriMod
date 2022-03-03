package etithespirit.orimod;

import etithespirit.orimod.annotation.ServerUseOnly;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.NoSuchElementException;

/**
 * A set of general utilities that the mod may or may not use.
 */
public final class GeneralUtils {
	
	private GeneralUtils() { throw new UnsupportedOperationException("Attempt to create new instance of static class " + this.getClass().getSimpleName()); }
	
	/**
	 * True if this is the development environment and false if this is the live game.
	 */
	public static final boolean IS_DEV_ENV = !FMLEnvironment.production;
	
	/**
	 * A value that can be passed into packed light parameters to indicate full brightness.
	 * Lightmaps are a UV graph of two short values packed as an int
	 */
	public static final int FULL_BRIGHT_LIGHT = 0x00F0_00F0;
	
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
	public static LocalPlayer getLocalPlayer() {
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
	public static boolean isClient(Level worldIn) {
		return worldIn.isClientSide;
	}
	
	/**
	 * The most reliable and the best way to acquire the side, this checks the isRemote status of the input world.
	 * @param worldIn The world to test.
	 * @return True if the world is serverside, false if it is not.
	 */
	public static boolean isServer(Level worldIn) {
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
	public static boolean hasAllFlags(int value, int flag) {
		return (value & flag) == flag;
	}
	
	/**
	 * Returns true if {@code (value & flag) > 0} (or, {@code value} has at least one of the bits defined by {@code flag} set to 1).
	 * @param value The value to test.
	 * @param flag The bits of value that should be equal to 1.
	 * @return Whether or not the given value has at least one of the given bits set (as defined in flag)
	 */
	public static boolean hasAnyFlag(int value, int flag) {
		return (value & flag) > 0;
	}
	
	/**
	 * Shows text at the bottom of the screen like when a new item is equipped.
	 * @param player The player to show it for.
	 * @param message The message to display.
	 */
	@ServerUseOnly
	public static void message(ServerPlayer player, String message) {
		player.sendMessage(new TranslatableComponent(message), ChatType.GAME_INFO, Util.NIL_UUID);
	}
}
