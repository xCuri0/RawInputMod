package com.kuri0.rawinput;
import net.minecraft.util.MouseHelper;

public class RawMouseHelper extends MouseHelper {
	   
    @Override
    public void mouseXYChange()
    {
        this.deltaX = (int)RawInput.dx;
        RawInput.dx = 0;
        this.deltaY = -(int)RawInput.dy;
        RawInput.dy = 0;
    }
}
