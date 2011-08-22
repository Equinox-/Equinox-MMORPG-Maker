package com.pi.launcher;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.pi.common.PILogger;

public class Natives {
    public static String getNativeJar() {
	String arch = System.getProperty("sun.arch.data.model");
	if (arch.equals("32"))
	    arch = "x86";
	else
	    arch = "amd64";
	switch (Paths.CURRENT_OS) {
	case LINUX:
	    return "natives-linux-" + arch + ".jar";
	case MAC:
	    return "natives-mac-universal.jar";
	case WINDOWS:
	    return "natives-windows-" + arch + ".jar";
	default:
	    return null;
	}
    }

    public static void load(PILogger log) throws IOException {
	String jarname = getNativeJar();
	File jarfile = new File(Paths.getNativesDirectory(), jarname);
	log.info("Downloading " + jarname);
	download(new URL(ServerConfiguration.libFolder + jarname), jarfile);
	JarFile arc = new JarFile(jarfile);
	Enumeration<JarEntry> entries = arc.entries();
	while (entries.hasMoreElements()) {
	    JarEntry entry = entries.nextElement();
	    File efile = new File(Paths.getNativesDirectory(), entry.getName());
	    if (!efile.exists())
		efile.createNewFile();
	    InputStream in = new BufferedInputStream(arc.getInputStream(entry));
	    OutputStream out = new BufferedOutputStream(new FileOutputStream(
		    efile));
	    byte[] buffer = new byte[2048];
	    for (;;) {
		int nBytes = in.read(buffer);
		if (nBytes <= 0)
		    break;
		out.write(buffer, 0, nBytes);
	    }
	    out.flush();
	    out.close();
	    in.close();
	}
	jarfile.delete();
	log.info("Extraction of natives complete");
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
