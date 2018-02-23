package run.brief.util;

public class UrlStore {

    private static final String DOMAIN_HOST="ht"+"tp://apps.br"+"ief.ink";
    private static final String DOMAIN_BOOK="ht"+"tp://apps.b"+"rief.ink";

    public static final String DOMAIN_WWW="ht"+"tp://ww"+"w.bri"+"ef.ink";
    private static final String DOMAIN_OPEN="ht"+"tp://static.br"+"ief.ink";


    /*
    private static final String DOMAIN_HOST="ht"+"tp://apps.br"+"ief.ink";
    private static final String DOMAIN_BOOK="ht"+"tp://apps.b"+"rief.ink";

    public static final String DOMAIN_WWW="ht"+"tp://ww"+"w.bri"+"ef.ink";
    private static final String DOMAIN_OPEN="ht"+"tp://static.br"+"ief.ink";




        private static final String DOMAIN_HOST="http://devapp.brief.ink:8091";

        private static final String DOMAIN_BOOK="http://devapp.brief.ink:8091";

        public static final String DOMAIN_WWW="http://www.brief.ink";
        private static final String DOMAIN_OPEN="http://static.brief.ink";

     */



    public static final String URL_FARM_SEND = DOMAIN_HOST+"/app/knock.jsp";


	public static final String USER_AGENT="Brief v1.0";
	public static final String USER_AGENT_PNP="unix/5.1 UPnP/1.0 Brief/1.0";
	
	public static final String MY_IP = DOMAIN_BOOK+"/ip.jsp";


    public static final String URL_HELP = DOMAIN_WWW+"/help/";
    public static final String URL_LEGAL = DOMAIN_WWW+"/legal/";
    public static final String URL_OPEN_SOURCE = DOMAIN_WWW+"/legal/open/";
    public static final String URL_CHECK_INTERNET = DOMAIN_OPEN+"/app/check_internet.json";
	public static final String URL_RSS_CATGORIES_MASTER = DOMAIN_OPEN+"/app/def_news_cats.json";
	public static final String URL_RSS_FEEDS_MASTER = DOMAIN_OPEN+"/app/def_news_feeds.json";
	
	public static final String ERROR_REPORT = DOMAIN_HOST+"/gen/errors.jsp";

    private class ModeLive extends ModeClass {

    }
    private static class ModeClass {

    }
    private static class ModeDev extends ModeClass {

    }

	//public static final String NEW_APP_REGISTER = DOMAIN+"/gen/register.jsp";
}
