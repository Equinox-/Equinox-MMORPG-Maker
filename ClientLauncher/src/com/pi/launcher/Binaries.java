package com.pi.launcher;

import java.io.*;
import java.net.URL;

import com.pi.common.PILogger;

public class Binaries {
    private final static String jogl_jar = "jogl.all.jar";
    private final static String gluegen_jar = "gluegen-rt.jar";
    private final static String nativewindow_jar = "nativewindow.all.jar";
    private final static String client_jar = "EquinoxClient.jar";

    public static void loadBinaries(PILogger log) throws IOException {
	URL jogl = new URL(ServerConfiguration.libFolder + jogl_jar);
	URL gluegen = new URL(ServerConfiguration.libFolder + gluegen_jar);
	URL nativewindow = new URL(ServerConfiguration.libFolder
		+ nativewindow_jar);
	URL client = new URL(ServerConfiguration.libFolder + client_jar);
	File jogl_f = new File(Paths.getBinDirectory(), jogl_jar);
	File gluegen_f = new File(Paths.getBinDirectory(), gluegen_jar);
	File nativewindow_f = new File(Paths.getBinDirectory(),
		nativewindow_jar);
	File client_f = new File(Paths.getBinDirectory(), client_jar);
	log.info("Downloading " + jogl_jar);
	download(jogl, jogl_f);
	log.info("Downloading " + gluegen_jar);
	download(gluegen, gluegen_f);
	log.info("Downloading " + nativewindow_jar);
	download(nativewindow, nativewindow_f);
	log.info("Downloading " + client_jar);
	download(client, client_f);
	log.info("Downloaded binaries");
    }

    private static void download(URL url, File dest) throws IOException {
	BufferedInputStream in = null;
	FileOutputStream fout = null;
	File f = new File(dest.getAbsolutePath() + ".part");
	f.createNewFile();
	try {
	    in = new BufferedInputStream(url.openStream());
	    fout = new FileOutputStream(f);
	    byte data[] = new byte[1024];
	    int count;
	    while ((count = in.read(data, 0, 1024)) != -1) {
		fout.write(data, 0, count);
	    }
	} finally {
	    if (in != null)
		in.close();
	    if (fout != null)
		fout.close();
	}
	dest.delete();
	f.renameTo(dest);
    }
}