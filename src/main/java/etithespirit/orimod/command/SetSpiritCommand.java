package etithespirit.orimod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.server.persistence.SpiritPermissions;
import etithespirit.orimod.spirit.SpiritIdentifier;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class SetSpiritCommand {
	
	private static final SimpleCommandExceptionType INSUFFICIENT_PERMS = new SimpleCommandExceptionType(() -> "You do not have permission to change if other players are Spirits.");
	private static final SimpleCommandExceptionType INSUFFICIENT_PERMS_SELF = new SimpleCommandExceptionType(() -> "You do not have permission to set whether or not you are a Spirit.");
	private static final SimpleCommandExceptionType SERVER_FORCES_STATE = new SimpleCommandExceptionType(() -> "This server is enforcing that players always use the current state. You cannot change it.");
	
	private static final LiteralArgumentBuilder<CommandSourceStack> COMMAND_INSTANCE = Commands
		.literal("spirit")
			.then(Commands.literal("set")
				.then(Commands.argument("player", EntityArgument.player())
					.then(Commands.argument("isSpirit", BoolArgumentType.bool())
						.executes(
							ctx -> setSpirit(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), BoolArgumentType.getBool(ctx, "isSpirit"))
						)
					)
				)
			)
			.then(Commands.literal("get")
				.then(Commands.argument("player", EntityArgument.player())
					.executes(
						ctx -> getSpirit(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))
					)
				)
			);
		
	
	private static int setSpirit(CommandSourceStack src, Player player, boolean isSpirit) throws CommandSyntaxException {
		if (OriModConfigs.FORCE_STATE.get()) {
			src.sendFailure(new TranslatableComponent("command.orimod.setspirit.failure_server_forces_state"));
			throw SERVER_FORCES_STATE.create();
		}
		
		if (src.getEntity() instanceof Player sender) {
			if (sender.equals(player)) {
				if (SpiritPermissions.getPermissions().get(sender).canChange()) {
					SpiritIdentifier.setSpiritNetworked(sender, isSpirit);
					src.sendSuccess(new TranslatableComponent("command.orimod.setspirit.success", sender.getName(), String.valueOf(isSpirit)), true);
				} else {
					src.sendFailure(new TranslatableComponent("command.orimod.setspirit.failure_insufficient_perms_self"));
					throw INSUFFICIENT_PERMS_SELF.create();
				}
			} else {
				if (sender.hasPermissions(2)) {
					SpiritIdentifier.setSpiritNetworked(player, isSpirit);
					src.sendSuccess(new TranslatableComponent("command.orimod.setspirit.success", player.getName(), String.valueOf(isSpirit)), true);
				} else {
					src.sendFailure(new TranslatableComponent("command.orimod.setspirit.failure_insufficient_perms_others"));
					throw INSUFFICIENT_PERMS.create();
				}
			}
		} else {
			src.sendSuccess(new TranslatableComponent("command.orimod.setspirit.success", player.getName(), String.valueOf(isSpirit)), true);
		}
		return 1;
	}
	
	private static int getSpirit(CommandSourceStack src, Player player) {
		boolean isSpirit = SpiritIdentifier.isSpirit(player);
		src.sendSuccess(new TranslatableComponent("command.orimod.getspirit.status", player.getName(), String.valueOf(isSpirit)), true);
		return isSpirit ? 1 : 0;
	}
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(COMMAND_INSTANCE);
	}
	
}
