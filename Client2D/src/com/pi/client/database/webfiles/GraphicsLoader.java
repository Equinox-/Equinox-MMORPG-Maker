package com.pi.client.database.webfiles;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.pi.client.Client;
import com.pi.client.database.Paths;

public class GraphicsLoader {
    public static void load(Client client) {
	try {
	    File filelistNew = new File(Paths.getGraphicsDirectory(),
		    "filelist_new");
	    File filelistCurrent = new File(Paths.getGraphicsDirectory(),
		    "filelist");
	    client.getLog().info("Downloading filelist...");
	    download(new URL(ServerConfiguration.fileList), filelistNew);
	    Map<String, Float> currentVersions = new HashMap<String, Float>();
	    if (filelistCurrent.exists()) {
		BufferedReader reader = new BufferedReader(new FileReader(
			filelistCurrent));
		while (reader.ready()) {
		    String[] line = reader.readLine().split("\t");
		    String name = line[0];
		    float ver;
		    try {
			ver = Float.valueOf(line[1]);
		    } catch (Exception e) {
			continue;
		    }
		    if (Paths.getGraphicsFile(name) != null)
			currentVersions.put(name, ver);
		}
		reader.close();
	    }
	    BufferedReader reader = new BufferedReader(new FileReader(
		    filelistNew));
	    while (reader.ready()) {
		String[] line = reader.readLine().split("\t");
		String name = line[0];
		float ver;
		try {
		    ver = Float.valueOf(line[1]);
		} catch (Exception e) {
		    e.printStackTrace();
		    continue;
		}
		Float cVer = currentVersions.get(name);
		if (cVer == null || cVer.floatValue() < ver) {
		    client.getLog()
			    .info(name + " is outdated.  Upgrading to version "
				    + ver);
		    try {
			File dest = new File(Paths.getGraphicsDirectory(), name);
			if (!dest.exists())
			    dest.createNewFile();
			download(new URL(ServerConfiguration.graphicsFolder
				+ name), dest);
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
	    }
	    reader.close();
	    filelistCurrent.delete();
	    filelistNew.renameTo(filelistCurrent);
	    client.getLog().info("Finished checking graphic versions!");
	} catch (Exception e) {
	    client.getLog().info("Failed to check for graphical updates!");
	}
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
