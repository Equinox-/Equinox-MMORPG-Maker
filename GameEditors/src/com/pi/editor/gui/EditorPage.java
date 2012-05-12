package com.pi.editor.gui;

import com.pi.graphics.device.Renderable;

public interface EditorPage extends Renderable {
    public void register();

    public void unRegister();
    
    public void resize(int width, int height);
}
