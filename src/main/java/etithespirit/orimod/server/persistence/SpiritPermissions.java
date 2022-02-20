package etithespirit.orimod.server.persistence;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.StoredUserList;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;
import java.util.UUID;

public final class SpiritPermissions extends StoredUserList<GameProfile, SpiritPermissions.SpiritEntry> {
	
	public static final File SPIRIT_CHANGE_PERMISSIONS = new File("spirit_permissions.json");
	public static final SpiritPermissions INSTANCE = new SpiritPermissions(SPIRIT_CHANGE_PERMISSIONS);
	
	public SpiritPermissions(File file) {
		super(file);
	}
	
	public void put(Player player, ChangePermissions permissions) {
		
		SpiritEntry entry = getEntry(player);
		if (entry == null) {
			entry = new SpiritEntry(player.getGameProfile(), permissions);
		}
		entry.permissions = permissions;
		super.add(entry);
	}
	
	/**
	 * Remove this player from data persistence. This is identical to calling {@link #put(Player, ChangePermissions)} where the permissions are {@link ChangePermissions#INHERIT_DEFAULT}.
	 * @param player The player to remove.
	 */
	public void remove(Player player) {
		super.remove(player.getGameProfile());
	}
	
	/**
	 * Return the permissions for the given player. Returns {@link ChangePermissions#INHERIT_DEFAULT} if this player has no permissions registered.
	 * @param player The player to check.
	 * @return The permissions the player has, or {@link ChangePermissions#INHERIT_DEFAULT} if this player has no permissions registered.
	 */
	public ChangePermissions get(Player player) {
		SpiritEntry entry = getEntry(player);
		if (entry == null) return ChangePermissions.INHERIT_DEFAULT;
		return entry.permissions;
	}
	
	/**
	 * Returns the raw entry associated with the given player.
	 * @param player The player to check.
	 * @return The entry for the given player, or null if no such entry exists.
	 */
	public @Nullable SpiritEntry getEntry(Player player) {
		return super.get(player.getGameProfile());
	}
	
	@Override
	protected StoredUserEntry<GameProfile> createEntry(JsonObject json) {
		return new SpiritEntry(createGameProfile(json), ChangePermissions.INHERIT_DEFAULT);
	}
	
	public String[] getUserList() {
		return this.getEntries().stream().map(StoredUserEntry::getUser).filter(Objects::nonNull).map(GameProfile::getName).toArray(String[]::new);
	}
	
	protected String getKeyForUser(GameProfile profile) {
		return profile.getId().toString();
	}
	
	private static GameProfile createGameProfile(JsonObject json) {
		String name = null;
		if (json.has("name")) {
			name = json.get("name").getAsString();
		}
		if (json.has("uuid")) {
			String uuidStr = json.get("uuid").getAsString();
			try {
				return new GameProfile(UUID.fromString(uuidStr), name);
			} catch (Throwable ignored) {} // Do nothing if it throws, go down to the name block.
		}
		
		if (name != null) {
			return new GameProfile(null, name);
		}
		return null;
	}
	
	public static final class SpiritEntry extends StoredUserEntry<GameProfile> {
		
		public ChangePermissions permissions;
		
		public SpiritEntry(@Nullable GameProfile profile, ChangePermissions permissions) {
			super(profile);
			this.permissions = permissions;
		}
		
		@Override
		public boolean hasExpired() {
			return this.getUser() != null && permissions.code == -1; // -1 is not saved
		}
		
		@Override
		protected void serialize(JsonObject json) {
			json.addProperty("changeAllowance", permissions.code);
			GameProfile user = this.getUser();
			if (user != null) {
				json.addProperty("uuid", user.getId().toString());
				json.addProperty("name", user.getName());
			} else {
				json.addProperty("_error", "This entry does not have a valid profile. It will be automatically removed.");
			}
		}
	}
	
	public enum ChangePermissions {
		
		/** The associated individual can never change their state regardless of the default allowance. */
		CAN_NEVER_CHANGE(0),
		
		/** The associated individual can always change their state regardless of the default allowance. */
		CAN_ALWAYS_CHANGE(1),
		
		/** The associated individual's ability to change is defined by the default allowance. */
		INHERIT_DEFAULT(-1);
		
		public final int code;
		public final boolean persistent;
		public final boolean canChange;
		
		ChangePermissions(int code) {
			this.code = code;
			persistent = code != -1;
			canChange = code == 1;
		}
		
	}
	
	/*
	
	private static final File SPIRIT_CHANGE_PERMISSIONS = new File("spirit_permissions.json");
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private static final HashMap<UUID, Boolean> CHANGEABILITY = new HashMap<>();
	
	private static boolean defaultState = false;
	private static boolean allowChangingByDefault = true;
	private static boolean alwaysForceAllPlayersToDefault = false;
	
	private static final String DEFAULT_STATE_KEY = "defaultState";
	private static final String ALLOW_CHANGING_KEY = "allowChangingByDefault";
	private static final String FORCE_STATE_KEY = "forcePlayersToUseDefaultState";
	private static final String CHANGEABILITY_KEY = "changeability";
	
	public static void save() {
		JsonObject permissions = new JsonObject();
		permissions.addProperty(DEFAULT_STATE_KEY, defaultState);
		permissions.addProperty(ALLOW_CHANGING_KEY, allowChangingByDefault);
		permissions.addProperty(FORCE_STATE_KEY, alwaysForceAllPlayersToDefault);
		
		JsonObject data = new JsonObject();
		CHANGEABILITY.forEach((uuid, bool) -> {
			data.addProperty(uuid.toString(), bool);
		});
		permissions.add(CHANGEABILITY_KEY, data);
		
		try (BufferedWriter writer = Files.newWriter(SPIRIT_CHANGE_PERMISSIONS, StandardCharsets.UTF_8)) {
			String json = GSON.toJson(permissions);
			writer.write(json);
		} catch (Exception exc) {
			OriMod.LOG.warn("Failed to save spirit permission database! " + exc.toString());
		}
	}
	
	public static void writeTo(FriendlyByteBuf buffer) {
		BitSet set = new BitSet();
		set.set(0, defaultState);
		set.set(1, allowChangingByDefault);
		set.set(2, alwaysForceAllPlayersToDefault);
		buffer.writeBitSet(set);
		buffer.writeMap(CHANGEABILITY, FriendlyByteBuf::writeUUID, FriendlyByteBuf::writeBoolean);
	}
	
	public static void readFrom(FriendlyByteBuf buffer) {
		BitSet set = buffer.readBitSet();
		defaultState = set.get(0);
		allowChangingByDefault = set.get(1);
		alwaysForceAllPlayersToDefault = set.get(2);
		CHANGEABILITY.clear();
		CHANGEABILITY.putAll(buffer.readMap(FriendlyByteBuf::readUUID, FriendlyByteBuf::readBoolean));
	}
	
	public static void load() {
		try (BufferedReader reader = Files.newReader(SPIRIT_CHANGE_PERMISSIONS, StandardCharsets.UTF_8)) {
			JsonObject arbObject = GSON.fromJson(reader, JsonObject.class);
			if (arbObject.has(DEFAULT_STATE_KEY)) defaultState = arbObject.get(DEFAULT_STATE_KEY).getAsBoolean();
			if (arbObject.has(ALLOW_CHANGING_KEY)) allowChangingByDefault = arbObject.get(ALLOW_CHANGING_KEY).getAsBoolean();
			if (arbObject.has(FORCE_STATE_KEY)) alwaysForceAllPlayersToDefault = arbObject.get(FORCE_STATE_KEY).getAsBoolean();
			if (arbObject.has(CHANGEABILITY_KEY)) {
				JsonObject data = arbObject.getAsJsonObject(CHANGEABILITY_KEY);
				for (Map.Entry<String, JsonElement> info : data.entrySet()) {
					try {
						UUID key = UUID.fromString(info.getKey());
						boolean value = info.getValue().getAsBoolean();
						CHANGEABILITY.put(key, value);
					} catch (Exception exc) {
						OriMod.LOG.warn("Failed to read part of 'changeability' in spirit permission database! " + exc.toString());
					}
				}
			}
		} catch (Exception exc) {
			OriMod.LOG.warn("Failed to load spirit permission database! " + exc.toString());
		}
	}
	
	public static @NetworkReplicated void setDefaultState(boolean defaultIsSpirit) {
		if (defaultState == defaultIsSpirit) return;
		defaultState = defaultIsSpirit;
		save();
	}
	
	public static @NetworkReplicated void setDefaultAllowChanging(boolean allowChanging) {
		if (allowChangingByDefault == allowChanging) return;
		allowChangingByDefault = allowChanging;
		save();
	}
	
	public static @NetworkReplicated void setDefaultStateEnforced(boolean isEnforced) {
		if (alwaysForceAllPlayersToDefault == isEnforced) return;
		alwaysForceAllPlayersToDefault = isEnforced;
		save();
	}
	
	
	public static @NetworkReplicated void setPermissions(UUID playerID, ChangePermissions permissions) {
		if (permissions.persistent) {
			CHANGEABILITY.put(playerID, permissions.canChange);
		} else {
			CHANGEABILITY.remove(playerID);
		}
		save();
	}
	
	public static @NetworkReplicated void setPermissions(Player player, ChangePermissions permissions) {
		setPermissions(player.getUUID(), permissions);
	}
	
	public static ChangePermissions getPermissions(UUID playerID) {
		Boolean trilean = CHANGEABILITY.get(playerID);
		if (trilean == null) {
			return ChangePermissions.INHERIT_DEFAULT;
		} else if (trilean.equals(true)) {
			return ChangePermissions.CAN_ALWAYS_CHANGE;
		} else if (trilean.equals(false)) {
			return ChangePermissions.CAN_NEVER_CHANGE;
		}
		throw new RuntimeException("If you are seeing this, start screaming and running in circles.");
	}
	
	public static ChangePermissions getPermissions(Player player) {
		return getPermissions(player.getUUID());
	}
	
	public static boolean getDefaultState() {
		return defaultState;
	}
	
	public static boolean getAllowChangingByDefault() {
		return allowChangingByDefault;
	}
	
	public static boolean getIsDefaultStateForced() {
		return alwaysForceAllPlayersToDefault;
	}
	*/
}
