package com.pi.launcher;

import java.io.IOException;

import com.pi.common.PILogViewer;
import com.pi.common.PILogger;

public class PILauncher {
    public static void main(String[] args) throws IOException {
	PILogViewer lviewer = new PILogViewer("Launcher");
	PILogger lgr = new PILogger(lviewer.logOut);
	Binaries.loadBinaries(lgr);
	Natives.load(lgr);
    }
}
