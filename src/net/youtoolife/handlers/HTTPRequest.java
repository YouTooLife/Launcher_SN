package net.youtoolife.handlers;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class HTTPRequest {
	
	private String readStreamToString(InputStream in, String encoding) throws IOException {
		StringBuffer b = new StringBuffer();
		InputStreamReader r = new InputStreamReader(in, encoding);
		int c;
		while ((c = r.read()) != -1) {
			b.append((char)c);
		}
		return b.toString();
	}
	
	public String getPost(String url, String[] key, String[] value,
            String coding) throws MalformedURLException, IOException{
        URL ur = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) ur.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        /*conn.setRequestProperty("accept-charset", coding);
        conn.setRequestProperty("class", "application");
        conn.setRequestProperty("Accept", "text/html");
        conn.setRequestProperty("Accept-Language", "en-US");
        conn.setRequestProperty ("User-agent", "Mozilla/4.76 (Java"
                    + "; U;"+System.getProperty("os.name")
                    + " "+System.getProperty("os.arch")
                    + " "+System.getProperty("os.version")
                    + "; ru; "+System.getProperty("java.vendor")
                    + " "+System.getProperty("java.version")
                    + ")");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        conn.setRequestProperty("Pragma", "no-cache");*/
        
        String data="";
        for(int j=0; j<key.length;j++){
            data+=data.equals("")
                    ? URLEncoder.encode(key[j],coding)+"="+URLEncoder.encode(value[j],coding)
                    : "&"+URLEncoder.encode(key[j],coding)+"="+URLEncoder.encode(value[j],coding);
        }
        
        PrintWriter writer = new PrintWriter(conn.getOutputStream());
        writer.print(data);
        writer.close();
        //System.out.println(conn.getResponseMessage());
        //System.out.println(conn.getURL());       
        /*
        StringBuffer result = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), coding));
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        reader.close();
        conn.disconnect();
        return result.toString();*/
        return readStreamToString(conn.getInputStream(), "UTF-8");
    }

}
