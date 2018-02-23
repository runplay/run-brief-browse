package run.brief.b;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import run.brief.browse.R;
import run.brief.util.Files;
import run.brief.util.Functions;
import run.brief.util.log.BLog;

//import run.brief.TelephonyInfo;
//import run.brief.sms.SmsFunctions;
//import run.brief.util.ViewManagerText;

public final class Device {
	private static final Device D = new Device();
	
	private boolean isInitialised=false;
	
	private int keyboardHeight;	
	private int previousHeightDiffrence = 0;
	private boolean isKeyBoardVisible;
	

    private String curLocale;
    //private PhoneNumberUtil phoneUtil;



	public static int getDisplayDensity(Activity activity) {
		return Float.valueOf(activity.getResources().getDisplayMetrics().density).intValue();
	}

	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	public static boolean isMediaMounted() {
		return android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
	}

	

	public static boolean isKeyboardVisible() {
		return D.isKeyBoardVisible;
	}
	
	public static void checkKeyboardHeight(final View parentLayout, final PopupWindow popupWindow,final LinearLayout emoticonsCover) {

		parentLayout.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						
						Rect r = new Rect();
						parentLayout.getWindowVisibleDisplayFrame(r);
						
						int screenHeight = parentLayout.getRootView()
								.getHeight();
						int heightDifference = screenHeight - (r.bottom);
						
						if (D.previousHeightDiffrence - heightDifference > 50) {							
							popupWindow.dismiss();
						}
						
						D.previousHeightDiffrence = heightDifference;
						if (heightDifference > 100) {

							D.isKeyBoardVisible = true;
							Device.changeKeyboardHeight(heightDifference, emoticonsCover);

						} else {

							D.isKeyBoardVisible = false;
							
						}

					}
				});

	}

	/**
	 * change height of emoticons keyboard according to height of actual
	 * keyboard
	 * 
	 * @param height
	 *            minimum height by which we can make sure actual keyboard is
	 *            open or not
	 */
	public static void changeKeyboardHeight(int height, LinearLayout emoticonsCover) {

		if (height > 100) {
			D.keyboardHeight = height;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, D.keyboardHeight);
			emoticonsCover.setLayoutParams(params);
		}

	}


	public static void copyToClipboardFlashView(Activity activity, View view, String text) {
		if(view!=null)
			Functions.copyToClipFlashView(activity, view);
		copyToClipboard(activity, text);

	}
	public static void copyToClipboard(Activity activity, String text) {
	    ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
	    ClipData clip = ClipData.newPlainText(B.NAME, text);
	    clipboard.setPrimaryClip(clip);
	    
	}
	


	

	public static String getInternalIP () {
		String address=null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().toString().contains(".")) {
                    	address= inetAddress.getHostAddress().toString();
                    	//BLog.e("INET", address);
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.i("externalip", ex.toString());
        }
        return address;
	}
    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isFlightModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }
    /*
	public static boolean isFlightModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(),
                Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
		//return Settings.System.getInt(context.getContentResolver(),
          //      Settings.System.AIRPLANE_MODE_ON, 0) == 1;
	}
	*/
	private static void setFlightMode(Context context, boolean isOn) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Settings.System.putInt(
                    context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, isOn ? 0 : 1);
        } else {
            Settings.Global.putInt(
                    context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, isOn ? 0 : 1);
        }
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", !isOn);
		context.sendBroadcast(intent);
	}

	
    public static void hideKeyboard(Activity activity) {
     	InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    	if(imm!=null) {
    		View v = activity.findViewById(R.id.container);
    		if(v!=null)
    			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    		else
    			BLog.e("keyboard", "fail hide 1");
    	} else
			BLog.e("keyboard", "fail hide 2");
    }
    public static void setKeyboard(Activity activity, View editTextView, boolean showKeyboard) {
        editTextView.requestFocus();
     	InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    	if(imm!=null) {

    			if(showKeyboard) {
                    imm.showSoftInput(editTextView, 0);
                    //if(getKeyboardHeight(activity)==0) {
                        //GetSetHeight getSetHeight = D.new GetSetHeight();
                        //getSetHeight.setActivity(activity);

                        //getSetHeight.setParentView(parentView);
                        //getSetHeightHandler.removeCallbacks(getSetHeight);
                        //getSetHeightHandler.postDelayed(getSetHeight, 1001);
                    //}

                } else {
                    imm.hideSoftInputFromWindow(editTextView.getWindowToken(), 0);
                }

    	} else
			BLog.e("keuboard", "fail hide 2");
    }

    public static InputMethodInfo getKeyboardInput(Activity activity) {
    	
    	String id = Settings.Secure.getString(
    			   activity.getContentResolver(), 
    			   Settings.Secure.DEFAULT_INPUT_METHOD
    			);
    	
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        List<InputMethodInfo> mInputMethodProperties = imm.getEnabledInputMethodList();

        final int N = mInputMethodProperties.size();

        for (int i = 0; i < N; i++) {

            InputMethodInfo imi = mInputMethodProperties.get(i);

            if (imi.getId().equals(Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.DEFAULT_INPUT_METHOD))) {

                //imi contains the information about the keyboard you are using
                return imi;
            }
        }
        return null;
    }
	public static void init(Context context) {
		if(!D.isInitialised) {
			
			D.isInitialised=true;
		}
	}

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }


	public static void sendFileVia(Activity activity,File file) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );
		activity.startActivity(intent);
	}




	public static void openAndroidFile(Activity activity, File file) {
		//File filesDir = getFilesDir();
		//Scanner input = new Scanner(new File(filesDir, filename));
        //BLog.e("OPENFILE",file.getAbsolutePath());
		if(file!=null) {
			String ext=Files.getExtension(Files.removeBriefFileExtension(file.getAbsolutePath()));
            if(ext!=null) {
                ext = ext.replace(".", "");
                String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());

                if (mime != null) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), mime);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //startActivityForResult(intent, 10);
                    //intent.setType("text/plain");
                    //intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );
					try {
						activity.startActivity(intent);
					} catch (Exception e) {
						Toast.makeText(activity, activity.getResources().getString(R.string.no_file_format), Toast.LENGTH_SHORT).show();
					}
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.no_file_format), Toast.LENGTH_SHORT).show();
                }
            }
		}
	}
	





	public static void vibrate(Context context) {
		vibrate(context, 500);
	}
	public static void vibrate(Context context, int milliSeconds) {
		if(milliSeconds>0 && milliSeconds<2000) {
			Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(milliSeconds);
		}
	}



	public static boolean isInitialised() {
		return D.isInitialised;
	}
	@TargetApi(19)
	public static double getSdSize() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		try {
			double sdAvailSize = (double) stat.getBlockCountLong() * (double) stat.getBlockSizeLong();
//One binary gigabyte equals 1,073,741,824 bytes.
			double gigaAvailable = sdAvailSize / 1073741824;
			return gigaAvailable;
		}catch(java.lang.NoSuchMethodError e) {}
		return 0;
	}
	@TargetApi(19)
	public static double getSdAvailable() {
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		try {
			double sdAvailSize = (double) stat.getAvailableBlocksLong()
					* (double) stat.getBlockSizeLong();
//One binary gigabyte equals 1,073,741,824 bytes.
			double gigaAvailable = sdAvailSize / 1073741824;
			return gigaAvailable;
		} catch(java.lang.NoSuchMethodError e) {}

		return 0;
	}
	@TargetApi(19)
	public static double getHddAvailable() {
		StatFs stat = new StatFs(Environment.getRootDirectory().getPath());
		try {
			double sdAvailSize = (double) stat.getAvailableBlocksLong()
					* (double) stat.getBlockSizeLong();
//One binary gigabyte equals 1,073,741,824 bytes.
			double gigaAvailable = sdAvailSize / 1073741824;
			return gigaAvailable;
		}catch(java.lang.NoSuchMethodError e) {}
		return 0;
	}
	
    private boolean hasContentProvider(Context context, String CONTENT_PROVIDER_) {
    	boolean isSuccess = false;
    	try {
	    	ContentProviderClient pc = context.getContentResolver().acquireContentProviderClient(getContentURI(CONTENT_PROVIDER_));
	    	if (pc != null) {
	    		pc.release();
	    		isSuccess = true;
	    	}
    	} catch(Exception e) {}
    	return isSuccess;
    }
    private static Uri getContentURI(String CONTENT_PROVIDER_) {
    	final Uri CONTENT_URI = Uri.parse(CONTENT_PROVIDER_);
    	return CONTENT_URI;

    }
	



    public static int getKeyboardManualHeight(Activity activity) {
        if(D.keyboardHeight==0)
            return activity.getResources().getDimensionPixelSize(R.dimen.keyboard_height);
        return D.keyboardHeight;
    }

    private static int getKeyboardHeightValue(Activity activity) {
        Rect r = new Rect();
        View rootview = activity.getWindow().getDecorView(); // this = activity
        rootview.getWindowVisibleDisplayFrame(r);
        int remainh=r.bottom;//-r.top;

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        return height-remainh;
    }

    private static Handler getSetHeightHandler = new Handler();
    private static Handler doneFirsttime = new Handler();


        /*
	public static boolean isRingerSilentMode(Context context) {
		if(D.mAudioManager==null)
			D.mAudioManager = (AudioManager) context.getSystemService(Activity.AUDIO_SERVICE);
        int ringermode =D. mAudioManager.getRingerMode();
        if (ringermode == AudioManager.RINGER_MODE_SILENT) {
            return true;
        }
        else
        {
            return false;
        }
	}

	public static void setRingerModeOn(Context context,boolean onOff){
		if(D.mAudioManager==null)
			D.mAudioManager = (AudioManager) context.getSystemService(Activity.AUDIO_SERVICE);
		if(onOff) {
			D.mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		} else {
			D.mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		}
	}

		private static void setRingMode(Context context, int AudioManagerRINGER_MODE_) {
		AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		if(AudioManagerRINGER_MODE_==AudioManager.RINGER_MODE_NORMAL) {
			int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
			audioManager.setRingerMode(AudioManagerRINGER_MODE_);
			audioManager.setStreamVolume(AudioManager.STREAM_RING, maxVolume, AudioManager.FLAG_SHOW_UI + AudioManager.FLAG_PLAY_SOUND);
		} else if(AudioManagerRINGER_MODE_==AudioManager.RINGER_MODE_VIBRATE) {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
		} else {
			audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
		}
		//AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

	}
    */


    


}
