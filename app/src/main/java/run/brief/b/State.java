package run.brief.b;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import run.brief.beans.BriefSettings;
import run.brief.util.Sf;
import run.brief.util.explore.fm.FileManager;
import run.brief.util.log.BLog;

public final class State {
	private static final State S = new State();
	
	public static final int SECTION_BRIEF=0;
	//public static final int SECTION_BRIEF_MESSAGE=1;
	

	public static final int SECTION_SEARCH=15;
	public static final int SECTION_SEARCH_SHORTCUT=803;
	
	public static final int SECTION_FILE_EXPLORE=19;
	public static final int SECTION_FILE_EXPLORE_DELETE=18;
	public static final int SECTION_FILE_EXPLORE_ARCHIVE=17;
	public static final int SECTION_IMAGES_SLIDER=609;

    public static final int SECTION_FILE_CREATE_ARCHIVE=610;

	public static final int SECTION_LOCKER=250;

    public static final int SECTION_SETTINGS=300;
    public static final int SECTION_SETTINGS_DATA =301;
    public static final int SECTION_SETTINGS_ABOUT =302;

    public static final int SECTION_HELP=400;
    public static final int SECTION_LEGAL=500;



	public static final int SECTION_POP_FOLDER_CHOOSER=1101;

	public static final int SECTION_TEXT_FILE_VIEW=1201;


	//public static final int RESTORE_INSTANCE_STATE=1011;
	
	public static final int FILE_EXPLORE_STATE_STANDALONE=0;
	public static final int FILE_EXPLORE_STATE_SELECTFILE=1;
	
	public static final int CONTACT_MODE_VIEW=0;
	public static final int CONTACT_MODE_SELECT_SMS=1;
	public static final int CONTACT_MODE_SELECT_EMAIL=2;
	public static final int CONTACT_MODE_SELECT_TWITTER=3;
	public static final int CONTACT_MODE_SELECT_FACEBOOK=4;
	
	private static String SECTION_SAVE_KEY="state.section";
	private static String SECTION_SAVE_KEY_1="state.section1";
	private static String SECTION_SAVE_KEY_2="state.section2";
    private static String SECTION_SAVE_KEY_3="state.section3";
	private static String CAMERA_LAST_PHOTO="camera.last";
	/*
	
	
	private static String SECTION_SAVE_KEY_3="state.section3";
	*/
	private static int currentScreenRotation;
	
	private int CONTACT_MODE= State.CONTACT_MODE_VIEW;
	private Map<Integer,List<StateObject>> state = new HashMap<Integer,List<StateObject>>();
	private List<String> camera = new ArrayList<String>();
	//private HashMap<String,Object> store = new HashMap<String,Object>();
	
	private BriefSettings settings;



	//private int section;
	private List<Integer> sections=new ArrayList<Integer>();
	//private int psection;


	private int FILE_EXPLORE_STATE_;

	private HashMap<String,Integer> activefolderstore=new HashMap<String,Integer>();

	public static void setFolderPosition(String absPath, int listPosition) {
		S.activefolderstore.put(absPath,listPosition);
	}
	public static int getFolderPosition(String absPath) {
		if(S.activefolderstore.get(absPath)==null)
			return 0;
		else
			return S.activefolderstore.get(absPath);
	}
	public static void setStateLastKnownPosition(int STATE_,ListView view) {
		State.addToState(STATE_,new StateObject(StateObject.INT_LAST_POS,view.getFirstVisiblePosition()));
	}
	public static void updateLastKnownPosition(int STATE_,ListView view) {
		int pos=State.getStateObjectInt(STATE_,StateObject.INT_LAST_POS);
		if(pos<view.getCount())
			view.setSelection(pos);
	}

	//private FileManager cachedFileManager;
	private List<FileManager> cachedFileManagers = new ArrayList<FileManager>();

	public static FileManager getCachedFileManager(Class<? extends FileManager> ofType) {
		for(FileManager fm: S.cachedFileManagers) {
			if(fm.getClass().equals(ofType)) {
				//Log.e("TST", "getCachedFileManager: "+ fm.getClass() + " - " + ofType);
				return fm;
			}
		}
		return null;
	}
	public static void popCachedFileManager(Class<? extends FileManager> ofType) {
		for(FileManager fm: S.cachedFileManagers) {
			if(fm.getClass().equals(ofType))
				S.cachedFileManagers.remove(fm);
		}
	}
	public static void addCachedFileManager(FileManager filemanager) {
		S.cachedFileManagers.add(0,filemanager);
		for(FileManager fm: S.cachedFileManagers) {
			if(fm.getClass().equals(filemanager.getClass()) && !fm.equals(filemanager))
				fm.close();
		}
        System.gc();
		//S.cachedFileManager =filemanager;
	}
	/*
	public static void setService(BriefService service) {
		S.service=service;
	}
	public static BriefService getService() {
		return S.service;
	}
    */
	public static void setContactsMode(int CONTACT_MODE_) {
		S.CONTACT_MODE=CONTACT_MODE_;
	}
	public static int getContactsMode() {
		return S.CONTACT_MODE;
	}
	public synchronized static void setSettings(BriefSettings settings) {
		S.settings=settings;
	}
	public static BriefSettings getSettings() {
        return S.settings;
	}
	

	public static void setFileExploreState(int FILE_EXPLORE_STATE_) {
		S.FILE_EXPLORE_STATE_=FILE_EXPLORE_STATE_;
	}
	public static int getFileExploreState() {
		return S.FILE_EXPLORE_STATE_;
	}
	public static boolean setCurrentSection(int SECTION_) {
		if(State.getCurrentSection()!=SECTION_) {
			S.sections.add(Integer.valueOf(SECTION_));
			return true;
			//BLog.e("SECTION","ADDED: "+SECTION_);
		} else {
			return false;
			//BLog.e("SECTION","NO ADD: "+SECTION_);
		}
		
		//S.psection=S.section;
		//S.section=SECTION_;
		//BLog.e("SECTION",""+SECTION_);
	}
	public static boolean replaceCurrentSection(int SECTION_) {
		if(S.sections.size()>0) {
			S.sections.set(S.sections.size()-1, Integer.valueOf(SECTION_));
			return true;
		} else {
			return false;
		}
	}

	public static final String getPackageName() {
		return State.class.getPackage().getName();
	}
	
	public synchronized static boolean sectionsGoBackstack() {
		if(!S.sections.isEmpty())
			S.sections.remove(S.sections.size()-1);
		return true;
		//S.psection=S.section;
		//S.section=SECTION_;
		//BLog.e("SECTION",""+SECTION_);
	}
	public static int getSectionsSize() {
		return S.sections.size();
	}
	public synchronized static boolean sectionsClearBackstack() {

		S.sections=new ArrayList<Integer>();

		return true;
		//S.psection=S.section;
		//S.section=SECTION_;
		//BLog.e("SECTION",""+SECTION_);
	}
	public static int getCurrentSection() {
		if(!S.sections.isEmpty())
			return S.sections.get(S.sections.size() - 1);
		else
			return State.SECTION_BRIEF;
	}
	public static int getPreviousSection() {
		if(S.sections.size()-2>0 && S.sections.size()>1)
			return S.sections.get(S.sections.size()-2);
		else
			return State.SECTION_BRIEF;
	}
    public static void clearStateAllObjects() {
        S.state = new HashMap<Integer,List<StateObject>>();
    }
	public static void clearStateObjects(int SECTION_) {
        S.state.remove(Integer.valueOf(SECTION_));
		S.state.put(Integer.valueOf(SECTION_), new ArrayList<StateObject>());
	}
    public static void clearStateObject(int SECTION_, String STATE_OBJECT_) {
        List<StateObject> objects = S.state.get(Integer.valueOf(SECTION_));
        if(objects!=null && !objects.isEmpty()) {
            for(int i=0; i<objects.size(); i++) {
                if(objects.get(i).getName().equals(STATE_OBJECT_)) {
                    objects.remove(i);
                }
            }
            //S.state.get(Integer.valueOf(SECTION_)).r;
        }
    }
	public static void addToState(int SECTION_, StateObject object) {
		if(S.state.get(Integer.valueOf(SECTION_))==null)
			S.state.put(Integer.valueOf(SECTION_), new ArrayList<StateObject>());
		List<StateObject> cstate = S.state.get(Integer.valueOf(SECTION_));
        for(StateObject sob:cstate) {
            if(sob.getName().equals(object.getName())) {
                cstate.remove(sob);
                break;
            }
            //BLog.e("HAS", sob.getObjectAsString());
        }
		cstate.add(object);
		S.state.put(Integer.valueOf(SECTION_), cstate);
	}
	public static List<StateObject> getState(int SECTION_) {
		return S.state.get(Integer.valueOf(SECTION_));
	}
	

	
	public static void addCameraPhoto(String filepath) {
		if(!S.camera.contains(filepath))
			S.camera.add(filepath);
	}
	public static String getCameraLastPhoto() {
		if(!S.camera.isEmpty())
			return S.camera.get(S.camera.size()-1);
		return null;
	}
	public static void clearCameraHistory() {
		S.camera.clear();
	}
	public static boolean hasCameraHistory() {
		if(S.camera.isEmpty())
			return false;
		else
			return true;
	}
	public static double getStateObjectDouble(int SECTION_, String OBJ_) {
		StateObject s=getStateObject(SECTION_,OBJ_);
		if(s!=null) {
			return s.getObjectAsDouble();
		}
		return 0;
	}
	public static int getStateObjectInt(int SECTION_, String OBJ_) {
		StateObject s=getStateObject(SECTION_,OBJ_);
		if(s!=null) {
			return s.getObjectAsInt();
		}
		return 0;
	}
	public static long getStateObjectLong(int SECTION_, String OBJ_) {
		StateObject s=getStateObject(SECTION_,OBJ_);
		if(s!=null) {
			return s.getObjectAsLong();
		}
		return 0;
	}
	public static String getStateObjectString(int SECTION_, String OBJ_) {
		StateObject s=getStateObject(SECTION_,OBJ_);
		if(s!=null) {
			return s.getObjectAsString();
		}
		return null;
	}
	public static boolean hasStateObjects(int SECTION_) {
		if(S.state!=null && !S.state.isEmpty()) 
			return true;
		return false;
	}
	public static boolean hasStateObject(int SECTION_, String OBJ_) {
		if(S.state.get(Integer.valueOf(SECTION_))!=null) {
			for(StateObject state: S.state.get(Integer.valueOf(SECTION_))) {
				if(state.getName().equals(OBJ_)) {
					return true;
				}
			}
		}
		return false;
	}
	private static StateObject getStateObject(int SECTION_, String OBJ_) {
		//ArrayList<StateObject> states = State.getState();
		if(S.state.get(Integer.valueOf(SECTION_))!=null) {
			for(StateObject state: S.state.get(Integer.valueOf(SECTION_))) {
				if(state.getName().equals(OBJ_)) {
					return state;
				}
			}
		}
		return null;
	}
	
	public static void saveState(Bundle outState) {

		Set<Integer> sections = S.state.keySet();
		if(!sections.isEmpty()) {
			for(Integer section: sections) {
				List<StateObject> objects=S.state.get(section);
				for(StateObject obj: objects) {
					switch(obj.getType()) {
						case StateObject.TYPE_INT:
							outState.putInt(section.toString()+"-"+StateObject.TYPE_INT+"-"+obj.getName(), obj.getObjectAsInt());
							break;
						case StateObject.TYPE_STRING:
                            //BLog.e("SAVE TO STATE: "+obj.getObjectAsString());
							outState.putString(section.toString()+"-"+StateObject.TYPE_STRING+"-"+obj.getName(), obj.getObjectAsString());
							break;
						case StateObject.TYPE_DOUBLE:
							outState.putDouble(section.toString()+"-"+StateObject.TYPE_DOUBLE+"-"+obj.getName(), obj.getObjectAsDouble());
							break;
						case StateObject.TYPE_LONG:
							outState.putLong(section.toString()+"-"+StateObject.TYPE_LONG+"-"+obj.getName(), obj.getObjectAsLong());
							break;
						default: break;
					}
				}
			}
		}
		if(!S.sections.isEmpty()) {
			int size=S.sections.size();
			if(size-2>=0) {
				outState.putInt(SECTION_SAVE_KEY_1, S.sections.get(S.sections.size() - 2).intValue());
				//BLog.e("SAVE", "section 1: " + S.sections.get(S.sections.size() - 2).intValue());
			}
			if(size-3>=0) {
				outState.putInt(SECTION_SAVE_KEY_2, S.sections.get(S.sections.size()-3).intValue());
				//BLog.e("SAVE", "section 2: "+S.sections.get(S.sections.size()-3).intValue());
			}
            if(size-4>=0) {
                outState.putInt(SECTION_SAVE_KEY_3, S.sections.get(S.sections.size()-4).intValue());
                //BLog.e("SAVE", "section 2: "+S.sections.get(S.sections.size()-3).intValue());
            }
			
			BLog.e("SAVE", "section: " + S.sections.get(size - 1).intValue());
			outState.putInt(SECTION_SAVE_KEY, S.sections.get(size-1).intValue());
		}
	}
	public static boolean loadState(Bundle inState) {
		boolean loaded=false;
		if(inState!=null && !inState.isEmpty()) {
			loaded=true;

            int section3 =inState.getInt(SECTION_SAVE_KEY_3);
            if(section3!=0)  {
                //BLog.e("LOAD", "section 2: "+section2);
                S.sections.add(Integer.valueOf(section3));
            }
			int section2 =inState.getInt(SECTION_SAVE_KEY_2);
			if(section2!=0)  {
				//BLog.e("LOAD", "section 2: "+section2);
				S.sections.add(Integer.valueOf(section2));
			}
			int section1 =inState.getInt(SECTION_SAVE_KEY_1);
			if(section1!=0) {
				//BLog.e("LOAD", "section 1: "+section1);
				S.sections.add(Integer.valueOf(section1));
			}
			
			int section =inState.getInt(SECTION_SAVE_KEY);
			S.sections.add(Integer.valueOf(section));
			BLog.e("LOAD", "section: " + section);

			String lastphoto = inState.getString(State.CAMERA_LAST_PHOTO);
			if(lastphoto!=null) {
				State.addCameraPhoto(lastphoto);
			}

			
			Set<String> keys = inState.keySet();
			for(String key: keys) {
				String[] pkeys = key.split("-");
				if(pkeys.length==3) {
					int ts = Sf.toInt(pkeys[0]);
					int typekey=Sf.toInt(pkeys[1]);
                    String objname=Sf.notNull(pkeys[2]);
                    //BLog.e("LOAD FROM STATE, key: "+key+" --  "+ts+"-"+typekey+" = "+inState.getString(key));
					if(typekey==StateObject.TYPE_INT) {
					
						addToState(ts,new StateObject(objname,inState.getInt(key)));
					} else if(typekey==StateObject.TYPE_DOUBLE) {
						addToState(ts,new StateObject(objname,inState.getDouble(key)));
					} else if(typekey==StateObject.TYPE_STRING) {

						addToState(ts, new StateObject(objname, inState.getString(key)));
					} else if(typekey==StateObject.TYPE_LONG) {

                        addToState(ts, new StateObject(objname, inState.getLong(key)));
                    }
				}
			}
			
		}
		return loaded;
	}
	

}
