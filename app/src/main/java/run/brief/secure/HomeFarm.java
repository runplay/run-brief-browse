package run.brief.secure;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import run.brief.util.Base64;

/**
 * Created by coops on 25/07/15.
 */
public class HomeFarm {
    private static final HomeFarm HOME = new HomeFarm();
    private String pemKey;

    private boolean isFirstTime=false;
    private Context context;

    private static final String APP_PARTNER="GP";



    public static boolean isFirstTime() {
        return HOME.isFirstTime;
    }
    public static void finishedWithFirstTime() {
        HOME.isFirstTime=false;
    }



    public static void init(Context context) {
        HOME.context = context;
        //BLog.e("HPATH","init()");
    }

    public static final String getPemKey() {
        if(HOME.pemKey!=null)
            return HOME.pemKey;
        if(Validator.isValidCaller()) {
            //Holder hold=HOME.new Holder();
            //Log.e("CRATEKEY","start................................................................................");
            HOME.pemKey=getUniqueDeviceId(HOME.context)+"Br13fR0ck5"+getAndroidId(HOME.context);
            //Log.e("CRATEKEY",HOME.pemKey);
            return HOME.pemKey;
        }
        return null;
    }

    private static String getAndroidId(Context context) {
        return android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
    }


    private static String getUniqueDeviceId(Context context) {
        String m_szAndroidID="";
        String m_szWLANMAC="";
        String m_szDevIDShort="";
        String m_szUniqueID = "";


        //2 compute DEVICE ID
        m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits
        //3 android ID - unreliable
        m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        //6 CONCAT THE IDs
        String m_szLongID = m_szDevIDShort + m_szAndroidID+ m_szWLANMAC;
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            //e.printStackTrace();
        }
        byte p_md5Data[]=null;
        if(m==null) {
            p_md5Data= Base64.encodeToByte(m_szLongID.getBytes(), false);
        } else {
            m.update(m_szLongID.getBytes(),0,m_szLongID.length());
            p_md5Data= m.digest();
        }



        for (int i=0;i<p_md5Data.length;i++) {
            int b =  (0xFF & p_md5Data[i]);
            // if it is a single digit, make sure it have 0 in front (proper padding)
            if (b <= 0xF) m_szUniqueID+="0";
            // add number to string
            m_szUniqueID+=Integer.toHexString(b);
        }
        m_szUniqueID = m_szUniqueID.toUpperCase();

        return m_szUniqueID+APP_PARTNER;

    }


}
