package net.youtoolife.handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;

import net.youtoolife.launcher.Main;


public class MD5Hasher implements Runnable {
	
	private String dir = null;
	private String[] db;
	
	public ArrayList<String> result = null;
	
	public int filesCount = 0;
	public int index = 0;
	public String state = "";
	
	public MD5Hasher(String dir) {
		this.dir = dir;
	}
	
	public byte[] getMD5HashBytes(String file) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
        FileInputStream fis = new FileInputStream(dir+file);
        
        byte[] dataBytes = new byte[1024];
     
        int nread = 0; 
        while ((nread = fis.read(dataBytes)) != -1) {
          md.update(dataBytes, 0, nread);
        };
        byte[] mdbytes = md.digest();
        fis.close();
        
        return mdbytes;
	}
	
	public String md5HachToString(byte[] mdbytes) {
		StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
	}
	
	public String md5HachToString2(byte[] mdbytes) {
		StringBuffer hexString = new StringBuffer();
    		for (int i=0;i<mdbytes.length;i++) {
    			String hex=Integer.toHexString(0xff & mdbytes[i]);
   	     		if(hex.length()==1) hexString.append('0');
   	     		hexString.append(hex);
    		}
        return hexString.toString();
	}
	
	public String getMD5Hach(String file) {
		try {
			return md5HachToString(getMD5HashBytes(file));
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			return "";
		}
	}
	
	
	public void checkFiles(String fileName) throws FileNotFoundException {
		
	  	Scanner sc = new Scanner(new File(dir+fileName));
		String text = sc.next();
		sc.close();
		
		db = text.split(">");
		
		//for (String t:db)	System.out.println(t);
		
		filesCount = db.length;
		System.out.println(filesCount);

  }
	
	private  ArrayList<String> getSubDir(String path) {
		ArrayList<String> result = new ArrayList<String>();
		File p = new File(dir+path);
		
		System.out.println("__"+dir+path);
		
		String[] files = p.list();
		for (String fileName: files) {	
			System.out.println(path+"/"+fileName);
			File file = new File(dir+path+"/"+fileName);
			if (file.isDirectory())
				result.addAll(getSubDir(path+"/"+fileName));
			else 
				result.add(path+"/"+fileName);
				
		}
		return result;
	}
	
	public void createHash(String in, String out) throws FileNotFoundException {
		
		PrintWriter pw = new PrintWriter(new File(dir+out));
		ArrayList<String> files = new ArrayList<String>();
		
		if (in.contains(".lst")) {
		Scanner sc = new Scanner(new File(dir+in));
		
		String text = sc.nextLine();
		sc.close();
		
		
		System.out.println(text);
		
		String[] paths = text.split(">");
		
		for (String path:paths)
			files.addAll(getSubDir(path));
		}
		else
			files.addAll(getSubDir(in));
		
		filesCount = files.size();
		
		for (int i = 0; i < filesCount; i++) {
			index = i+1;
			String str = files.get(i);
			state = "Writing signature of file '" + str+"'";
			pw.write(getMD5Hach(str)+"<"+str+(index==filesCount?"":">"));
		}
		pw.close();
		
	}

	public void run() {
		result = new ArrayList<String>();
		for (int i = 0; i < filesCount; i++) {
			index = i+1;
			String str = db[i];
			String[] fileInf = str.split("<");
			state = "Verifying signature of file '" + fileInf[1]+"'";
			//System.out.println(fileInf[1]);
			String locFile = fileInf[1];
			if (fileInf[1].contains(".DS_Store"))
				continue;
			if (fileInf[1].contains("__CORE__/") || fileInf[1].contains(Main.platform+"/")) {
				locFile = fileInf[1].substring(fileInf[1].lastIndexOf('/')+1);
				//System.out.println("__core!__"+fileInf[1]);
			}
			if (!(new File(dir+locFile)).exists())
				result.add(fileInf[1]);
			else {
				String hash = getMD5Hach(locFile);
			if (!fileInf[0].equals(hash)) {
				System.out.println("__NO__"+locFile+"__"+hash);
				System.out.println(locFile);
				result.add(fileInf[1]);
			}
			}
		}
		System.out.println("NO:"+result.size());
	}

}
