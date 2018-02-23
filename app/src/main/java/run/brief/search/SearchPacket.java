package run.brief.search;

import run.brief.beans.BJSONBean;
import run.brief.util.json.JSONObject;

/**
 * Created by coops on 06/08/15.
 */
public class SearchPacket extends BJSONBean {

    public static final String INT_TYPE="ty";
    public static final String STRING_TERM="tm";
    public static final String INT_ICON="ic";
    public SearchPacket(JSONObject bjbean) {
        super();
        this.bean=bjbean;
    }
    public SearchPacket(int type, int Rdrawable, String term) {
        super();
        setInt(INT_TYPE, type);
        setInt(INT_ICON,Rdrawable);
        setString(STRING_TERM,term);
    }

}
