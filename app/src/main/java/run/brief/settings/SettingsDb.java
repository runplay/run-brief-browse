package run.brief.settings;

import run.brief.beans.BriefSettings;
import run.brief.secure.Validator;
import run.brief.util.FileReadTask;
import run.brief.util.FileWriteTask;
import run.brief.util.Files;
import run.brief.util.json.JSONObject;

public final class SettingsDb {
	
	private static final SettingsDb DB = new SettingsDb();
	
	private static final String dbArrayName="settings";
	
	private BriefSettings settings;
	private boolean isLoaded=false;
	
	private static FileWriteTask fwt;
	private static FileReadTask frt;
	
	public static FileWriteTask getFwt() {
		return fwt;
	}

	public static FileReadTask getFrt() {
		return frt;
	}

	private SettingsDb() {
		//Load();
	}
	
	public static BriefSettings getSettings() {
		if(Validator.isValidCaller())
            return DB.settings;
        else
            return null;
	}

	public static void Update(BriefSettings settings) {
		if(settings!=null)
			DB.settings=settings;
	}
	
	public static boolean Save() {
		if(DB.isLoaded) {
		try {
            //Log.e("SAVE", "SETTINGS: " + DB.settings.getBean().toString());
            //Validator.logCaller();
            //BLog.e("SAVE-SET","********************************************!!!!!    -- "+DB.settings.getString(BriefSettings.STRING_STYLE_FONT_FACE));
			fwt=new FileWriteTask(Files.HOME_PATH_APP, Files.FILENAME_GENERAL_SETTINGS, DB.settings.getBean().toString());
			return fwt.WriteSecureToSd();

		} catch(Exception e) {
			//Log.e("SAVE",e.getMessage());
			
		}
		}
		return false;
	}
    public static boolean exists() {
        frt = new FileReadTask(Files.HOME_PATH_APP, Files.FILENAME_GENERAL_SETTINGS);

        return frt.exists();

    }
	public static void init() {
        if(true || Validator.isValidCaller()) {
            if (DB.settings == null) {

                frt = new FileReadTask(Files.HOME_PATH_APP, Files.FILENAME_GENERAL_SETTINGS);
                if (!frt.exists()) {
                    //Log.e("SETTINGS","not exists creating");
                    DB.settings = new BriefSettings();
                    DB.isLoaded = true;
                    Save();
                }
                if (frt.ReadSecureFromSd()) {
                    //Log.e("SETTINGS","--"+frt.getFileContent());
                    if (frt.getFileContent() != null && !frt.getFileContent().isEmpty()) {
                        try {
                            JSONObject db = new JSONObject(frt.getFileContent());
                            if (db != null) {
                                DB.settings = new BriefSettings(db);
                                DB.isLoaded = true;
                                //BLog.e("LOADEDSSS", "load settings ---- - - - " + DB.settings.toString());
                            } else {
                                //BLog.e("Sb.jon().no create","DB IS NULL");
                            }
                        } catch (Exception e) {
                            //if(e.getMessage()!=null)
                            //BLog.e("Sb.init()", e.getMessage());
                        }
                    } else {
                        //Log.e("Sb.init().empty",frt.getStatusMessage());
                        DB.settings = new BriefSettings();
                        DB.isLoaded = true;
                        Save();
                    }
                } else {
                    //BLog.e("Sb.init().no read",frt.getStatusMessage());
                }

            }

        }
	}
}
