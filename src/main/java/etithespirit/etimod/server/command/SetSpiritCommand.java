package etithespirit.etimod.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import etithespirit.etimod.info.spirit.SpiritData;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

/**
 * The command used to swap out whether or not a player is a spirit.
 *
 * @author Eti
 */
public class SetSpiritCommand {
	
	@SuppressWarnings("resource")
	private static final LiteralArgumentBuilder<CommandSource> CommandInstance = Commands.literal("togglespirit")
	.executes((src) -> {
		boolean isSpirit = SpiritData.isSpirit(src.getSource().getPlayerOrException());
		if (src.getSource().getPlayerOrException().getCommandSenderWorld().isClientSide) {
			ReplicateMorphStatus.askToSetSpiritStatusAsync(!isSpirit);
			src.getSource().sendSuccess(new StringTextComponent("Sent a request to set your spirit status to " + (!isSpirit) + " to the server."), false);
		} else {
			ReplicateMorphStatus.tellEveryonePlayerSpiritStatus(src.getSource().getPlayerOrException(), !isSpirit);
		}
		return 0;
	});
		
	public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(CommandInstance);
	}
	
}
