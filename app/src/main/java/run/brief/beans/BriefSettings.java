package run.brief.beans;

import run.brief.settings.SettingsDb;
import run.brief.util.json.JSONObject;

public final class BriefSettings extends BJSONBean {

	//public static final String SET_="";
	public static final String INT_COUNT_LAUNCH="launched";
	public static final String BOOL_STYLE_DARK="style";
	public static final String LONG_LAST_INDEX_QUICK="indq";
	public static final String LONG_LAST_INDEX_FULL="indf";
	
	public static final String BOOL_USE_EMOTICONS="emo";





	
	public BriefSettings() {
		bean=new JSONObject();
		bean.put(BOOL_USE_EMOTICONS, Boolean.TRUE);
		bean.put(INT_COUNT_LAUNCH, 0);
		bean.put(BOOL_STYLE_DARK, Boolean.TRUE);


	}
	public BriefSettings(JSONObject obj) {

        this.bean=obj;


	}
	public void save() {
	    SettingsDb.Update(this);
	    SettingsDb.Save();
	}


}
