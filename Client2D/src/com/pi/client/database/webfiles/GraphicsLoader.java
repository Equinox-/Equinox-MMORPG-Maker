package com.pi.client.database.webfiles;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import com.pi.client.Client;
import com.pi.client.constants.Constants;
import com.pi.client.database.Paths;
import com.pi.common.contants.NetworkConstants;

/**
 * Fetches and updates the graphics from the server.
 * 
 * @author Westin
 * 
 */
public final class GraphicsLoader {
	/**
	 * The web socket connection timeout, in milliseconds.
	 */
	private static final int CONNECT_TIMEOUT = 10000;

	/**
	 * Gets a map of the current graphical versions from a file.
	 * 
	 * @param filelistCurrent the file path
	 * @return a map of the graphics ID to the version.
	 * @throws IOException if there is a file read exception
	 */
	private static Map<Integer, Float> getCurrentVersions(
			final File filelistCurrent) throws IOException {
		HashMap<Integer, Float> currentVersions =
				new HashMap<Integer, Float>();
		if (filelistCurrent.exists()) {
			BufferedReader reader =
					new BufferedReader(new FileReader(
							filelistCurrent));
			while (reader.ready()) {
				String[] line = reader.readLine().split("\t");
				String cleanedName = line[0];
				for (String s : Paths.IMAGE_FILES) {
					cleanedName =
							cleanedName.replace("." + s, "");
				}
				try {
					Integer name = Integer.valueOf(cleanedName);
					float ver;
					ver = Float.valueOf(line[1]);
					if (Paths.getGraphicsFile(name) != null) {
						currentVersions.put(name, ver);
					}
				} catch (Exception e) {
					continue;
				}
			}
			reader.close();
		}
		return currentVersions;
	}

	/**
	 * Updates the graphical files from the graphics server designated in
	 * {@link Constants#GRAPHICS_URL}.
	 * 
	 * @see Constants#GRAPHICS_URL
	 * @see Constants#GRAPHICS_FILELIST
	 * @param client the client instance
	 */
	public static void load(final Client client) {
		if (Constants.GRAPHICS_FILELIST == null) {
			return;
		}
		try {
			File filelistNew =
					new File(Paths.getGraphicsDirectory(),
							"filelist_new");
			File filelistCurrent =
					new File(Paths.getGraphicsDirectory(),
							"filelist");
			client.getLog().info("Downloading filelist...");
			download(new URL(Constants.GRAPHICS_FILELIST),
					filelistNew);
			Map<Integer, Float> currentVersions =
					getCurrentVersions(filelistCurrent);
			BufferedReader reader =
					new BufferedReader(new FileReader(
							filelistNew));
			while (reader.ready()) {
				String[] line = reader.readLine().split("\t");
				String rawName = line[0];
				String cleanedName = rawName;
				for (String s : Paths.IMAGE_FILES) {
					cleanedName =
							cleanedName.replace("." + s, "");
				}
				Integer name = Integer.valueOf(cleanedName);
				float ver;
				try {
					ver = Float.valueOf(line[1]);
				} catch (Exception e) {
					client.getLog().printStackTrace(e);
					continue;
				}
				Float cVer = currentVersions.get(name);
				if (cVer == null || cVer.floatValue() < ver) {
					client.getLog()
							.info(name
									+ " is outdated.  Upgrading to version "
									+ ver);
					try {
						File dest =
								new File(
										Paths.getGraphicsDirectory(),
										rawName);
						if (!dest.exists()) {
							dest.createNewFile();
						}
						download(new URL(Constants.GRAPHICS_URL
								+ rawName), dest);
					} catch (IOException e) {
						client.getLog().printStackTrace(e);
					}
				}
			}
			reader.close();
			filelistCurrent.delete();
			filelistNew.renameTo(filelistCurrent);
			client.getLog().info(
					"Finished checking graphic versions!");
		} catch (NumberFormatException e) {
			client.getLog()
					.severe("There appears to be a number format error server side!  Please report this.");
		} catch (Exception e) {
			client.getLog().printStackTrace(e);
			client.getLog().info(
					"Failed to check for graphical updates!");
		}
	}

	/**
	 * Downloads the contents of a URL to the provided File.
	 * 
	 * @param url the source URL
	 * @param dest the destination File
	 * @throws IOException if there are problems in downloading the file
	 */
	private static void download(final URL url, final File dest)
			throws IOException {
		BufferedInputStream in = null;
		URLConnection conn;
		FileOutputStream fout = null;
		File f = new File(dest.getAbsolutePath() + ".part");
		f.createNewFile();
		try {
			conn = url.openConnection();
			conn.setConnectTimeout(CONNECT_TIMEOUT);
			in = new BufferedInputStream(conn.getInputStream());
			fout = new FileOutputStream(f);
			byte[] data =
					new byte[NetworkConstants.DOWNLOAD_CACHE_SIZE];
			int count;
			while ((count =
					in.read(data, 0,
							NetworkConstants.DOWNLOAD_CACHE_SIZE)) != -1) {
				fout.write(data, 0, count);
			}
		} finally {
			if (in != null) {
				in.close();
			}
			if (fout != null) {
				fout.close();
			}
		}
		dest.delete();
		f.renameTo(dest);
	}

	/**
	 * Overridden to not allow instances of this class to be produced.
	 */
	private GraphicsLoader() {
	}
}
