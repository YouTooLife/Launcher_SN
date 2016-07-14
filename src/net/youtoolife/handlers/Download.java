package net.youtoolife.handlers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;

import net.youtoolife.launcher.Main;


public class Download extends Observable implements Runnable {
	  
	private static final int MAX_BUFFER_SIZE = 1024;
	
	public final int DOWNLOADING = 0;
	public final int COMPLETE = 1;
	public int status; 
	
	public ArrayList<String> files;
	public int size; 
	public int downloaded; 
	public int index = 0;
	public String state = "";
	public String fileName = "";
	  
	  private String dir = System.getProperty("user.home")+"/YouTooLife/Supernova/";
	  private String strUrl = "ytl.96.lt/downloads/dwns.php?user=nop&file=";


	  
	  public Download(String url, String dir) {
		this.dir = dir;
		this.strUrl = url;
		files = new ArrayList<String>();
	  }
	  
	  
	  public void download(String file) {
		files.add(file);
	  }
	  
	  public void download(ArrayList<String> list) {
			files.addAll(list);
			//Thread thread = new Thread(this);
			//thread.run();
	  }
	  
	 /* private String getFileName(URL url) {
	    String fileName = url.getFile();
	    return fileName.substring(fileName.lastIndexOf('/') + 1);
	  }*/
	  
	  public void run() {
		  for (int i = 0; i < files.size(); i++) {
			 index = i+1;
			size = -1;
			downloaded = 0;
			status = DOWNLOADING;
			//System.out.println("Downloading... "+files.get(i));
			
			RandomAccessFile file = null;
			InputStream stream = null;
	
			try {
				URL url = new URL(strUrl+files.get(i));
				HttpURLConnection connection =
						(HttpURLConnection) url.openConnection();
				connection.setRequestProperty("Range",downloaded + "-");
				connection.connect();
	
				if (connection.getResponseCode() / 100 != 2) {
					//error();
					System.out.println("Error");
				}
	
				int contentLength = connection.getContentLength();
				if (contentLength < 1) {
					//error();
					System.out.println("Error2");
				}
	
				if (size == -1) {
					size = contentLength;
					stateChanged();
				}
	  
	  fileName = files.get(i);
	  
	  if (fileName.contains("__CORE__/") || fileName.contains(Main.platform+"/")) {
		  fileName = fileName.substring(fileName.lastIndexOf('/')+1);
	  }
	
	  File f = new File(dir+(fileName.contains("/")?fileName.substring(0,fileName.lastIndexOf('/')):""));
	  
	  if (!f.exists())
		  f.mkdirs();
	  
	  file = new RandomAccessFile(dir+fileName, "rw");
	  file.seek(downloaded);
	
	  stream = connection.getInputStream();
	  while (status == DOWNLOADING) {
	    byte buffer[];
	    if (size - downloaded > MAX_BUFFER_SIZE) {
	      buffer = new byte[MAX_BUFFER_SIZE];
	    } else {
	      buffer = new byte[size - downloaded];
	    }
	
	    int read = stream.read(buffer);
	    if (read == -1)
	      break;
	
	    file.write(buffer, 0, read);
	    downloaded += read;
	    stateChanged();
	  }
	
	  if (index == files.size())
	  if(status == DOWNLOADING) {
	    status = COMPLETE;
	    stateChanged();
	  }
	} catch(Exception e) {
	  //error();
	} finally {
	  if (file != null) 
	    try {
	      file.close();
	    } catch (Exception e) {
	    	// ...
	    }      
	
	  if (stream != null) 
	    try {
	      stream.close();
	    } catch (Exception e) {
	    	// ...
	        }      
		}
	}
	}
	  private double longDoable(long a) {
			double size = a/1000.0f;
			size = new BigDecimal(size).setScale(2, 0).doubleValue();
			return size;
		}
	
	  private  void stateChanged() throws IOException {
		  state = ("File: "+fileName+"\n - "+longDoable(downloaded)+"/"+longDoable(size)+" (KBytes)");
	  }

	
}
