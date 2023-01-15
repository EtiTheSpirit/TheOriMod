package etithespirit.orimod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import etithespirit.orimod.common.capabilities.SpiritCapabilities;
import etithespirit.orimod.config.OriModConfigs;
import etithespirit.orimod.networking.player.ReplicateKnownAbilities;
import etithespirit.orimod.server.persistence.SpiritPermissions;
import etithespirit.orimod.spirit.SpiritIdentifier;
import etithespirit.orimod.spirit.abilities.SpiritDashAbility;
import etithespirit.orimod.spirit.abilities.SpiritJumpAbility;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.command.EnumArgument;

import java.util.Optional;

public class SetSpiritCommand {
	
	public static final String NAME = "spirit";
	
	private static final SimpleCommandExceptionType INSUFFICIENT_PERMS_SET_SPIRIT = new SimpleCommandExceptionType(Component.translatable("command.orimod.setspirit.failure.set_spirit_other"));
	private static final SimpleCommandExceptionType INSUFFICIENT_PERMS_SELF_SET_SPIRIT = new SimpleCommandExceptionType(Component.translatable("command.orimod.setspirit.failure.set_spirit_self"));
	private static final SimpleCommandExceptionType SERVER_FORCES_STATE = new SimpleCommandExceptionType(Component.translatable("command.orimod.setspirit.failure.server_forces_state"));
	
	private static final SimpleCommandExceptionType INSUFFICIENT_PERMS_ABILITIES = new SimpleCommandExceptionType(Component.translatable("command.orimod.setspirit.failure.set_abilities_other"));
	private static final SimpleCommandExceptionType INSUFFICIENT_PERMS_SELF_ABILITIES = new SimpleCommandExceptionType(Component.translatable("command.orimod.setspirit.failure.set_abilities_self"));
	
	private static final LiteralArgumentBuilder<CommandSourceStack> COMMAND_INSTANCE = Commands
		.literal(NAME)
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
		)
		.then(Commands.literal("ability")
			.then(Commands.argument("player", EntityArgument.player())
				.then(Commands.literal("walljump")
				    .then(Commands.argument("enabled", BoolArgumentType.bool())
				        .executes(
				        	ctx -> setWallJump(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), BoolArgumentType.getBool(ctx, "enabled"))
				        )
				    )
				)
				.then(Commands.literal("jumptype")
					.then(Commands.argument("jumpType", EnumArgument.enumArgument(SpiritJumpAbility.class))
					    .executes(
					    	ctx -> setAirJumpMode(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("jumpType", SpiritJumpAbility.class))
					    )
					)
				)
				.then(Commands.literal("dashtype")
					.then(Commands.argument("dashType", EnumArgument.enumArgument(SpiritDashAbility.class))
						.executes(
							ctx -> setDashMode(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), ctx.getArgument("dashType", SpiritDashAbility.class))
						)
					)
				)
			    .then(Commands.literal("get")
			        .executes(
			        	ctx -> getSpiritAbilities(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))
			        )
			    )
			)
		);

	private static int getSpiritAbilities(CommandSourceStack src, Player player) {
		Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
		capsCtr.ifPresent(spiritCapabilities -> src.sendSuccess(spiritCapabilities.dumpToComponent(), true));
		return 1;
	}
	
	private static int setWallJump(CommandSourceStack src, Player player, boolean enabled) throws CommandSyntaxException {
		if (src.getEntity() instanceof Player sender) {
			if (sender.equals(player)) {
				if (!sender.hasPermissions(OriModConfigs.CHANGE_ABILITIES_SELF_LEVEL.get())) {
					throw INSUFFICIENT_PERMS_SELF_ABILITIES.create();
				}
			} else {
				if (!sender.hasPermissions(OriModConfigs.CHANGE_ABILITIES_OTHERS_LEVEL.get())) {
					throw INSUFFICIENT_PERMS_ABILITIES.create();
				}
			}
			
			Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
			if (capsCtr.isPresent()) {
				SpiritCapabilities caps = capsCtr.get();
				caps.setCanWallJump(enabled);
				ReplicateKnownAbilities.Server.sendNewWallJump((ServerPlayer)player, caps);
				src.sendSuccess(Component.translatable("command.orimod.setspirit.success.ability", sender.getDisplayName()).append(caps.dumpToComponent()), true);
			}
		}
		return 1;
	}
	
	private static int setAirJumpMode(CommandSourceStack src, Player player, SpiritJumpAbility mode) throws CommandSyntaxException {
		if (src.getEntity() instanceof Player sender) {
			if (sender.equals(player)) {
				if (!sender.hasPermissions(OriModConfigs.CHANGE_ABILITIES_SELF_LEVEL.get())) {
					throw INSUFFICIENT_PERMS_SELF_ABILITIES.create();
				}
			} else {
				if (!sender.hasPermissions(OriModConfigs.CHANGE_ABILITIES_OTHERS_LEVEL.get())) {
					throw INSUFFICIENT_PERMS_ABILITIES.create();
				}
			}
			
			Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
			if (capsCtr.isPresent()) {
				SpiritCapabilities caps = capsCtr.get();
				caps.setAirJumpType(mode);
				ReplicateKnownAbilities.Server.sendNewAirJump((ServerPlayer)player, caps);
				src.sendSuccess(Component.translatable("command.orimod.setspirit.success.ability", sender.getDisplayName()).append(caps.dumpToComponent()), true);
			}
		}
		return 1;
	}
	
	private static int setDashMode(CommandSourceStack src, Player player, SpiritDashAbility mode) throws CommandSyntaxException {
		if (src.getEntity() instanceof Player sender) {
			if (sender.equals(player)) {
				if (!sender.hasPermissions(OriModConfigs.CHANGE_ABILITIES_SELF_LEVEL.get())) {
					throw INSUFFICIENT_PERMS_SELF_ABILITIES.create();
				}
			} else {
				if (!sender.hasPermissions(OriModConfigs.CHANGE_ABILITIES_OTHERS_LEVEL.get())) {
					throw INSUFFICIENT_PERMS_ABILITIES.create();
				}
			}
			
			Optional<SpiritCapabilities> capsCtr = SpiritCapabilities.getCaps(player);
			if (capsCtr.isPresent()) {
				SpiritCapabilities caps = capsCtr.get();
				caps.setDashType(mode);
				ReplicateKnownAbilities.Server.sendNewDash((ServerPlayer)player, caps);
				src.sendSuccess(Component.translatable("command.orimod.setspirit.success.ability", sender.getDisplayName()).append(caps.dumpToComponent()), true);
			}
		}
		return 1;
	}
	
	private static int setSpirit(CommandSourceStack src, Player player, boolean isSpirit) throws CommandSyntaxException {
		if (OriModConfigs.FORCE_STATE.get()) {
			src.sendFailure(Component.translatable("command.orimod.setspirit.failure_server_forces_state"));
			throw SERVER_FORCES_STATE.create();
		}
		
		if (src.getEntity() instanceof Player sender) {
			if (sender.equals(player)) {
				if (sender.hasPermissions(OriModConfigs.CHANGE_MODEL_SELF_LEVEL.get())) {
					SpiritIdentifier.setSpiritNetworked(sender, isSpirit);
					src.sendSuccess(Component.translatable("command.orimod.setspirit.success", sender.getName(), String.valueOf(isSpirit)), true);
				} else {
					src.sendFailure(Component.translatable("command.orimod.setspirit.failure_insufficient_perms_self"));
					throw INSUFFICIENT_PERMS_SELF_SET_SPIRIT.create();
				}
			} else {
				if (sender.hasPermissions(OriModConfigs.CHANGE_MODEL_OTHERS_LEVEL.get())) {
					SpiritIdentifier.setSpiritNetworked(player, isSpirit);
					src.sendSuccess(Component.translatable("command.orimod.setspirit.success", player.getName(), String.valueOf(isSpirit)), true);
				} else {
					src.sendFailure(Component.translatable("command.orimod.setspirit.failure_insufficient_perms_others"));
					throw INSUFFICIENT_PERMS_SET_SPIRIT.create();
				}
			}
		} else {
			src.sendSuccess(Component.translatable("command.orimod.setspirit.success", player.getName(), String.valueOf(isSpirit)), true);
		}
		return 1;
	}
	
	private static int getSpirit(CommandSourceStack src, Player player) {
		boolean isSpirit = SpiritIdentifier.isSpirit(player);
		src.sendSuccess(Component.translatable("command.orimod.getspirit.status", player.getName(), String.valueOf(isSpirit)), true);
		return isSpirit ? 1 : 0;
	}
	
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(COMMAND_INSTANCE);
	}
	
}
