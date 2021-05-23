package com.kuri0.rawinput;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.ClientCommandHandler;
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
    public static final String VERSION = "1.1.1";
    
    public static Mouse mouse;
    // Delta for mouse
    public static int dx = 0;
    public static int dy = 0;
    
    @SuppressWarnings("unchecked")
	private static ControllerEnvironment createDefaultEnvironment() throws ReflectiveOperationException {
        // Find constructor (class is package private, so we can't access it directly)
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
    	ClientCommandHandler.instance.registerCommand(new RescanCommand());
        Minecraft.getMinecraft().mouseHelper = new RawMouseHelper();

        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
            	while (true) {
            		if (mouse == null) {
						try {
							Controller[] controllers;
							controllers = createDefaultEnvironment().getControllers();
							for (Controller controller : controllers) {
								try {
									if (controller.getType() == Controller.Type.MOUSE) {
										controller.poll();
										float px = ((Mouse) controller).getX().getPollData();
										float py = ((Mouse) controller).getY().getPollData();
										float eps = 0.1f;

										// check if mouse is moving
										if ((-eps < px && px < eps) || (-eps < py && py < eps)) {
											mouse = (Mouse) controller;
											Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Found mouse"));
										}
									}
								}
								catch (Exception e) {
									// skip to next
									e.printStackTrace();
								}
							}
						} catch (ReflectiveOperationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
            		} else {
		            	mouse.poll();
		
		                dx += (int)mouse.getX().getPollData();
		               	dy += (int)mouse.getY().getPollData();         		
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

