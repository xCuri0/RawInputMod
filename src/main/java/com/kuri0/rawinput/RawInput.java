package com.kuri0.rawinput;
import net.minecraft.client.Minecraft;
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
    public static final String MODID = "RawInput";
    public static final String VERSION = "1.0";
    
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
								for (int i = 0; i < controllers.length; i++) {
			        			if (controllers[i].getType() == Controller.Type.MOUSE) {
			        				controllers[i].poll();
			        				if (((Mouse)controllers[i]).getX().getPollData() != 0.0 || ((Mouse)controllers[i]).getY().getPollData() != 0.0)
			        					mouse = (Mouse)controllers[i];
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

