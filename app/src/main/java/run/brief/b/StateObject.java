package run.brief.b;

public final class StateObject {

	public static final int TYPE_STRING=0;
	public static final int TYPE_INT=1;
	public static final int TYPE_DOUBLE=2;
	public static final int TYPE_LONG=3;
	

	public static final String STRING_FILE_PATH="file_path";


    //public static final String LONG_USE_ACCOUNT_ID_REPLY="sel_account";
	public static final String INT_MODE="edit";
    public static final String INT_VALUE="int";
    public static final String STRING_VALUE="str";
    public static final String STRING_ID="strid";
	public static final String INT_LAST_POS="lapos";
	
	public static final String INT_TEST_SELECT_START="txt_sel_start";
	//public static final String INT_TEST_SELECT_END="txt_sel_end";
	
	public static final String INT_LISTVIEW_FIRST_VIEWABLE="list_sel_start";
    //public static final String INT_LAST_SCROLL="scroll_last";
	
	public static final String STRING_BJSON_OBJECT="json_object";
	public static final String STRING_BJSON_ARRAY="json_array";
	
	//public static final String OBJ_STRING_="sms_resp";
	

	private String name;
	private int type;
	private Object object;
	
	public StateObject(String name,int value) {
		this.name = name;
		setObjectAsInt(value);
	}
	public StateObject(String name,long value) {
		this.name = name;
		setObjectAsLong(value);
	}
	public StateObject(String name,double value) {
		this.name = name;
		setObjectAsDouble(value);
	}

	public StateObject(String name,String value) {
		this.name = name;
		setObjectAsString(value);
	}
	public String getName() {
		return name;
	}
	public String getNameSave() {
		return type+name;
	}

	public int getType() {
		return type;
	}

	private void setObjectAsInt(int value) {
		this.type=TYPE_INT;
		this.object = Integer.valueOf(value);
	}
	private void setObjectAsString(String value) {
		this.type=TYPE_STRING;
		this.object = value;
	}
	private void setObjectAsLong(long value) {
		this.type=TYPE_LONG;
		this.object = Long.valueOf(value);
	}
	private void setObjectAsDouble(double value) {
		this.type=TYPE_DOUBLE;
		this.object = Double.valueOf(value);
	}

	public String getObjectAsString() {
		try {
			return ((String) object);
		} catch(Exception e) {}
		return "";
	}
	public int getObjectAsInt() {
		try {
			return ((Integer) object).intValue();
		} catch(Exception e) {}
		return 0;
	}
	public long getObjectAsLong() {
		try {
			return ((Long) object).longValue();
		} catch(Exception e) {}
		return 0;
	}
	public double getObjectAsDouble() {
		try {
			return ((Double) object).intValue();
		} catch(Exception e) {}
		return 0;
	}
	public Object getObject() {
		try {
			return object;
		} catch(Exception e) {}
		return 0;
	}
}
