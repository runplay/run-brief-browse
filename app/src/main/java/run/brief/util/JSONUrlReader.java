package run.brief.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import run.brief.util.json.JSONObject;

public class JSONUrlReader {
	  private static int URL_TIMEOUT_MILLIS=15000; // 5 second timeout
	  private static HashMap<String,String> cookieStore= new HashMap<String,String>();
	  
	  private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
    public static JSONObject readJsonFromUrlPlainText(String url)  {
        //BLog.e("JSON",url);
        URL serverUrl = null;
        //HttpURLConnection urlConnection = null;
        try {
            serverUrl=new URL(url);
            //urlConnection = (HttpURLConnection) serverUrl.openConnection();
        } catch(Exception e) {
            //BLog.e("JSONpt1", "" + e.getMessage());
        }


        JSONObject json=null;
        //String useCookie = cookieStore.get(url);
        InputStream is=null;

        try {
            is=serverUrl.openStream();
        } catch(IOException e) {
            //BLog.e("JSONpt2",""+e.getMessage());
        }
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //BLog.e("GOTJSON",jsonText+"");
            json= new JSONObject(jsonText);

        } catch(Exception e) {
            //BLog.e("JSONpt3",""+e.getMessage());
        }
        try {
            is.close();
        } catch(Exception e) {
            //BLog.e("JSONpt4",""+e.getMessage());
        }
        return json;
    }
	  public static JSONObject readJsonFromUrl(String url)  {
          //BLog.e("JSON",url);
		  URL serverUrl = null;
          HttpURLConnection urlConnection = null;
          try {
              serverUrl=new URL(url);
              urlConnection = (HttpURLConnection) serverUrl.openConnection();
          } catch(Exception e) {
              //BLog.e("JSON1", "" + e.getMessage());
          }


          JSONObject json=null;
          String useCookie = cookieStore.get(url);
          InputStream is=null;

          if(useCookie!=null) {
              urlConnection.setRequestProperty("Cookie", useCookie);
          }
          try {
              // mock an android header
              urlConnection.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
              //urlConnection.setRequestProperty("Content-Type","application/json");
              urlConnection.setRequestMethod("GET");

              urlConnection.setDoOutput(true);
              urlConnection.setDoInput(true);
              urlConnection.setConnectTimeout(URL_TIMEOUT_MILLIS);
              urlConnection.setReadTimeout(URL_TIMEOUT_MILLIS);
              urlConnection.setInstanceFollowRedirects(true);

          } catch(Exception e) {
              //BLog.e("readJsonFromUrl().error.msg","2:"+e.getMessage());
          }
          try {
              //urlConnection.conn
              urlConnection.connect();
          } catch(Exception e) {
              cookieStore.remove(url);
              //BLog.e("readJsonFromUrl().error.msg","3 - connect(): "+e.getMessage());
          }	
          try {
              is = urlConnection.getInputStream();
          } catch(IOException e) {
              //BLog.e("JSON4",""+e.getMessage());
          }
          try {
              BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            //BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String jsonText = readAll(rd);
            //BLog.e("GOTJSON",jsonText+"");
            json= new JSONObject(jsonText);

          } catch(Exception e) {
              //BLog.e("JSON5",""+e.getMessage());
          }
          try {
              is.close();
          } catch(Exception e) {
              //BLog.e("JSON6",""+e.getMessage());
          }
          return json;
	  }
}