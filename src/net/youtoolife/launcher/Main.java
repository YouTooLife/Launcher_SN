package net.youtoolife.launcher;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.JFrame;

import net.youtoolife.handlers.Download;
import net.youtoolife.handlers.MD5Hasher;

public class Main {
	
	public static final int INSTALL = 0;
	public static final int MAIN_MENU = 1;
	public static final int UPDATE = 2;
	public static final int DOWNLOADING = 3;
	public static final int HASHING = 4;
	
	public static Integer state = HASHING;
	
	public static String server = "https://github.com/YouTooLife/Supernova/raw/master/desktop/";
	public static String dir = System.getProperty("user.home")+"/YouTooLife/Supernova/";
	public static ArrayList<String> files;
	
	public static Download down = null;
	public static MD5Hasher md5 = null;
	public static String platform = null;
	
	//public static Thread thread;
	
	public static int width = 631, height = 354;
	
	private static void updateHash() {
		String myJarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String dirPath = new File(myJarPath).getParent();
		dir = dirPath+"/";
		MD5Hasher md5 = new MD5Hasher(dir);
		
		try {
			md5.createHash("directories.lst", "HASH.lst");
			String strOS = "win32>win64>linux32>linux64>mac";
			String[] listOS = strOS.split(">");
			for (String os:listOS)
				md5.createHash(os, os+"HASH.lst");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void runThread(Runnable object, boolean join) {
		try {
			Thread thread = new Thread(object);
			thread.start();
			if (join) {
				if (thread.isAlive()) {
					thread.join();
				}
			}
		}
		catch (Exception ex) {
			System.out.println("___Error__"+ex.getMessage());
		}
	}

	public static void main(String[] args) {

		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("hash")) {
				updateHash();
				System.exit(0);
			}
		}

		boolean is64Bit = System.getProperty("os.arch").equals("amd64") || System.getProperty("os.arch").equals("x86_64");
		if (System.getProperty("os.name").contains("Windows")) platform = "win"+(is64Bit ? "64":"32");
		if (System.getProperty("os.name").contains("Linux")) platform = "linux"+(is64Bit ? "64":"32");
		if (System.getProperty("os.name").contains("Mac")) platform = "mac";
		
		JFrame frame = new JFrame("SUPERNOVA");
		frame.setSize(width, height);
		
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        GraphicsEnvironment genv = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = genv.getDefaultScreenDevice();
		java.awt.DisplayMode mode = device.getDisplayMode();
        frame.setLocation(mode.getWidth()/2-width/2,mode.getHeight()/2-height/2);
        
        frame.add(new Form());
        frame.setVisible(true);

			try {
				state = HASHING;
				System.out.println("Download cash...");
				files = new ArrayList<String>();
				down = new Download(server,dir);
				files.add("HASH.lst");
				files.add(platform+"HASH.lst");
				down.download(files);
				runThread(down, true);
			    files.clear();
			    
			    System.out.println("done");
			    
			    state = HASHING;
			    md5 = new MD5Hasher(dir);
			    md5.checkFiles("HASH.lst");
			    runThread(md5, true);
			    files.addAll(md5.result);
			    md5.checkFiles(platform+"HASH.lst");
			    runThread(md5, true);
			    files.addAll(md5.result);

			    System.out.println("CASH done");
			        
				if (files.size() > 0) {
					state = UPDATE;
				}
				else {
					state = MAIN_MENU;
				}
				File file;
				file = new File(dir+"Supernova.jar");
				if (!file.exists()) {
					state = INSTALL;
					System.out.println("InstallState");
				}
				System.out.println("STATE: "+state);
			} catch (FileNotFoundException e) {
				if (state != INSTALL)
				state = MAIN_MENU;
				else
				System.exit(1);
				System.out.println(e.getMessage());
			}
		
	}

}
