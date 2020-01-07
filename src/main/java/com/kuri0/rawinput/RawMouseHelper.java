package com.kuri0.rawinput;
import net.minecraft.util.MouseHelper;

public class RawMouseHelper extends MouseHelper {
	   
    @Override
    public void mouseXYChange()
    {
        this.deltaX = RawInput.dx;
        RawInput.dx = 0;
        this.deltaY = -RawInput.dy;
        RawInput.dy = 0;
    }
}
