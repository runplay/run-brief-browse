package run.brief.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import run.brief.browse.R;
import run.brief.util.log.BLog;

public class Files {

    public static final String SDCARD_PATH= Environment.getExternalStorageDirectory().toString();

	public static final String HOME_PATH_FILES= SDCARD_PATH+"/BriefBrowse";
	public static final String HOME_PATH_ZIP_FILES=HOME_PATH_FILES+File.separator+"archived";
    //public static final String HOME_PATH_APP=Environment.getExternalStorageDirectory().toString()+"/Brief";
    public static String HOME_PATH_APP=null;//Environment.getExternalStorageDirectory().toString()+"/Android/data/run.brief";
    //public static String HOME_PATH_APP=getApplicationInfo().dataDir+"/briefdata";
    //public static final String HOME_PATH_LOCKER= Environment.getExternalStorageDirectory().toString()+"/Android/data/r.lock";

    public static void setAppHomePath(Context context) {
        if(HOME_PATH_APP==null && context.getApplicationInfo()!=null) {
            HOME_PATH_APP = context.getApplicationInfo().dataDir + "/briefdata";
        }
		//Log.e("PATH",HOME_PATH_APP);
    }

    public static final String FILENAME_GENERAL_SETTINGS="BSE.dat";

	public static final String FILENAME_SEARCH_HISTORY="SHST.dat";
	public static final String FOLDER_TWITTER_IMAGES="_img_tw";

	public static final String FOLDER_IMAGES="_img";
	public static final String FOLDER_SOUND="_sound";
	public static final String FOLDER_VIDEO="_video";

	public static final String FILE_EXT_JPG="jpg";
	public static final String FILE_EXT_GIF="gif";
	public static final String FILE_EXT_BITMAP="bmp";
	public static final String FILE_EXT_VIDEO_MP4="mp4";
	public static final String FILE_EXT_SOUND_WAV="wav";

	private static final HashMap<String,Integer> IMG_FILES;
	public static final int F_NO=R.drawable.f_no;
	public static final int F_DIR=R.drawable.f_folder;


	public static final int CAT_UNKNOWN=999;
	public static final int CAT_ANY=0;
	public static final int CAT_IMAGE=1;
	public static final int CAT_VIDEO=2;
	public static final int CAT_SOUND=3;
	public static final int CAT_DOCUMENT=4;
	public static final int CAT_SPREADSHEET=5;
	public static final int CAT_POWERPOINT=6;
	public static final int CAT_COMPRESSION=7;
	public static final int CAT_TEXTFILE=8;

	
	static {
		IMG_FILES=new HashMap<String,Integer>();
		IMG_FILES.put(".dir", Integer.valueOf(R.drawable.f_folder));
		IMG_FILES.put(".no", Integer.valueOf(R.drawable.f_no));
        IMG_FILES.put(".tmp", Integer.valueOf(R.drawable.f_tmp));
		
		IMG_FILES.put(".htm", Integer.valueOf(R.drawable.f_htm));
		IMG_FILES.put(".html", Integer.valueOf(R.drawable.f_htm));
        IMG_FILES.put(".xhtml", Integer.valueOf(R.drawable.f_htm));
		IMG_FILES.put(".css", Integer.valueOf(R.drawable.f_css));
		IMG_FILES.put(".js", Integer.valueOf(R.drawable.f_js));

        IMG_FILES.put(".ico", Integer.valueOf(R.drawable.f_ico));
				
		IMG_FILES.put(".txt", Integer.valueOf(R.drawable.f_txt));

        IMG_FILES.put(".tor", Integer.valueOf(R.drawable.f_tor));
        IMG_FILES.put(".torrent", Integer.valueOf(R.drawable.f_tor));
		
		IMG_FILES.put(".pdf", Integer.valueOf(R.drawable.f_pdf));


		IMG_FILES.put(".jpg", Integer.valueOf(R.drawable.f_jpg));
		IMG_FILES.put(".jpeg", Integer.valueOf(R.drawable.f_jpg));
		IMG_FILES.put(".gif", Integer.valueOf(R.drawable.f_gif));
		IMG_FILES.put(".bmp", Integer.valueOf(R.drawable.f_bmp));
		IMG_FILES.put(".png", Integer.valueOf(R.drawable.f_png));
		IMG_FILES.put(".psd", Integer.valueOf(R.drawable.f_psd));
		IMG_FILES.put(".tif", Integer.valueOf(R.drawable.f_tiff));
		IMG_FILES.put(".tiff", Integer.valueOf(R.drawable.f_tiff));
		
		
		IMG_FILES.put(".ai", Integer.valueOf(R.drawable.f_ai));
		IMG_FILES.put(".eps", Integer.valueOf(R.drawable.f_eps));
		IMG_FILES.put(".ps", Integer.valueOf(R.drawable.f_ps));
		IMG_FILES.put(".svg", Integer.valueOf(R.drawable.f_svg));
		
		IMG_FILES.put(".mp3", Integer.valueOf(R.drawable.f_mp3));
		IMG_FILES.put(".wav", Integer.valueOf(R.drawable.f_wav));
		IMG_FILES.put(".m4a", Integer.valueOf(R.drawable.f_m4v));
		IMG_FILES.put(".wma", Integer.valueOf(R.drawable.f_wma));
		
		IMG_FILES.put(".mp4", Integer.valueOf(R.drawable.f_mp4));
		IMG_FILES.put(".3gp", Integer.valueOf(R.drawable.f_3gp));
		IMG_FILES.put(".avi", Integer.valueOf(R.drawable.f_avi));
		IMG_FILES.put(".mov", Integer.valueOf(R.drawable.f_mov));

		IMG_FILES.put(".mpg", Integer.valueOf(R.drawable.f_mpg));
		IMG_FILES.put(".wmv", Integer.valueOf(R.drawable.f_wmv));

		IMG_FILES.put(".apk", Integer.valueOf(R.drawable.f_apk));


		IMG_FILES.put(".3g2", Integer.valueOf(R.drawable.f_3gp));
		
		IMG_FILES.put(".doc", Integer.valueOf(R.drawable.f_doc));
		IMG_FILES.put(".docx", Integer.valueOf(R.drawable.f_docx));
		IMG_FILES.put(".odt", Integer.valueOf(R.drawable.f_odt));
		IMG_FILES.put(".rtf", Integer.valueOf(R.drawable.f_doc));
		IMG_FILES.put(".wps", Integer.valueOf(R.drawable.f_doc));
		
		IMG_FILES.put(".ppt", Integer.valueOf(R.drawable.f_ppt));
		IMG_FILES.put(".odp", Integer.valueOf(R.drawable.f_odp));
        IMG_FILES.put(".odb", Integer.valueOf(R.drawable.f_odb));
        IMG_FILES.put(".odc", Integer.valueOf(R.drawable.f_odc));
        IMG_FILES.put(".odf", Integer.valueOf(R.drawable.f_odf));
        IMG_FILES.put(".odg", Integer.valueOf(R.drawable.f_odg));
        IMG_FILES.put(".odi", Integer.valueOf(R.drawable.f_odi));
        IMG_FILES.put(".ods", Integer.valueOf(R.drawable.f_ods));
        IMG_FILES.put(".odx", Integer.valueOf(R.drawable.f_odx));
		
		IMG_FILES.put(".xls", Integer.valueOf(R.drawable.f_xls));
		IMG_FILES.put(".xlsx", Integer.valueOf(R.drawable.f_xls));
		IMG_FILES.put(".ods", Integer.valueOf(R.drawable.f_xls));
		
		IMG_FILES.put(".zip", Integer.valueOf(R.drawable.f_zip));
		IMG_FILES.put(".7z", Integer.valueOf(R.drawable.f_7z));
		IMG_FILES.put(".gz", Integer.valueOf(R.drawable.f_gzip));
		IMG_FILES.put(".rar", Integer.valueOf(R.drawable.f_rar));
		//IMG_FILES.put(".tar.gz",Integer.valueOf(R.drawable.f_zip));
        IMG_FILES.put(".sql", Integer.valueOf(R.drawable.f_sql));

        IMG_FILES.put(".class", Integer.valueOf(R.drawable.f_class));
        IMG_FILES.put(".jar", Integer.valueOf(R.drawable.f_jar));
        IMG_FILES.put(".jsp", Integer.valueOf(R.drawable.f_jsp));

		IMG_FILES.put(".rpm", Integer.valueOf(R.drawable.f_rar));
		
	}

	public static String getSDCardFilePath() {
		return Environment.getExternalStorageDirectory().toString();
	}

    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }

    public static String removeBriefFileExtension(String filename) {
        return filename.replaceAll(".brf","");
    }
	public static Drawable getFileIcon(Context context, String fileOrFilePath) {
		if(fileOrFilePath!=null) {
            fileOrFilePath=removeBriefFileExtension(fileOrFilePath);
			int extp=fileOrFilePath.lastIndexOf(".");
			if(extp>0) {
				String ext=fileOrFilePath.substring(extp);
				//BLog.e("GEEXT", ext);
				Integer Rval = IMG_FILES.get(ext.toLowerCase(Locale.getDefault()));
				if(Rval!=null) {
					Drawable d=context.getResources().getDrawable(Rval.intValue());
					if(d!=null)
						return d;
				}
			} else {
				return context.getResources().getDrawable(F_DIR);
			}
		}
		return context.getResources().getDrawable(F_NO);
	}
	public static int getFileRIcon(String fileOrFilePath) {
		if(fileOrFilePath!=null && fileOrFilePath.length()>4) {
			fileOrFilePath=removeBriefFileExtension(fileOrFilePath);
			int extp=fileOrFilePath.lastIndexOf(".");
			if(extp>0) {
				String ext=fileOrFilePath.substring(extp).toLowerCase();

				Integer Rval = IMG_FILES.get(ext);
				if(Rval!=null) 
					return Rval.intValue();
			} else {
				return F_DIR;
			}
		}
		return F_NO;
	}
	public static int getFoldersRIcon(final int InderFileCategory) {
		int cat=R.drawable.f_folder;
		switch (InderFileCategory) {
			case Files.CAT_IMAGE:
				cat=R.drawable.f_folder_pics;
				break;
			case Files.CAT_DOCUMENT:
				cat=R.drawable.f_folder_docs;
				break;
			case Files.CAT_SOUND:
				cat=R.drawable.f_folder_music;
				break;
			case Files.CAT_VIDEO:
				cat=R.drawable.f_folder_videos;
				break;
			case Files.CAT_TEXTFILE:
				cat=R.drawable.f_folder_txt;
				break;
		}
		return cat;
	}
	public static String getExtension(String fileOrFilePath) {
		if(fileOrFilePath!=null && fileOrFilePath.length()>2) {
			int extp=fileOrFilePath.lastIndexOf(".");
			if(extp>0) {
				return fileOrFilePath.substring(extp);
			}
		}
		return null;
	}
	public static String getFilenameLessExtension(String fileOrFilePath) {
		if(fileOrFilePath!=null && fileOrFilePath.length()>2) {
			int extp=fileOrFilePath.lastIndexOf(".");
			if(extp>0) {
				return fileOrFilePath.substring(0,extp);
			}
		}
		return null;
	}
	public static boolean isVideo(String filename) {
		filename=filename.toLowerCase();
		if(filename.endsWith(".mp4")
				|| filename.endsWith(".mov")
				) {
			return true;
		}
		return false;

	}
	public static boolean isImage(String filename) {
		filename=filename.toLowerCase();
		if(filename.endsWith(".jpg")
				|| filename.endsWith(".gif")
				|| filename.endsWith(".png")
				|| filename.endsWith(".jpeg")
				|| filename.endsWith(".tif")
				|| filename.endsWith(".tiff")
				|| filename.endsWith(".bmp")
				) {
			return true;
		}
		return false;			
		
	}
	public static boolean isTextFile(String filename) {
		filename=filename.toLowerCase();
		if(filename.endsWith(".txt")
				|| filename.endsWith(".css")
				|| filename.endsWith(".java")
				|| filename.endsWith(".pl")
				|| filename.endsWith(".js")
				|| filename.endsWith(".jsp")
                || filename.endsWith(".xml")
                || filename.endsWith(".sql")
				|| filename.endsWith(".out")
				) {
			return true;
		}
		return false;

	}


	public static String readTrimRawTextFile(Context ctx, int resId) {
		InputStream inputStream = ctx.getResources().openRawResource(resId);

		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader buffreader = new BufferedReader(inputreader);
		String line;
		StringBuilder text = new StringBuilder();
		try {
			while ((line = buffreader.readLine()) != null) {
				text.append(line.trim());
			}
		}
		catch (IOException e) {
			return null;
		}
		return text.toString();
	}

	public static FileCreateTask newTwitterImageFile() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_TWITTER_IMAGES);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_JPG));

		return f;
	}
	
	public static FileCreateTask newImageFileJpg() {    
		return newImageFileJpg(newFileName(FILE_EXT_JPG));
	}
	public static FileCreateTask newImageFileJpg(String filename) {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_IMAGES);

		FileCreateTask f = new FileCreateTask(dir.toString(),filename);

		return f;
	}
	public static FileCreateTask newImageFileBmp() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_IMAGES);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_BITMAP));

		return f;
	}
	public static FileCreateTask newVideoFile() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_VIDEO);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_VIDEO_MP4));

		return f;
	}
	public static FileCreateTask newSoundFile() {
		StringBuilder dir = new StringBuilder(HOME_PATH_FILES);
		dir.append(File.separator);
		dir.append(FOLDER_SOUND);

		FileCreateTask f = new FileCreateTask(dir.toString(),newFileName(FILE_EXT_SOUND_WAV));

		return f;
	}
	public static String newFileName(String FILE_EXT_) {
		Date d= new Date();
		return d.getTime()+ File.separator+FILE_EXT_;
	}
	public static String getFileNameFromPath(String fullpath) {
		
		if(fullpath.indexOf("/")!=-1) {
			String str[] = fullpath.split("/");
			if(str!=null) {
				return str[str.length-1];
			}
		} else {
			return fullpath;
		}
		
		return "";
	}
	public static String getPathLessFileName(String fullpath) {
		
		if(fullpath.indexOf("/")!=-1) {
			
			return fullpath.substring(0,fullpath.lastIndexOf("/")+1);
			
		} else {
			return fullpath;
		}

	}
	public static String createFileNameFromUrl(String url) {
		url=url.replaceFirst("http://", "");
		String ext = url.substring(url.lastIndexOf("."),url.length());
		
		
		url=url.replaceAll("[^_A-Za-z0-9]", "");
		return url+ext;
	}
    public static String createFilePathFromUrl(String url) {
        StringBuilder dir = new StringBuilder(Files.HOME_PATH_FILES);
        dir.append(File.separator);
        dir.append(Files.FOLDER_IMAGES);
        String path=dir.toString();
        String filename = Files.createFileNameFromUrl(url);


        dir.append(File.separator);
        dir.append(filename);
        return dir.toString();
    }
	public static boolean ensurePath(String path) {
		boolean ok=true;
		File filedir = new File(path);
		if(!filedir.exists())
			ok=filedir.mkdirs();

		return ok;
	}
	public static boolean ensurePathAndFile(String path, String filename) {
		boolean ok=true;
		File filedir = new File(path);
		if(!filedir.exists())
			ok=filedir.mkdirs();
		if(ok) {
			File file = new File(path+ File.separator+filename);
			try {
			if(!file.exists())
				file.createNewFile();
			} catch(IOException e) {
				BLog.add("EnsurePathAndFile", e);
			}
			
			ok=(file.exists() && file.canWrite());

		}
		return ok;
	}
	public static String getAvailableIncrementedFilePath(String requestedFileNameAndPath) {
		File f = new File(requestedFileNameAndPath);
		String path=f.getParentFile().getAbsolutePath();
		Files.ensurePath(path);
		if(!f.exists())
			return requestedFileNameAndPath;
		String newfilename=null;
		String []splits = f.getName().split("\\.");
		String fnle = splits[0];
		String fnler="";
		if(splits.length>1) {
			for(int i=1; i<splits.length;i++)
				fnler+="."+splits[i];
		}
        if(fnle.indexOf("-")!=-1) {
            String subfn = fnle.substring(0,fnle.lastIndexOf("-"));
            String co = fnle.substring(fnle.lastIndexOf("-")+1,fnle.length());
            //BLog.e("splitting name: "+co);
            if(Sf.toInt(co)>0)
                fnle=subfn;
        }
		//String ext = Files.getExtension(f.getName());
		for(int i=1; i<1000; i++) {
			f=new File(path+File.separator+fnle+"-"+i+fnler);
			if(!f.exists()) {
				break;
			}
		}
		//Log.e("FN","available name: "+f.getAbsolutePath());
		return f.getAbsolutePath();
	}

}
