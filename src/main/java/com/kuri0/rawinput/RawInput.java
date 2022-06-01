package com.kuri0.rawinput;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import java.lang.reflect.Constructor;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;

@Mod(modid = RawInput.MODID, version = RawInput.VERSION)
public class RawInput
{
    public static final String MODID = "rawinput";
    public static final String VERSION = "1.1.2";

    public static Mouse mouse;
    // Delta for mouse
    public static int dx = 0;
    public static int dy = 0;

    @SuppressWarnings("unchecked")
    private static ControllerEnvironment createDefaultEnvironment() throws ReflectiveOperationException {    // Find constructor (class is package private, so we can't access it directly)
        Constructor<ControllerEnvironment> constructor = (Constructor<ControllerEnvironment>)
                Class.forName("net.java.games.input.DefaultControllerEnvironment").getDeclaredConstructors()[0];

        // Constructor is package private, so we have to deactivate access control checks
        constructor.setAccessible(true);
        // Create object with default constructor
        return constructor.newInstance();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        // Abort mission if OS is not windows - Erymanthus / RayDeeUx
        if (Util.getOSType() != Util.EnumOS.WINDOWS) { MinecraftForge.EVENT_BUS.register(new UnintendedUsageWarnings()); return; }
        ClientCommandHandler.instance.registerCommand(new RescanCommand());
        Minecraft.getMinecraft().mouseHelper = new RawMouseHelper();

        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ControllerEnvironment enviro = null;
                while (true) {
                    if (enviro == null) {
                        try {
                            enviro = createDefaultEnvironment();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    } else if (mouse == null) {
                        try {
                            Controller[] controllers = enviro.getControllers();
                            for (Controller controller : controllers) {
                                try {
                                    if (controller.getType() == Controller.Type.MOUSE) {
                                        controller.poll();
                                        float px = ((Mouse) controller).getX().getPollData();
                                        float py = ((Mouse) controller).getY().getPollData();
                                        float eps = 0.1f;

                                        // check if mouse is moving
                                        if (px < -eps || px > eps || py < -eps || py > eps) {
                                            mouse = (Mouse) controller;
                                            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "[RawInput] Found mouse"));
                                        }
                                    }
                                } catch (Exception e) {
                                    // skip to next
                                    e.printStackTrace();
                                }
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    } else {
                        mouse.poll();
                        if (Minecraft.getMinecraft().currentScreen == null) {
                            dx += (int)mouse.getX().getPollData();
                            dy += (int)mouse.getY().getPollData();
                        }
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
        inputThread.setName("inputThread");
        inputThread.start();
    }
}
