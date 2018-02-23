package run.brief.secure;

import java.io.PrintWriter;
import java.io.StringWriter;

import run.brief.b.State;
import run.brief.util.log.BLog;

public final class Validator {

    public static void calldata() {
        Exception ex = new Exception();
        StackTraceElement[] st = ex.getStackTrace();
        for(int i=st.length-1; i>=0; i--) {

            //BLog.e("ST", st[i].getClassName() + " -- " + st[i].getLineNumber() + " -- " + st[i].getMethodName());
        }
        //String calleeClassName = [ex.getStackTrace().length-1]..getClassName();
        StringWriter stack = new StringWriter();
        ex.printStackTrace(new PrintWriter(stack));
        BLog.e("STACK", stack.toString());
        //return "";
    }
/*
    public static boolean isNativeStart() {
        Exception ex = new Exception();
        if(ex.getStackTrace().length>2) {
            //for(StackTraceElement e: ex.getStackTrace()) {
            //	BLog.e("CC", ""+e.getClassName());
            //}
            StringWriter stack = new StringWriter();
            ex.printStackTrace(new PrintWriter(stack));
            //BLog.e("STACK", stack.toString());
            String[] stacks=stack.toString().split("\n");
            //String callerClassName = ex.getStackTrace()[2].getClassName();
            String calleeClassName = stacks[stacks.length-1]; //ex.getStackTrace()[ex.getStackTrace().length-1]..getClassName();
            //BLog.e("CALLING CLASS", "called: " + calleeClassName + ", contains?: NativeStart");
            if(calleeClassName.contains("NativeStart")) {
                return true;
            }
        }
        //BLog.e("CC", "2-------------------------------------------RETURN FALSE");
        return false;
    }
            public static void logCaller() {
            Exception ex = new Exception();

            if(ex.getStackTrace().length>3) {
                //String calleeClassName = ex.getStackTrace()[1].getClassName();
                //BLog.e("CALLING CLASS", "called: " + ex.getStackTrace()[2].getClassName()+", "+ex.getStackTrace()[3].getClassName() + ", startwith?: " + State.getPackageName());

            }
        }
    */

	public static boolean isValidCaller() {
		Exception ex = new Exception();
        String calleeClassName = "run.brief";
        //BLog.e("CALLING CLASS", "called: " + calleeClassName + ", startwith?: " + State.getPackageName());
		if(ex.getStackTrace().length>2) {

            String fromClass = ex.getStackTrace()[2].getClassName();
			if(fromClass.startsWith(calleeClassName)) {
				return true;
			} else {
                BLog.e("CALLING CLASS", "BAD ----*********************---------  called: " + calleeClassName + ", startwith?: " + State.getPackageName());
            }
		}
		return false;
	}
    public static String callingClass() {
        Exception ex = new Exception();

        if(ex.getStackTrace().length>2) {

            return ex.getStackTrace()[2].getClassName()+"."+ex.getStackTrace()[2].getMethodName()+"() --";

        }
        return "no.class.found";
    }
    /*

    proguard makes this call not work

	public static boolean isCallerLockerManager() {
		Exception ex = new Exception();
		if(ex.getStackTrace().length>2) {

			String calleeClassName = ex.getStackTrace()[1].getClassName();
			if(calleeClassName.startsWith(LockerManager.class.getName())
					|| calleeClassName.startsWith(LockerFragment.class.getName())
					) {
				return true;
			}
		}
		return false;
	}
    */
}
