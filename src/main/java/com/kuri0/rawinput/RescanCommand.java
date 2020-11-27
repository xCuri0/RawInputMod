package com.kuri0.rawinput;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class RescanCommand extends CommandBase {
	@Override
	public String getName() {
		return "rescan";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "Rescans input devices: /rescan";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		sender.sendMessage(new TextComponentString("Rescanning input devices..."));
		com.kuri0.rawinput.RawInput.mouse = null;
	}
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}
}
