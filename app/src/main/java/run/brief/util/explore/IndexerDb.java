package run.brief.util.explore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import run.brief.util.Db;
import run.brief.util.DbField;
import run.brief.util.Files;
import run.brief.util.Sf;


public class IndexerDb {
	
	private static final IndexerDb NOTES = new IndexerDb();
	
	private static final String DB_DEFAULT_ITEMS="files";
	
	private int countNew;
	private List<IndexerFile> lastSearchData=new ArrayList<IndexerFile>();
	//private ArrayList<RssUserFeed> userdata;
	private IndexerDbTable database;
	private boolean isLoaded=false;
	//public AsyncTask<RssUserFeed, Void, Integer> doload;


	public IndexerDb() {
		//Load();
	}

	public static List<IndexerFile> getLastSearch() {
		return NOTES.lastSearchData;
	}
	public static List<IndexerFile> searchByString(ArrayList<String> terms) {
		NOTES.lastSearchData= NOTES.database.searchAbsoluteFile(terms,0,200);
		return NOTES.lastSearchData;
	}
	public static List<IndexerFile> searchByString(String term) {
		NOTES.lastSearchData= NOTES.database.searchAbsoluteFile(term, 0, 200);
		return NOTES.lastSearchData;
	}
	public static List<IndexerFile> searchByCategory(int type) {
		NOTES.lastSearchData=NOTES.database.searchByCategory(type, 0, 200);
		return NOTES.lastSearchData;
	}
	public static List<IndexerFile> searchByCreateDate(long dateLT, long dateGT) {
		NOTES.lastSearchData=NOTES.database.searchByModifiedDate(dateLT, dateGT, 0, 200);
		return NOTES.lastSearchData;
	}




	public static final IndexerDbTable getDb() {
		return NOTES.database;
	}
	
	public synchronized static void init(Context context) {
		if(NOTES.database==null) {
			NOTES.database=NOTES.new IndexerDbTable(context);
			//SortIndexerFiles();
		}
	}


	public synchronized static boolean remove(IndexerFile item) {
		if(item!=null) {
			NOTES.database.delete(item);
			return true;
		} else 
			return false;
		
	}
	public synchronized static long add(IndexerFile item) {
		if(item!=null) {
			long id=NOTES.database.add(item);
			return id;
		}
		return -1;
	}
	public synchronized static void update(IndexerFile item) {
		if(item!=null) {
			NOTES.database.update(item);
		}
		
	}
	public static boolean has(String filename, String filepath) {
		if(NOTES.database.hasItem(filename,filepath))
			return true;
		return false;
		
	}
	public class IndexerDbTable extends Db {

		public IndexerDbTable(Context context) {

			super("IndexerFiles",
					new DbField[] {
							new DbField(IndexerFile.LONG_ID,DbField.FIELD_TYPE_INT,true,false),
							new DbField(IndexerFile.STRING_FILENAME,DbField.FIELD_TYPE_TEXT,false,true),
							new DbField(IndexerFile.STRING_FILEPATH,DbField.FIELD_TYPE_TEXT,false,true),
							new DbField(IndexerFile.INT_CATEGORY,DbField.FIELD_TYPE_INT,false,true),
							new DbField(IndexerFile.LONG_FILESIZE,DbField.FIELD_TYPE_INT),
							new DbField(IndexerFile.INT_ICONTYPE,DbField.FIELD_TYPE_INT),
							new DbField(IndexerFile.LONG_MODIFIED,DbField.FIELD_TYPE_INT,false,true),
							new DbField(IndexerFile.INT_BOOL_ISFOLDER,DbField.FIELD_TYPE_INT),
					}
					,context
			);
			this.context=context;

		}
		public ArrayList<IndexerFile> searchAbsoluteFile(List<String> terms, int limitStart, int limitEnd) {
			ArrayList<IndexerFile> items = new ArrayList<IndexerFile>();

			Cursor cur =null;
			for(String term: terms) {
                //term="IMG";
				cur=db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+IndexerFile.INT_BOOL_ISFOLDER + "=0 AND " +IndexerFile.STRING_FILENAME+" LIKE '%"+Sf.makeDbSafe(term)+"%'  COLLATE NOCASE LIMIT "+limitStart+","+limitEnd,null);
				//cur = db.query(TABLE_NAME, this.getFieldNames(), IndexerFile.STRING_FILENAME + " LIKE ?", new String[]{"%" + term + "%"}, null, null, IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd+" COLLATE NOCASE");
//BLog.e( "DB SEARCH '" + term + "' RESULTS: " + cur.getCount());
				if (cur.getCount() > 0) {
					cur.moveToFirst();
					do {
						items.add(getIndexerFileFromCursor(cur));
					} while (cur.moveToNext());

				}
			}
			if(cur!=null)
				cur.close();

			return items;
		}
		public ArrayList<IndexerFile> searchAbsoluteFile(String term, int limitStart, int limitEnd) {
			ArrayList<IndexerFile> items = new ArrayList<IndexerFile>();
            Cursor cur=db.rawQuery("SELECT * FROM "+TABLE_NAME+" WHERE "+IndexerFile.INT_BOOL_ISFOLDER + "=0 AND " +IndexerFile.STRING_FILENAME+" LIKE '%"+Sf.makeDbSafe(term)+"%'  COLLATE NOCASE LIMIT "+limitStart+","+limitEnd,null);
			//Cursor cur = db.query(TABLE_NAME, this.getFieldNames(),IndexerFile.STRING_FILENAME +"=?", new String[]{"%"+term+"%"}, null, null, IndexerFile.LONG_MODIFIED +" DESC LIMIT "+limitStart+","+limitEnd);

			if(cur.getCount()>0) {
				cur.moveToFirst();
				do {
					items.add(getIndexerFileFromCursor(cur));
				} while(cur.moveToNext());

			}

			cur.close();

			return items;
		}
		public HashMap<String,IndexerFile> getSubFolderCategories(String parentFolder) {
			HashMap<String,IndexerFile> items = new HashMap<String,IndexerFile>();
            parentFolder=parentFolder.replaceFirst(Files.SDCARD_PATH,"");
            //BLog.e(parentFolder);
			//Cursor cur = db.query(TABLE_NAME, this.getFieldNames(),IndexerFile.STRING_FILEPATH +"=?", new String[]{parentFolder}, null, null, null);
            //Cursor cur = db.query(TABLE_NAME, this.getFieldNames(),IndexerFile.INT_BOOL_ISFOLDER +"=1 AND "+IndexerFile.STRING_FILEPATH +"?", new String[]{parentFolder+"%"}, null, null, null);
            Cursor cur=db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + IndexerFile.INT_BOOL_ISFOLDER + "=1 AND " + IndexerFile.STRING_FILEPATH + " LIKE '" + Sf.makeDbSafe(parentFolder) + "%'  COLLATE NOCASE", null);

			if(cur.getCount()>0) {
				cur.moveToFirst();
				do {
					IndexerFile iff=getIndexerFileFromCursor(cur);
					items.put(iff.getString(IndexerFile.STRING_FILENAME), iff);
				} while(cur.moveToNext());

			}

			cur.close();

			return items;
		}
		public ArrayList<IndexerFile> searchByCategory(int category, int limitStart, int limitEnd) {
			ArrayList<IndexerFile> items = new ArrayList<IndexerFile>();
			Cursor cur = db.query(TABLE_NAME, this.getFieldNames(), IndexerFile.INT_CATEGORY + "=?", new String[]{category + ""}, null, null, IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd);

			if(cur.getCount()>0) {
				cur.moveToFirst();
				do {
					items.add(getIndexerFileFromCursor(cur));
				} while(cur.moveToNext());

			}

			cur.close();

			return items;
		}
        public ArrayList<IndexerFile> getFoldersByCategory(int category, int limitStart, int limitEnd) {
            ArrayList<IndexerFile> items = new ArrayList<IndexerFile>();
			Cursor cur=null;
			if(category==Files.CAT_ANY) {
				//cur = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + IndexerFile.INT_BOOL_ISFOLDER+"=1 ORDER BY " + IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd, null);
				cur = db.query(TABLE_NAME, this.getFieldNames(), IndexerFile.INT_BOOL_ISFOLDER+"=1", null, null, null, IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd);
			} else {
				cur = db.query(TABLE_NAME, this.getFieldNames(), IndexerFile.INT_CATEGORY + "=? AND "+IndexerFile.INT_BOOL_ISFOLDER+"=1", new String[]{category + ""}, null, null, IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd);
			}


            if(cur.getCount()>0) {
                cur.moveToFirst();
                do {
                    items.add(getIndexerFileFromCursor(cur));
                } while(cur.moveToNext());

            }

            cur.close();

            return items;
        }
		public ArrayList<IndexerFile> searchByModifiedDate(long dateLT, long dateGT, int limitStart, int limitEnd) {
			ArrayList<IndexerFile> items = new ArrayList<IndexerFile>();
			String whereStr = IndexerFile.LONG_MODIFIED+"<"+dateLT+" AND "+IndexerFile.LONG_MODIFIED+">"+dateGT;
			if(dateGT<1)
				whereStr = IndexerFile.LONG_MODIFIED+"<"+dateLT;
			else if(dateLT<1)
				whereStr = IndexerFile.LONG_MODIFIED+">"+dateGT;

			Cursor cur = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + whereStr + " ORDER BY " + IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd, null);

			if(cur.getCount()>0) {
				cur.moveToFirst();
				do {
					items.add(getIndexerFileFromCursor(cur));
				} while(cur.moveToNext());

			}

			cur.close();

			return items;
		}
		public ArrayList<IndexerFile> getItems(int limitStart, int limitEnd) {
			ArrayList<IndexerFile> items = new ArrayList<IndexerFile>();
			if(db!=null) {
				Cursor cur = db.query(TABLE_NAME, this.getFieldNames(), null, null, null, null, IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd);

				if (cur.getCount() > 0) {
					cur.moveToFirst();
					do {
						items.add(getIndexerFileFromCursor(cur));
					} while (cur.moveToNext());

				}

				cur.close();
			}

			return items;
		}
		public ArrayList<IndexerFile> getImageVideoItems(int limitStart, int limitEnd) {
			ArrayList<IndexerFile> items = new ArrayList<IndexerFile>();
			if(db!=null) {
				Cursor cur = db.query(TABLE_NAME, this.getFieldNames(), IndexerFile.INT_CATEGORY+"="+Files.CAT_IMAGE +" OR "+IndexerFile.INT_CATEGORY+"="+Files.CAT_VIDEO, null, null, null, IndexerFile.LONG_MODIFIED + " DESC LIMIT " + limitStart + "," + limitEnd);

				if (cur.getCount() > 0) {
					cur.moveToFirst();
					do {
						items.add(getIndexerFileFromCursor(cur));
					} while (cur.moveToNext());

				}

				cur.close();
			}

			return items;
		}
		public void update(IndexerFile item) {
			ContentValues values = new ContentValues();

			values.put(IndexerFile.STRING_FILENAME, item.getString(IndexerFile.STRING_FILENAME));
            values.put(IndexerFile.STRING_FILEPATH, item.getString(IndexerFile.STRING_FILEPATH).replaceFirst(Files.SDCARD_PATH, ""));
			values.put(IndexerFile.INT_CATEGORY, item.getInt(IndexerFile.INT_CATEGORY));
			values.put(IndexerFile.LONG_FILESIZE, item.getLong(IndexerFile.LONG_FILESIZE));
			values.put(IndexerFile.INT_ICONTYPE, item.getInt(IndexerFile.INT_ICONTYPE));
			values.put(IndexerFile.LONG_MODIFIED, item.getLong(IndexerFile.LONG_MODIFIED));
            values.put(IndexerFile.INT_BOOL_ISFOLDER, item.getInt(IndexerFile.INT_BOOL_ISFOLDER));
			if(db!=null) {
				long id = db.update(TABLE_NAME, values, IndexerFile.LONG_ID + "=?", new String[]{"" + item.getLong(IndexerFile.LONG_ID)});
			}

		}

		private IndexerFile getIndexerFileFromCursor(Cursor cursor) {
			return new IndexerFile(cursor.getLong(cursor.getColumnIndex(IndexerFile.LONG_ID))
					,cursor.getString(cursor.getColumnIndex(IndexerFile.STRING_FILENAME))
					,Files.SDCARD_PATH+ cursor.getString(cursor.getColumnIndex(IndexerFile.STRING_FILEPATH))
					,cursor.getInt(cursor.getColumnIndex(IndexerFile.INT_CATEGORY))
					,cursor.getLong(cursor.getColumnIndex(IndexerFile.LONG_FILESIZE))
					,cursor.getInt(cursor.getColumnIndex(IndexerFile.INT_ICONTYPE))
					,cursor.getLong(cursor.getColumnIndex(IndexerFile.LONG_MODIFIED))
                    ,cursor.getInt(cursor.getColumnIndex(IndexerFile.INT_BOOL_ISFOLDER))
			);

		}

		public boolean hasItem(String filename, String filepath) {
			boolean alreadyHasFeed=false;
			if(db!=null) {
				Cursor cur = db.query(TABLE_NAME, this.getFieldNames(),
						IndexerFile.STRING_FILENAME + "=? AND " + IndexerFile.STRING_FILEPATH + "=?", new String[]{filename, filepath.replaceFirst(Files.SDCARD_PATH,"")}, null, null, null);


				if (cur != null && cur.getCount() > 0)
					alreadyHasFeed = true;
				cur.close();
			}
			return alreadyHasFeed;
		}

		public long add(IndexerFile item) {

			if(item!=null && db!=null) {
				ContentValues values = new ContentValues();
                //if(item.getAsFileItem().isDirectory())
                //    BLog.e("ADD TO DB: "+item.getString(IndexerFile.STRING_FILENAME));
				values.put(IndexerFile.STRING_FILENAME, item.getString(IndexerFile.STRING_FILENAME));
				values.put(IndexerFile.STRING_FILEPATH, item.getString(IndexerFile.STRING_FILEPATH).replaceFirst(Files.SDCARD_PATH, ""));
				values.put(IndexerFile.INT_CATEGORY, item.getInt(IndexerFile.INT_CATEGORY));
				values.put(IndexerFile.LONG_FILESIZE, item.getLong(IndexerFile.LONG_FILESIZE));
				values.put(IndexerFile.INT_ICONTYPE, item.getInt(IndexerFile.INT_ICONTYPE));
				values.put(IndexerFile.LONG_MODIFIED, item.getLong(IndexerFile.LONG_MODIFIED));
                values.put(IndexerFile.INT_BOOL_ISFOLDER, item.getInt(IndexerFile.INT_BOOL_ISFOLDER));
				return db.insert(TABLE_NAME, null, values);
			}
			return -1;
		}

		public void delete(IndexerFile item) {
			open();
			db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + IndexerFile.STRING_FILENAME + " = '" + item.getString(IndexerFile.STRING_FILENAME) + "' AND " + IndexerFile.STRING_FILEPATH + " = '" + item.getString(IndexerFile.STRING_FILENAME) + "'");

		}
		public void deleteAll() {
			open();
			db.execSQL("DELETE FROM "+TABLE_NAME);

		}
	}
}
