package etithespirit.etimod.common.command;

import java.util.UUID;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import etithespirit.etimod.util.EtiUtils;
import etithespirit.etimod.util.spirit.SpiritIdentificationType;
import etithespirit.etimod.util.spirit.SpiritIdentifier;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class SetSpiritCommand {
	
	/** My Universally Unique ID */
	@Deprecated private static final UUID ETIS_UNIQUE_ID = UUID.fromString("8e3bfbb2-db95-423c-b321-4eab2d8e0e8c");
	
	@Deprecated private static final UUID EGGYS_UNIQUE_ID = UUID.fromString("bbcad97d-c9ff-4da5-a7ba-c93e79f3aa6b");
	
	/** Determines whether or not the given player can use the command. */
	@SuppressWarnings("unused")
	@Deprecated 
	private static final boolean CanUseToggleCommand(PlayerEntity player) {
		if (EtiUtils.IS_DEV_ENV) return true; // Development environment, not the live game.
		
		// If there's no player, just abort early and return false.
		if (player == null) return false;
		
		if (player.getUUID().equals(ETIS_UNIQUE_ID) || player.getUUID().equals(EGGYS_UNIQUE_ID)) return true;
		
		// If all else fails, return false.
		return false;
	}
	
	@SuppressWarnings("resource")
	private static final LiteralArgumentBuilder<CommandSource> CommandInstance = Commands.literal("togglespirit")
			/*.requires((user) -> {
				
				try {
					// Is the mod the public build? If so, this always returns true.
					if (!EtiMod.IS_PRIVATE_DISTRO) return true;
					
					// Otherwise, check.
					return CanUseToggleCommand(user.asPlayer());
				} catch (CommandSyntaxException e) {
					user.sendErrorMessage(new StringTextComponent(e.getMessage()));
				}
				return false;
			})*/
			.executes((src) -> {
				boolean isSpirit = SpiritIdentifier.isSpirit(src.getSource().getPlayerOrException(), SpiritIdentificationType.FROM_PLAYER_MODEL);
				if (src.getSource().getPlayerOrException().getCommandSenderWorld().isClientSide) {
					ReplicateMorphStatus.askToSetSpiritStatusAsync(!isSpirit);
					src.getSource().sendSuccess(new StringTextComponent("Sent a request to set your spirit status to " + (!isSpirit) + " to the server."), false);
				} else {
					ReplicateMorphStatus.tellEveryonePlayerSpiritStatus(src.getSource().getPlayerOrException().getUUID(), !isSpirit);
				}
				return 0;
			});
			
	
	/*
	private static final LiteralArgumentBuilder<CommandSource> CommandInstance2 = Commands.literal("setspiritother")
			.then(Commands.argument("shouldBeSpirit",	BoolArgumentType.bool()))
			.then(Commands.argument("player",			GameProfileArgument.gameProfile()))
			.requires((src) -> {
				return src.hasPermissionLevel(src.getServer().getOpPermissionLevel());
			})
			.executes(
			(src) -> {
				ServerPlayerEntity player = src.getSource().asPlayer();
				UUID targetID;
				if (player == null) {
					GameProfile targetPlayer = src.getArgument("player", GameProfile.class);
					targetID = targetPlayer.getId();
					ReplicateMorphStatus.TellEveryonePlayerSpiritStatus(targetID, src.getArgument("shouldBeSpirit", Boolean.class));
					return 0;
				}
				if (player.getEntityWorld().isRemote) {
					// client
					if (player.hasPermissionLevel(player.server.getOpPermissionLevel())) {
						GameProfile targetPlayer = src.getArgument("player", GameProfile.class);
						targetID = targetPlayer.getId();
						ReplicateMorphStatus.AskToSetSpiritStatus(targetID, src.getArgument("shouldBeSpirit", Boolean.class));
						return 0;
					} else {
						src.getSource().sendErrorMessage(new StringTextComponent("You aren't an op and can't change the model of other players."));
						return 0;
					}
				} else {
					// server
					GameProfile targetPlayer = src.getArgument("player", GameProfile.class);
					targetID = targetPlayer.getId();
					ReplicateMorphStatus.TellEveryonePlayerSpiritStatus(targetID, src.getArgument("shouldBeSpirit", Boolean.class));
					return 0;
				}
			});
	*/
	public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(CommandInstance);
		// dispatcher.register(CommandInstance2);
	}
	
}
