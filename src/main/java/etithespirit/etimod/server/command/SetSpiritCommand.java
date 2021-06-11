package etithespirit.etimod.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import etithespirit.etimod.info.spirit.SpiritIdentificationType;
import etithespirit.etimod.info.spirit.SpiritIdentifier;
import etithespirit.etimod.networking.morph.ReplicateMorphStatus;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

public class SetSpiritCommand {
	
	@SuppressWarnings("resource")
	private static final LiteralArgumentBuilder<CommandSource> CommandInstance = Commands.literal("togglespirit")
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
		
	public static void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(CommandInstance);
	}
	
}
