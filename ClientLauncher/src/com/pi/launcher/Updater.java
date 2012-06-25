package com.pi.launcher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.pi.common.debug.PILogger;

/**
 * An updater utility class for updating the natives and binaries.
 * 
 * @author Westin
 * 
 */
public final class Updater {
	/**
	 * Update the binaries and natives, outputting to the provided logger.
	 * 
	 * @param log the logger
	 * @throws IOException if an error occurs
	 */
	public static void update(final PILogger log)
			throws IOException {
		log.info("Downloading version information");
		Map<String, Integer> remoteVersion =
				readVersionInfo(log, new URL(
						ServerConfiguration.LIB_FOLDER
								+ "version").openStream(),
						" on the remote version file");
		Map<String, Integer> localVersion =
				new HashMap<String, Integer>();
		File lcl = new File(Paths.getBinDirectory(), "version");
		if (lcl.exists()) {
			localVersion.putAll(readVersionInfo(log,
					new FileInputStream(lcl),
					" on the local version file"));
		}
		if (checkKey("natives", remoteVersion, localVersion)) {
			loadNatives(log);
		}
		for (int i = 0; i < Math.min(
				ServerConfiguration.BINARY_FILES.length,
				ServerConfiguration.BINARY_KEYS.length); i++) {
			if (checkKey(ServerConfiguration.BINARY_KEYS[i],
					remoteVersion, localVersion)
					|| checkFile(ServerConfiguration.BINARY_FILES[i])) {
				URL binary =
						new URL(
								ServerConfiguration.LIB_FOLDER
										+ ServerConfiguration.BINARY_FILES[i]);
				File binaryFile =
						new File(
								Paths.getBinDirectory(),
								ServerConfiguration.BINARY_FILES[i]);
				log.info("Downloading "
						+ ServerConfiguration.BINARY_FILES[i]);
				download(binary, binaryFile);
			}
		}
		if (!lcl.exists()) {
			lcl.createNewFile();
		}
		BufferedWriter writer =
				new BufferedWriter(new FileWriter(lcl));
		for (String s : remoteVersion.keySet()) {
			writer.write(s + "\t" + remoteVersion.get(s));
			writer.newLine();
		}
		writer.close();
	}

	/**
	 * Checks if a file doesn't exist, and should be downloaded regardless.
	 * 
	 * @param name the binary name.
	 * @return if the file should be force downloaded
	 */
	private static boolean checkFile(final String name) {
		return !new File(Paths.getBinDirectory(), name).exists();
	}

	/**
	 * Compares the keys on both the local and remote maps, to see if the file
	 * should be re-downloaded.
	 * 
	 * @param key the key to compare
	 * @param remote the remote version map
	 * @param local the local version map
	 * @return if the keys aren't equal
	 */
	private static boolean checkKey(final String key,
			final Map<String, Integer> remote,
			final Map<String, Integer> local) {
		String cKey = key.toLowerCase();
		return remote.containsKey(cKey)
				&& (!local.containsKey(cKey) || local.get(cKey)
						.intValue() != remote.get(cKey)
						.intValue());
	}

	/**
	 * Read version information from an input stream.
	 * 
	 * @param in the input stream
	 * @param log the logger
	 * @param suffix the suffix for the logger
	 * @return the version mapping
	 * @throws IOException if there is a problem
	 */
	private static Map<String, Integer> readVersionInfo(
			final PILogger log, final InputStream in,
			final String suffix) throws IOException {
		Map<String, Integer> map =
				new HashMap<String, Integer>();
		BufferedReader read =
				new BufferedReader(new InputStreamReader(in));
		int line = 0;
		while (read.ready()) {
			String[] data = read.readLine().split("\t");
			line++;
			if (data.length == 2) {
				try {
					int ver = Integer.valueOf(data[1]);
					map.put(data[0].toLowerCase(), ver);
				} catch (NumberFormatException e) {
					log.info("Error on line #" + line + suffix);
				}
			}
		}
		return map;
	}

	/**
	 * Downloads the URL's contents to the local File.
	 * 
	 * @param url the source URL
	 * @param dest the destination file
	 * @throws IOException if an error occurs
	 */
	public static void download(final URL url, final File dest)
			throws IOException {
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		File f = new File(dest.getAbsolutePath() + ".part");
		f.createNewFile();
		try {
			in = new BufferedInputStream(url.openStream());
			fout = new FileOutputStream(f);
			byte[] data =
					new byte[ServerConfiguration.DOWNLOAD_CACHE];
			int count;
			while ((count =
					in.read(data, 0,
							ServerConfiguration.DOWNLOAD_CACHE)) != -1) {
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
	 * Get the URL representation of all of the local JAR binaries.
	 * 
	 * @return the URL array.
	 */
	public static URL[] getJarURLs() {
		try {
			URL[] urls =
					new URL[ServerConfiguration.BINARY_FILES.length];
			for (int i = 0; i < urls.length; i++) {
				urls[i] =
						new File(
								Paths.getBinDirectory(),
								ServerConfiguration.BINARY_FILES[i])
								.toURI().toURL();
			}
			return urls;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(0);
			return null;
		}
	}

	/**
	 * Checks if all binaries have been loaded at least once.
	 * 
	 * @return <code>true</code> if all binary files exist, <code>false</code>
	 *         if any don't.
	 */
	public static boolean hasBinaries() {
		for (int i = 0; i < ServerConfiguration.BINARY_FILES.length; i++) {
			if (!new File(Paths.getBinDirectory(),
					ServerConfiguration.BINARY_FILES[i])
					.exists()) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the natives have been loaded at least once.
	 * 
	 * @return <code>true</code> if at least one file exists in the natives
	 *         folder, <code>false</code> if not
	 */
	public static boolean hasNatives() {
		return Paths.getNativesDirectory().isDirectory()
				&& Paths.getNativesDirectory().list().length > 0;
	}

	/**
	 * Gets the jar name for the current system architecture.
	 * 
	 * @return the file name of the natives archive
	 */
	public static String getNativeJar() {
		String arch = System.getProperty("sun.arch.data.model");
		if (arch.equals("32")) {
			arch = "x86";
		} else {
			arch = "amd64";
		}
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

	/**
	 * Loads and unzips the natives.
	 * 
	 * @param log the logger
	 * @throws IOException if there is an error.
	 */
	public static void loadNatives(final PILogger log)
			throws IOException {
		String jarname = getNativeJar();
		File jarfile =
				new File(Paths.getNativesDirectory(), jarname);
		log.info("Downloading " + jarname);
		Updater.download(new URL(ServerConfiguration.LIB_FOLDER
				+ jarname), jarfile);
		JarFile arc = new JarFile(jarfile);
		Enumeration<JarEntry> entries = arc.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			File efile =
					new File(Paths.getNativesDirectory(),
							entry.getName());
			if (!efile.exists()) {
				efile.createNewFile();
			}
			InputStream in =
					new BufferedInputStream(
							arc.getInputStream(entry));
			OutputStream out =
					new BufferedOutputStream(
							new FileOutputStream(efile));
			byte[] buffer =
					new byte[ServerConfiguration.UNZIP_CACHE];
			for (;;) {
				int nBytes = in.read(buffer);
				if (nBytes <= 0) {
					break;
				}
				out.write(buffer, 0, nBytes);
			}
			out.flush();
			out.close();
			in.close();
		}
		jarfile.delete();
		log.info("Extraction of natives complete");
	}

	/**
	 * Overridden constructor to allow instances to not be created.
	 */
	private Updater() {
	}
}
