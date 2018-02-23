package run.brief.search;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import run.brief.b.BCallback;
import run.brief.util.Cal;
import run.brief.util.explore.FileItem;
import run.brief.util.explore.IndexerDb;
import run.brief.util.explore.IndexerFile;

public class Searcher {

	private static List<IndexerFile> results=new ArrayList<IndexerFile>();

	
	public static List<IndexerFile> getResults() {
		return results;
	}
	public static List<FileItem> getResultsFileItems() {
		List<FileItem> items = new ArrayList<FileItem>();
		for(IndexerFile f: results) {
			items.add(f.getAsFileItem());
		}
		return items;
	}
	public static IndexerFile get(int index) {
		if(index>=0 && index<results.size()) {
			return results.get(index);
		}
		return null;
	}
	public static int size() {
		return results.size();
	}
	public static void clear() {
		results.clear();
	}
	public static void doSearch(Context context, String term, BCallback callback) {
		if(term !=null) {
			
			ArrayList<String> st=getWords(term.toLowerCase());

			IndexerDb.init(context);
			results = IndexerDb.searchByString(st);

			Log.e("SEARCH","SEARCH FOUND RESULTS :"+results.size());
			long last24 = Cal.getUnixTime()-(1000*60*60*24);

			// add ratings
			for(IndexerFile result: results) {
				if(st.size()>1) {
					for (String param : st) {
						if(result.getString(IndexerFile.STRING_FILENAME).indexOf(param)!=-1)
							result.rating++;
						if(result.getString(IndexerFile.STRING_FILEPATH).indexOf(param)!=-1)
							result.rating++;
					}
				}
				if(result.getLong(IndexerFile.LONG_MODIFIED)>last24)
					result.rating++;
				if(result.getLong(IndexerFile.LONG_FILESIZE)>50000)
					result.rating++;
			}
			reOrderResults();
		}


		if(callback!=null)
			callback.callback();
	}
    public static void doSearchShortcut(Context context, int type, BCallback callback) {


        IndexerDb.init(context);
        results = IndexerDb.searchByCategory(type);

        if(callback!=null)
            callback.callback();
    }
	public static void doSearchFolderByCat(Context context, int cat, BCallback callback) {


		IndexerDb.init(context);
		results = IndexerDb.getDb().getFoldersByCategory(cat, 0, 200);

		if(callback!=null)
			callback.callback();
	}
	private static void reOrderResults() {


		//Collections.sort(results, PersonComparator.ascending(PersonComparator.getComparator(PersonComparator.RATING_SORT, PersonComparator.NAME_SORT)));

		//Collections.sort(fileList, PersonComparator.decending(PersonComparator.getComparator(PersonComparator.NAME_SORT, PersonComparator.DATE_SORT)));
	}
	private enum PersonComparator implements Comparator<IndexerFile> {
		RATING_SORT {
			public int compare(IndexerFile o1, IndexerFile o2) {
				if(Cal.toDate(o1.rating).before(Cal.toDate(o2.rating)))
					return 1;
				return 0;
			}},
		NAME_SORT {
			public int compare(IndexerFile o1, IndexerFile o2) {
				return o1.getString(IndexerFile.STRING_FILENAME).toLowerCase(Locale.getDefault()).compareTo(o2.getString(IndexerFile.STRING_FILENAME).toLowerCase(Locale.getDefault()));
			}};

		public static Comparator<IndexerFile> decending(final Comparator<IndexerFile> other) {
			return new Comparator<IndexerFile>() {
				public int compare(IndexerFile o1, IndexerFile o2) {
					return -1 * other.compare(o1, o2);
				}
			};
		}
		public static Comparator<IndexerFile> ascending(final Comparator<IndexerFile> other) {
			return new Comparator<IndexerFile>() {
				public int compare(IndexerFile o1, IndexerFile o2) {
					return -1 * other.compare(o2, o1);
				}
			};
		}

		public static Comparator<IndexerFile> getComparator(final PersonComparator... multipleOptions) {
			return new Comparator<IndexerFile>() {
				public int compare(IndexerFile o1, IndexerFile o2) {
					for (PersonComparator option : multipleOptions) {
						int result = option.compare(o1, o2);
						if (result != 0) {
							return result;
						}
					}
					return 0;
				}
			};
		}
	}


	private static String getResultText(int index, String term, String searchText) {
		StringBuilder sb=new StringBuilder();
		int st=index-10;
		if(st<0)
			st=0;
		int se=index+40;
		if(se>searchText.length()-1)
			se=searchText.length();
		sb.append(searchText.substring(st, index));
		sb.append(term);
		sb.append(searchText.substring(index+term.length(), se));
		return sb.toString();
		
	}
	private static ArrayList<String> getWords(String s) {
		ArrayList<String> fwords=new ArrayList<String>();
		//ArrayList<String> f=new ArrayList<String>();
		String[] tmp = s.split(",");
		if(tmp!=null) {
			for(int i=0; i<tmp.length; i++) {
				String[] etmp = tmp[i].split("\\s");
				if(etmp!=null) {

					for(int j=0; j<etmp.length; j++) {
						if(etmp[j].length()>1)
							fwords.add(etmp[j]);
					}
				}
			}
		}
		return fwords;
	}
}
