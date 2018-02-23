package run.brief.b;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import run.brief.browse.Main;
import run.brief.util.log.BLog;

/**
 * Created by coops on 25/08/15.
 */
public class CatchCrash  implements java.lang.Thread.UncaughtExceptionHandler {
        private final Context myContext;
        public CatchCrash(Context context) {
            myContext = context;
        }
        @Override
        public void uncaughtException(Thread thread, Throwable exception) {
            final StringWriter stackTrace = new StringWriter();
            exception.printStackTrace(new PrintWriter(stackTrace));
            //System.err.println(stackTrace);
            BLog.e(stackTrace.toString());

            Intent intent = new Intent(myContext, Main.class);
            intent.putExtra(Main.INTENT_DATE_STACKTRACE, stackTrace.toString());
            myContext.startActivity(intent);

            try {
                myContext.startActivity(intent);
            } catch (ActivityNotFoundException e) {

            }
            if (myContext instanceof Activity) {
                ((Activity)myContext).finish();
            }
            Process.killProcess(Process.myPid());
            System.exit(10);
        }



}