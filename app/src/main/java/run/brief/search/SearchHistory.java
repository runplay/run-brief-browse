package run.brief.search;

import java.util.ArrayList;
import java.util.List;

import run.brief.util.FileReadTask;
import run.brief.util.FileWriteTask;
import run.brief.util.Files;
import run.brief.util.json.JSONArray;
import run.brief.util.json.JSONException;
import run.brief.util.json.JSONObject;


public final class SearchHistory {
	private static final SearchHistory E = new SearchHistory();
	

	//private static ArrayList<String> errors=new ArrayList();
	
	private static final String DB_ROOT="history";
	private static final int MAX_SIZE=50;
	
	private JSONArray data=new JSONArray();
	private boolean isInitialised=false;
	//private boolean isDebug=true;


	public static int size() {
		return E.data.length();
	}
	public static String get(int index) {
		if(!E.isInitialised)
			init();
		return E.data.optString(index);
	}

	public static void add(SearchPacket packet) {

		if(!E.isInitialised)
			init();
		LimitSizeForAdd();
		try {
			E.data.put(E.data.length(),packet);
		} catch(Exception e) {
			//Log.e("LOG ERROR", "Failed add");
		}

	}

	public static List<SearchPacket> getHistory() {
		List<SearchPacket> hist = new ArrayList<SearchPacket>();
		for(int i=E.data.length()-1;i>=0; i--) {
			try {
				hist.add((SearchPacket) E.data.get(i));
			} catch(Exception e){}
		}
		return hist;
	}

	public static void deleteAllHistory() {
		E.data=new JSONArray();
		Save();
	}

	private static void LimitSizeForAdd() {
		try {
			if(E.data.length()>MAX_SIZE)
				E.data.remove(0);
		} catch(Exception e){}
	}
	public static boolean Save() {

		if(!E.isInitialised)
			init();
		if(E.data.length()!=0) {
			JSONObject db = new JSONObject();
			try {
				db.put(DB_ROOT, E.data);
				FileWriteTask frt=new FileWriteTask(Files.HOME_PATH_APP, Files.FILENAME_SEARCH_HISTORY, db.toString());
				return frt.WriteToSd();
			} catch(Exception e) {}
		}

		return false;
	}
	public static void init() {
		if(E.data==null) {
			FileReadTask frt = new FileReadTask(Files.HOME_PATH_APP, Files.FILENAME_SEARCH_HISTORY);
			if(frt.ReadSecureFromSd()) {
				try {
					JSONObject db = new JSONObject(frt.getFileContent());
					if(db!=null) {
						E.data = db.getJSONArray(DB_ROOT);
						E.isInitialised=true;
					}
				} catch(JSONException e) {}
			}
		}
		if(!E.isInitialised) {
			E.data = new JSONArray();
			E.isInitialised=true;
		}
	}
}
