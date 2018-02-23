package run.brief.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.ContactsContract;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.net.URLEncoder;

import run.brief.browse.Main;
import run.brief.browse.R;
import run.brief.util.bluetooth.SendFile;

//import run.brief.util.voip._CLIENT;
//import run.brief.util.voip._MAIN;

public class BriefActivityManager {

    public static void closeAndRestartBrief(Activity activity) {
        //Intent mStartActivity = new Intent(activity, Main.class);
        //int mPendingIntentId = 123456;
        //PendingIntent mPendingIntent = PendingIntent.getActivity(activity, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        //AlarmManager mgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
        //mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        activity.recreate();//.finish();
        //activity.startActivity(mStartActivity);
        //System.exit(0);
    }

	private static void openAndroidBrowserUrlPost(Context context, String url, String data) {
		Intent i = new Intent();
		// MUST instantiate android browser, otherwise it won't work (it won't find an activity to satisfy intent)
		i.setComponent(new ComponentName("com.android.browser", "com.android.browser.BrowserActivity"));
		i.setAction(Intent.ACTION_VIEW);
		String html = Files.readTrimRawTextFile(context, R.raw.crashreport);

		// Replace params (if any replacement needed)

		// May work without url encoding, but I think is advisable
		// URLEncoder.encode replace space with "+", must replace again with %20
		String dataUri = "data:text/html," + URLEncoder.encode(html).replaceAll("\\+","%20");
		i.setData(Uri.parse(dataUri));
		context.startActivity(i);
	}

	public static void shareFile(Context context, String absolutFilePath) {
		if(absolutFilePath!=null) {
			File f = new File(absolutFilePath);
			if(!f.isDirectory()) {
				String ext=Files.getExtension(Files.removeBriefFileExtension(f.getAbsolutePath()));
				ext = ext.replace(".", "");
				String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext.toLowerCase());

				if (mime != null) {

					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_SEND);
					intent.setDataAndType(Uri.fromFile(f), mime);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					//startActivityForResult(intent, 10);
					//intent.setType("text/plain");
					//intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );
					context.startActivity(intent);
				} else {
					Toast.makeText(context, context.getResources().getString(R.string.no_file_format), Toast.LENGTH_SHORT).show();
				}
			}
		}


	}



	public static void openBriefApp(Context context) {
	    Intent intent = new Intent(context, Main.class);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
	    context.startActivity(intent);
        //context.removeStickyBroadcast(intent);
	}

	public static void openAndroidContactsCreateNew(Activity activity) {
	    //Uri mSelectedContactUri = Contacts.getLookupUri(Sf.toLong(person.getId()), person.getLookupKey());

	    // Creates a new Intent to edit a contact
	    //Intent editIntent = new Intent(Intent.ACTION_);
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra("finishActivityOnSaveCompleted", true);
	    activity.startActivity(intent);
	}

	public static void openBluetoothSendFile(Activity activity) {

	    Intent intent = new Intent(activity, SendFile.class);
	    activity.startActivity(intent);

	}

    /*
	public static void openVoipClient(Activity activity) {

	    Intent intent = new Intent(activity, _CLIENT.class);
	    activity.startActivity(intent);

	}
	public static void openVoipServer(Activity activity) {

	    Intent intent = new Intent(activity, _MAIN.class);
	    activity.startActivity(intent);

	}
	*/
	public static void openPhone(Activity activity) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		activity.startActivity(intent);
	}
	public static void openPhone(Activity activity, String telephoneNumber) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + telephoneNumber));
		activity.startActivity(intent);
	}
	public static void openAndroidSettingsWifi(Activity activity) {
		try {
			activity.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
		} catch(Exception e) {}
	}
	public static void openAndroidBrowserUrl(Activity activity, String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		activity.startActivity(intent);
	}


	public static boolean isInstagramInstalled(Context myContext) {
		  PackageManager myPackageMgr = myContext.getPackageManager();
		  try {
		    myPackageMgr.getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES);
		  } catch (PackageManager.NameNotFoundException e) {
		    return (false);
		  }
		  return (true);
	}
	public static void openInstagram(Context myContext, Uri imageUri, String textCaption) {
		  if (isInstagramInstalled(myContext)) {


				Intent instagram = new Intent(android.content.Intent.ACTION_SEND);
				instagram.setType("image/jpg");
			  instagram.putExtra(Intent.EXTRA_STREAM, imageUri);
			  instagram.putExtra(Intent.EXTRA_TEXT, textCaption);
			  instagram.setPackage("com.instagram.android");
				//instagram.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); //.FLAG_ACTIVITY_NEW_TASK
				myContext.startActivity(instagram);
		  }
	}

	public static void openGmailClient(Context context, String email,String subject) {
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		sendIntent.setType("plain/text");
		sendIntent.setData(Uri.parse(email));
		sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
		if(subject!=null) {
			sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		}
		sendIntent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n\n\n\nSent via Brief.ink");
		sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(sendIntent);
	}
	public static void openGmailClient(Context context, String email) {
		openGmailClient(context,email,null);
	}
	public static boolean isGmailClientInstalled(Context myContext) {
		  PackageManager myPackageMgr = myContext.getPackageManager();
		  try {
		    myPackageMgr.getPackageInfo("com.google.android.gm", PackageManager.GET_ACTIVITIES);
		  } catch (PackageManager.NameNotFoundException e) {
		    return (false);
		  }
		  return (true);
	}
	public static void openNaverLineClientUser(Context context) {
		String appId = "jp.naver.line.android";

		  Intent intent = new Intent();
		  intent.setAction(Intent.ACTION_VIEW);
		  intent.setData(Uri.parse("line://msg/text/yui"));
		  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		  context.startActivity(intent);
	}
	public static void openNaverLineClient(Context context, String phonenumber) {
		
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		sendIntent.setType("plain/text");
		sendIntent.setData(Uri.parse("tel:" + phonenumber));
		sendIntent.setClassName(PACKAGE_NAME, CLASS_NAME);
		sendIntent.putExtra(Intent.EXTRA_TEXT, "");
		sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(sendIntent);
	}
	public static void openNaverLineClientVideo(Context context, File videoFile) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setClassName(PACKAGE_NAME, CLASS_NAME);
		intent.setType("video/mp4");
		intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(videoFile.getPath()));
		//intent.putExtra(Intent.EXTRA_STREAM, 影片路徑);
		intent.putExtra(Intent.EXTRA_TEXT, "Enjoy the Video");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	public static void openNaverLineClient(Context context, File imageFile) {
		/*
		 *
		 * compress file
		 *
		 InputStream iStream = context.getContentResolver().openInputStream(Uri.parse(imageFile.getPath()));
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
		 Bitmap bm = BitmapFactory.decodeStream(iStream);
		 bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
		 os.flush();
		 byte[] w = os.toByteArray();
		 os.close();
		 iStream.close();
		 FileOutputStream out = new FileOutputStream(imageFile.getPath());
		 out.write(w, 0, w.length);
		 out.flush();
		  */
		 Uri uri = Uri.fromFile(new File(imageFile.getPath()));
		  
		 Intent intent = new Intent(Intent.ACTION_SEND);
		  
		 intent.setClassName(PACKAGE_NAME, CLASS_NAME);
		 intent.setType("image/jpeg");
		 intent.putExtra(Intent.EXTRA_STREAM, uri);
		 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		 context.startActivity(intent); 
	}
	public static final String PACKAGE_NAME = "jp.naver.line.android";
	public static final String CLASS_NAME = "jp.naver.line.android.activity.selectchat.SelectChatActivity";

	public static boolean isNaverLineClientInstalled(Context myContext) {
		  PackageManager myPackageMgr = myContext.getPackageManager();
		  try {
		    myPackageMgr.getPackageInfo("jp.naver.line.android", PackageManager.GET_ACTIVITIES);
		    /*
		    FeatureInfo[] feat=myPackageMgr.getSystemAvailableFeatures();
		    if(feat!=null) {
		    	for(FeatureInfo f: feat) {
		    		BLog.e("LINE", ""+f.name);
		    	}
		    }
		    */
		  } catch (PackageManager.NameNotFoundException e) {
		    return (false);
		  }
		  return (true);
	}
	
}
