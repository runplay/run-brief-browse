package run.brief.util.log;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import run.brief.b.B;
import run.brief.secure.Validator;
import run.brief.util.Cal;
import run.brief.util.json.JSONArray;


public final class BLog {
	private static final BLog E = new BLog();
	

	//private static ArrayList<String> errors=new ArrayList();
	
	private static final String DB_ROOT="errors";
	private static final int MAX_SIZE=100;
	
	private JSONArray data=new JSONArray();
	private boolean isInitialised=false;
	private boolean isDebug=true;
	
	public static void SaveIfNeeded() {
		if(E.isDebug)
			Save();
	}
	
	public static void setMode() {
		E.isDebug= B.DEBUG;

	}
	public static int size() {
		return E.data.length();
	}
	public static String get(int index) {
		if(!E.isInitialised)
			init();
		return E.data.optString(index);
	}
	public static void e(String message) {
		if(E.isDebug) {
			String callclass=Validator.callingClass();
			Log.e(Validator.callingClass(), message);
			add(callclass + " - " + message);
		}
	}
	public static void e(String tag, String message) {
		if(E.isDebug) {
            Log.e(tag, message);
            add(tag + " - " + message);
        }
	}
	public static void add(String errorString) {
		if(E.isDebug) {
			if(!E.isInitialised)
				init();
			LimitSizeForAdd();
			try {
				E.data.put(E.data.length(),Cal.getCal().friendlyReadDate()+" - "+errorString);
			} catch(Exception e) {
				//Log.e("LOG ERROR", "Failed add");
			}
		}
	}
	public static void add(String methodString, Exception e) {
		if(E.isDebug) {
            if(!E.isInitialised)
                init();
            LimitSizeForAdd();
            StringBuilder sb=new StringBuilder(Cal.getCal().getDatabaseDate()+" - ");
            sb.append(methodString);
            sb.append("\nexception:\n");
            sb.append(e.getMessage());
            sb.append("\nStack:\n");
            StringWriter stack = new StringWriter();
            e.printStackTrace(new PrintWriter(stack));
            BLog.e(methodString,stack.toString());
            sb.append(stack.toString());
            try {
                E.data.put(E.data.length(),sb.toString());
            } catch(Exception ex) {
                Log.e("LOG ERROR", "Failed add from exception");
            }
		}
	}
	private static void LimitSizeForAdd() {
		try {
			if(E.data.length()>MAX_SIZE)
				E.data.remove(0);
		} catch(Exception e){}
	}
	public static boolean Save() {

		return true;
	}
	public static void init() {

	}
}
