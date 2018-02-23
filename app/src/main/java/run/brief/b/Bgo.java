package run.brief.b;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import run.brief.browse.R;
import run.brief.search.SearchFragment;
import run.brief.search.ShortcutSearchFragment;
import run.brief.settings.SettingsHomeTabbedFragment;
import run.brief.util.Files;
import run.brief.util.explore.FileExploreFragment;
import run.brief.util.explore.FileItem;
import run.brief.util.explore.FilesArchiveFragment;
import run.brief.util.explore.FilesDeleteFragment;
import run.brief.util.explore.FolderChooseFragment;
import run.brief.util.explore.ImagesSliderFragment;
import run.brief.util.explore.TextFileFragment;
import run.brief.util.explore.ZipExploreFragment;
import run.brief.util.explore.fm.FileManagerDisk;
import run.brief.util.explore.fm.FileManagerList;
import run.brief.util.json.JSONArray;
import run.brief.util.log.BLog;

public final class Bgo {

    private static Activity useActivity;

    private static void setUseActivity(Activity activity) {
        useActivity=activity;
    }

    public static void openFile(Activity activity, FileManagerDisk fm, File f) {
        if(f!=null) {

                if(Files.isImage(f.getName())) {

					//boolean hitfile=false;
					int usepos=0;
					List<FileItem> useitems = new ArrayList<FileItem>();

					for(int i=0; i<fm.getDirectory(activity).size(); i++) {
						File testFile =fm.getDirectoryItem(i);
						if(!Files.isImage(Files.removeBriefFileExtension(testFile.getName()))) {

						} else {
							useitems.add(fm.getDirectoryItem(i));

						}
						if(f.getName().equals(testFile.getName())) {

							usepos=useitems.size()-1;
						}
					}


					FileManagerList fml = new FileManagerList(useitems);
					fml.setStartAtPosition(usepos);
					State.addCachedFileManager(fml);

					Bgo.openFragmentBackStack(activity,new ImagesSliderFragment());

                } else if(State.getFileExploreState()==State.FILE_EXPLORE_STATE_STANDALONE) {
                    //openOptions(f.getAbsolutePath());
                    Device.openAndroidFile(activity, f);
                } else {

                    JSONArray jarr = new JSONArray();
                    jarr.put(f.getAbsolutePath());
                    //State.clearStateObjects(State.getPreviousSection());
                    Log.e("FEF", "back to: " + State.getPreviousSection() + " -- with--" + jarr.toString());
                    State.addToState(State.SECTION_FILE_EXPLORE,new StateObject(StateObject.STRING_FILE_PATH,jarr.toString()));
                    Bgo.goPreviousFragment(activity);
                }

        }
    }
 	public static void openCurrentState(Activity activity) {
		Class<? extends BFragment> fragment = null;

		BLog.e("STATE", "openCurrentState: " + State.getCurrentSection() + ", size: " + State.getSectionsSize());
		switch (State.getCurrentSection()) {
		//case State.SECTION_TWITTER:
		//	fragment = new TwitterHomeFragment();
		//	break;
			case State.SECTION_FILE_EXPLORE:
				fragment = FileExploreFragment.class;
				break;
			case State.SECTION_FILE_EXPLORE_DELETE:
				fragment = FilesDeleteFragment.class;
				break;
			case State.SECTION_FILE_CREATE_ARCHIVE:
				fragment = FilesArchiveFragment.class;
				break;

			case State.SECTION_FILE_EXPLORE_ARCHIVE:
				fragment = ZipExploreFragment.class;
				break;
			case State.SECTION_SEARCH:
				fragment = SearchFragment.class;
				break;
			case State.SECTION_SEARCH_SHORTCUT:
				fragment = ShortcutSearchFragment.class;
				break;
			case State.SECTION_IMAGES_SLIDER:
				fragment = ImagesSliderFragment.class;
				break;
            case State.SECTION_POP_FOLDER_CHOOSER:
                fragment = FolderChooseFragment.class;
                break;
			case State.SECTION_SETTINGS:
				fragment = SettingsHomeTabbedFragment.class;
				break;
			case State.SECTION_TEXT_FILE_VIEW:
				fragment= TextFileFragment.class;
			default:
				fragment = FileExploreFragment.class;
				break;
		}
		if (fragment != null) {
			Bgo.openFragment(activity, fragment);
		}
	}

    //@SuppressWarnings()
	public static boolean openFragment(Activity activity, Class<? extends Fragment> fragment) {


        //if(!activity.isDestroyed()) {
		setUseActivity(activity);

		Device.hideKeyboard(activity);
		State.sectionsGoBackstack();
            FragmentManager fm = activity.getFragmentManager();

            FragmentTransaction tr = fm.beginTransaction();
			tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.replace(R.id.container, Fragment.instantiate(activity, fragment.getName()));
           // tr.replace(R.id.container, fragment, fragment.getClass().getName());
            //tr.replace(R.id.container, fragment, fragment.getClass().getName());

            tr.commit();
        //}
		//Log.e("BGO", "openfrgament: " + fragment.getClass().getName() + " - backstack: " + fm.getBackStackEntryCount());
		return true;
	}
	/*
	public static boolean openPopFragment(Activity activity, Class<? extends Fragment> fragment) {


		//if(!activity.isDestroyed()) {
		setUseActivity(activity);

		Device.hideKeyboard(activity);
		State.sectionsGoBackstack();
		FragmentManager fm = activity.getFragmentManager();

		FragmentTransaction tr = fm.beginTransaction();
		tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.add(R.id.container, Fragment.instantiate(activity, fragment.getName()));
		// tr.replace(R.id.container, fragment, fragment.getClass().getName());
		//tr.replace(R.id.container, fragment, fragment.getClass().getName());

		tr.commit();
		//}
		//Log.e("BGO", "openfrgament: " + fragment.getClass().getName() + " - backstack: " + fm.getBackStackEntryCount());
		return true;
	}
	*/
	public static boolean openFragmentAnimate(Activity activity, Fragment fragment) {
        setUseActivity(activity);

		Device.hideKeyboard(activity);
		State.sectionsGoBackstack();
		FragmentManager fm = activity.getFragmentManager();
		FragmentTransaction tr = fm.beginTransaction();
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.replace(R.id.container, fragment, fragment.getClass().getName());
		tr.commit();
		return true;
	}

	public static boolean openFragmentBackStackAnimate(Activity activity,Fragment fragment) {
        setUseActivity(activity);
		Device.hideKeyboard(activity);

		FragmentManager fm = activity.getFragmentManager();
		FragmentTransaction tr = fm.beginTransaction();
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		tr.replace(R.id.container, fragment, fragment.getClass().getName());

		tr.commit();
		return true;
	}

	public static boolean openFragmentBackStack(Activity activity,Fragment fragment) {

        setUseActivity(activity);
		Device.hideKeyboard(activity);

		FragmentManager fm = activity.getFragmentManager();
		FragmentTransaction tr = fm.beginTransaction();
        tr.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        
		tr.replace(R.id.container, fragment, fragment.getClass().getName());
		tr.commit();
		return true;
	}

	public static void clearBackStack(Activity activity) {
		FragmentManager fm = activity.getFragmentManager();
		int backStackCount = fm.getBackStackEntryCount();
		for (int i = 0; i < backStackCount; i++) {
			int backStackId = fm.getBackStackEntryAt(i).getId();

			fm.popBackStack(backStackId,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);

		}
		State.sectionsClearBackstack();
	}

	public static void refreshFragment(Activity activity,String fragmentClassName) {
		FragmentManager fm = activity.getFragmentManager();
		try {
			BRefreshable f = (BRefreshable) fm
					.findFragmentByTag(fragmentClassName);
			if (f != null) {
				f.refresh();
			}
		} catch (Exception e) {

		}
	}


    public static void tryRefreshCurrentFragment() {
        if (useActivity != null) {
            refreshCurrentFragment(useActivity);
        } else {
           // BLog.e("TRY","********************** activity=null");
        }
    }
    public static void tryRefreshDataCurrentFragment() {
        if (useActivity != null) {
            refreshDataCurrentFragment(useActivity);
        }
    }
    public static void tryRefreshCurrentIfFragment(Class ifRefreshableClass) {
        if (useActivity != null) {
            refreshCurrentIfFragment(useActivity, ifRefreshableClass);
        }
    }
    public static void refreshDataCurrentIfFragment(Class ifRefreshableClass) {
        if (useActivity != null) {

            final BRefreshable f = getCurrentRefeshableFragment(useActivity);
            //BLog.e("REFC","current: "+f.getClass().getName()+" -- need match : "+ifRefreshableClass.getName());
            if (f != null && f.getClass().getName().equals(ifRefreshableClass.getName())) {
                useActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        f.refreshData();
                    }
                });
            }

        }
    }
	public static void refreshCurrentFragment(Activity activity) {
		if (activity != null) {

			BRefreshable f = getCurrentRefeshableFragment(activity);
			if (f != null) {

				f.refresh();
			}

		}
	}
    public static void refreshDataCurrentFragment(Activity activity) {
        if (activity != null) {

            BRefreshable f = getCurrentRefeshableFragment(activity);
            if (f != null) {
                f.refreshData();
            }

        }
    }
    public static void refreshDataCurrentIfFragment(Activity activity,Class ifRefreshableClass) {
        if (activity != null) {

            BRefreshable f = getCurrentRefeshableFragment(activity);
            if (f != null && f.getClass().getName().equals(ifRefreshableClass.getName())) {
                f.refreshData();
            }

        }
    }
	public static void refreshCurrentIfFragment(Activity activity,Class ifRefreshableClass) {
		if (activity != null) {

			final BRefreshable f = getCurrentRefeshableFragment(activity);
            //BLog.e("REFC","current: "+f.getClass().getName()+" -- need match : "+ifRefreshableClass.getName());
			if (f != null && f.getClass().getName().equals(ifRefreshableClass.getName())) {
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						f.refresh();
					}
				});
			}

		}
	}

	public static BRefreshable getCurrentRefeshableFragment(Activity activity) {
		if (activity != null) {
			FragmentManager fm = activity.getFragmentManager();
			try {

				BRefreshable f = (BRefreshable) fm
						.findFragmentByTag(getFragmentNameBystate(State.getCurrentSection()));
                //BLog.e("REF","---"+getFragmentNameBystate(State.getCurrentSection()));
				if (f != null) {
					return f;
				} else {
                    BRefreshable fr = (BRefreshable) fm
                            .findFragmentById(R.id.container);
                    if (fr != null) {
                        return fr;
                    }
                }
			} catch (Exception e) {
               // BLog.e("REF","ex: "+e.toString());
			}
		}
		return null;
	}
	public static Fragment getCurrentFragment(Activity activity) {
		if (activity != null) {
			FragmentManager fm = activity.getFragmentManager();
			try {

				Fragment f = (Fragment) fm
						.findFragmentByTag(getFragmentNameBystate(State.getCurrentSection()));
				//BLog.e("REF","---"+getFragmentNameBystate(State.getCurrentSection()));
				if (f != null) {
					return f;
				}
			} catch (Exception e) {
				// BLog.e("REF","ex: "+e.toString());
			}
		}
		return null;
	}
    public static void removeFragmentFromFragmentManager(Activity activity, String TAG_FRAGMENT) {
        if (activity != null) {
            Fragment fragment = activity.getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
            if(fragment != null)
                activity.getFragmentManager().beginTransaction().remove(fragment).commit();


        }
    }
	private static String getFragmentNameBystate(int STATE_) {
		String fragname = null;

		switch (STATE_) {

        case State.SECTION_SEARCH:
            fragname = SearchFragment.class.getName();
            break;
		case State.SECTION_SEARCH_SHORTCUT:
			fragname = ShortcutSearchFragment.class.getName();
			break;
		case State.SECTION_IMAGES_SLIDER:
			fragname = ImagesSliderFragment.class.getName();
			break;
		case State.SECTION_FILE_EXPLORE:
			fragname = FileExploreFragment.class.getName();
			break;
		case State.SECTION_FILE_EXPLORE_ARCHIVE:
			fragname = ZipExploreFragment.class.getName();
			break;
		case State.SECTION_FILE_CREATE_ARCHIVE:
			fragname = FilesArchiveFragment.class.getName();
			break;
		case State.SECTION_FILE_EXPLORE_DELETE:
			fragname = FilesDeleteFragment.class.getName();
			break;
        case State.SECTION_POP_FOLDER_CHOOSER:
            fragname = FolderChooseFragment.class.getName();
            break;

            case State.SECTION_TEXT_FILE_VIEW:
                fragname = TextFileFragment.class.getName();
                break;
		case State.SECTION_SETTINGS:
			fragname = SettingsHomeTabbedFragment.class.getName();
			break;


		default:
			fragname = FileExploreFragment.class.getName();
			break;


		}
		//Log.e("ST", "GET getFragmentNameBystate(): "+fragname);
		return fragname;
	}

	public static void goPreviousFragment(Activity activity) {
		Device.hideKeyboard(activity);
//BLog.e("SS", "Go previous sections size: " + State.getSectionsSize());
		if (State.getSectionsSize() == 0) {
			Bgo.clearBackStack(activity);
			Bgo.openFragment(activity, FileExploreFragment.class);
		} else {
			State.sectionsGoBackstack();
			Bgo.openCurrentState(activity);

		}

	}

}
