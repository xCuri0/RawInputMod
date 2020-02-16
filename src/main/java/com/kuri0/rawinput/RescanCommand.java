package com.kuri0.rawinput;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class RescanCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "rescan";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "Rescans input devices: /rescan";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws CommandException {
		sender.addChatMessage(new ChatComponentText("Rescanning input devices..."));
		com.kuri0.rawinput.RawInput.mouse = null;
	}
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
