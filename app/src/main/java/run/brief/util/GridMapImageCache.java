package run.brief.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import run.brief.b.bGridViewImage;

/**
 * Created by coops on 21/12/14.
 */
public class GridMapImageCache {
    private static final GridMapImageCache IC=new GridMapImageCache();
    private Map<String,bGridViewImage> cache = new ConcurrentHashMap<String,bGridViewImage>();
    private List<String> cacheorder = new ArrayList<String>();
    public static final int CACHE_B_LOADING=0;
    public static final int CACHE_B_LOADED=1;


    public static void put(String path,bGridViewImage image) {
        IC.cache.put(path,image);
        GridMapImageCache.addOrder(path);
        GridMapImageCache.trimCache();
    }
    public static void addOrder(String path) {
        IC.cacheorder.add(path);
    }
    public static bGridViewImage get(String path) {
        return IC.cache.get(path);
    }
    public static Map<String,bGridViewImage> get() {
        return IC.cache;
    }
    public static List<String> getOrder() {
        return IC.cacheorder;
    }
    public static void clearCache() {
        IC.cache.clear();
    }
    private final int MAX_CACHE=30;
    public static synchronized void trimCache() {
        if(IC.cacheorder.size()>IC.MAX_CACHE) {
            String rem = IC.cacheorder.remove(0);
            IC.cache.remove(rem);
        }
        System.gc();
    }
}
