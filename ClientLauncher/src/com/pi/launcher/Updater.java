package com.pi.launcher;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.pi.common.PILogger;

public class Updater {
    public static void update(PILogger log) throws IOException {
	log.info("Downloading version information");
	Map<String, Integer> remote_ver = readVersionInfo(new URL(
		ServerConfiguration.libFolder + "version").openStream());
	Map<String, Integer> local_ver = new HashMap<String, Integer>();
	File lcl = new File(Paths.getBinDirectory(), "version");
	if (lcl.exists())
	    local_ver.putAll(readVersionInfo(new FileInputStream(lcl)));
	if (checkKey("natives", remote_ver, local_ver)) {
	    Natives.load(log);
	}
	if (checkKey("jogl", remote_ver, local_ver)) {
	    Binaries.loadBinary(log, Binaries.jogl_jar);
	}
	if (checkKey("gluegen", remote_ver, local_ver)) {
	    Binaries.loadBinary(log, Binaries.gluegen_jar);
	}
	if (checkKey("nativewindow", remote_ver, local_ver)) {
	    Binaries.loadBinary(log, Binaries.nativewindow_jar);
	}
	if (checkKey("client", remote_ver, local_ver)) {
	    Binaries.loadBinary(log, Binaries.client_jar);
	}
	if (!lcl.exists())
	    lcl.createNewFile();
	BufferedWriter writer = new BufferedWriter(new FileWriter(lcl));
	for (String s : remote_ver.keySet()) {
	    writer.write(s + "\t" + remote_ver.get(s));
	    writer.newLine();
	}
	writer.close();
    }

    private static boolean checkKey(String key, Map<String, Integer> remote,
	    Map<String, Integer> local) {
	key = key.toLowerCase();
	return remote.containsKey(key)
		&& (!local.containsKey(key) || local.get(key).intValue() != remote
			.get(key).intValue());
    }

    private static Map<String, Integer> readVersionInfo(InputStream in)
	    throws IOException {
	Map<String, Integer> map = new HashMap<String, Integer>();
	BufferedReader read = new BufferedReader(new InputStreamReader(in));
	while (read.ready()) {
	    String[] data = read.readLine().split("\t");
	    if (data.length == 2) {
		try {
		    int ver = Integer.valueOf(data[1]);
		    map.put(data[0].toLowerCase(), ver);
		} catch (NumberFormatException e) {
		}
	    }
	}
	return map;
    }
}
