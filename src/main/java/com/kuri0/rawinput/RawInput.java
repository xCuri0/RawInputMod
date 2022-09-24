package com.kuri0.rawinput;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.lang.reflect.Constructor;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;

@Mod(modid = RawInput.MODID, version = RawInput.VERSION, acceptedMinecraftVersions = "[1.8.9]")
public class RawInput
{
	public static final String MODID = "rawinput";
	public static final String VERSION = "1.1.2";

	public static Mouse mouse;
	public static Controller[] controllers;
	public static float dx = 0;
	public static float dy = 0;

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		ClientCommandHandler.instance.registerCommand(new RescanCommand());
		Minecraft.getMinecraft().mouseHelper = new RawMouseHelper();
		controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		Thread inputThread = new Thread((() -> {
			while (true) {
				int i = 0;
				while (i < controllers.length && mouse == null) {
					if (controllers[i].getType() == Controller.Type.MOUSE) {
						controllers[i].poll();
						float px = ((Mouse) controllers[i]).getX().getPollData();
						float py = ((Mouse) controllers[i]).getY().getPollData();
						float eps = 0.1f;
						if (Math.abs(px) > eps || Math.abs(py) > eps) {
							mouse = (Mouse) controllers[i];
							try {
								Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Found mouse"));
							} catch (Exception ignored) {}
						}
					}
					i++;
				}
				if (mouse != null) {
					mouse.poll();
					dx += mouse.getX().getPollData();
					dy += mouse.getY().getPollData();
				}

				try {
					Thread.sleep(1L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}));
		inputThread.setName("inputThread");
		inputThread.start();
	}
}
