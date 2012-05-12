package com.pi.graphics.device;

import java.awt.Container;
import java.io.File;

import com.pi.common.debug.PILogger;

public interface DeviceRegistration {

    public PILogger getLog();

    public void fatalError(String s);

    public Container getContainer();

    public ThreadGroup getThreadGroup();

    public File getGraphicsFile(int id);
}
