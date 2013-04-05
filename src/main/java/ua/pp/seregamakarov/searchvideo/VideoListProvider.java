package ua.pp.seregamakarov.searchvideo;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class VideoListProvider extends ContentProvider {
	final String LOG_TAG = "VideoListProvider";

	//DB
	static final String DB_NAME = "searchdata";
	public static final int DB_VERSION = 1;
	
	//Tables
	static final String VIDEO_TABLE = "videos";
	
	//Fields
	static final String KEY_ROWID = "_id";
	static final String KEY_KWORDS = "key_words";
	static final String KEY_URL = "url";
	static final String KEY_TITLE = "title";
	static final String KEY_WEBSITE = "website";
	
	private static final String DB_CREATE = "CREATE TABLE " + VIDEO_TABLE + "(" + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
		      + KEY_KWORDS + " TEXT NOT NULL, " + KEY_URL + " TEXT NOT NULL, " + KEY_TITLE + " TEXT NOT NULL, " 
			+ KEY_WEBSITE + " TEXT NOT NULL);";
	
	////Uri
	//authority
	static final String AUTHORITY = "ua.pp.seregamakarov.searchvideo.providers.VideoList";
	
	//path
	static final String VIDEOS_PATH = "videos";
	
	//Common Uri
	public static final Uri VIDEOS_CONTENT_URI = Uri.parse("content://"
	  + AUTHORITY + "/" + VIDEOS_PATH);
	
	//Types data
	static final String VIDEOS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd."
			+ AUTHORITY + "." + VIDEOS_PATH;

	static final String VIDEOS_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd."
			+ AUTHORITY + "." + VIDEOS_PATH;
	
	////UriMatcher
	//Common Uri
	static final int URI_VIDEOS = 1;
	
	//Uri with ID
	static final int URI_VIDEOS_ID = 2;
	
	//Creating UriMatcher
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, VIDEO_TABLE, URI_VIDEOS);
		uriMatcher.addURI(AUTHORITY, VIDEO_TABLE + "/#", URI_VIDEOS_ID);
	}
	
    private SQLiteOpenHelper mHelper;
	
	//Closes the SQLite database helper class
    public void close() {
        mHelper.close();
    }
	
	private static class VideoProviderHelper extends SQLiteOpenHelper {
		
		VideoProviderHelper(Context context) {
		  super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		  Log.w(VideoProviderHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
			  + newVersion + ", which will destroy all old data");
		  db.execSQL("DROP TABLE IF EXISTS tasks");
		  onCreate(db);
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		Log.d(LOG_TAG, "delete, " + uri.toString());
		
	    switch (uriMatcher.match(uri)) {
		    case URI_VIDEOS:
		      Log.d(LOG_TAG, "URI_CONTACTS");
		      break;
		    case URI_VIDEOS_ID:
		      String id = uri.getLastPathSegment();
		      Log.d(LOG_TAG, "URI_CONTACTS_ID, " + id);
		      if (TextUtils.isEmpty(selection)) {
		        selection = KEY_ROWID + " = " + id;
		      } else {
		        selection = selection + " AND " + KEY_ROWID + " = " + id;
		      }
		      break;
		    default:
		      throw new IllegalArgumentException("Wrong URI: " + uri);
	    }
	    SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();
	    int cnt = localSQLiteDatabase.delete(VIDEO_TABLE, selection, selectionArgs);
	    getContext().getContentResolver().notifyChange(uri, null);
	    return cnt;
	}

	@Override
	public String getType(Uri uri) {
		Log.d(LOG_TAG, "getType, " + uri.toString());
	    switch (uriMatcher.match(uri)) {
	    case URI_VIDEOS:
	      return VIDEOS_CONTENT_TYPE;
	    case URI_VIDEOS_ID:
	      return VIDEOS_CONTENT_ITEM_TYPE;
	    }
	    return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		//Log.d(LOG_TAG, "insert, " + uri.toString());
	    if (uriMatcher.match(uri) != URI_VIDEOS)
	      throw new IllegalArgumentException("Wrong URI: " + uri);

	    SQLiteDatabase localSQLiteDatabase = mHelper.getWritableDatabase();
	    long rowID = localSQLiteDatabase.insert(VIDEO_TABLE, null, values);
	    if (rowID >0) {
	    	Uri resultUri = ContentUris.withAppendedId(VIDEOS_CONTENT_URI, rowID);
	    	getContext().getContentResolver().notifyChange(resultUri, null);
	    	return resultUri;
	    }
	    throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public boolean onCreate() {
		Log.d(LOG_TAG, "onCreate");
	    mHelper = new VideoProviderHelper(getContext());
	    return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		//Log.d(LOG_TAG, "query, " + uri.toString());
	    //Check Uri
	    switch (uriMatcher.match(uri)) {
	    
		    case URI_VIDEOS:
		      //Log.d(LOG_TAG, "URI_VIDEOS");

		      if (TextUtils.isEmpty(sortOrder)) {
		        sortOrder = KEY_ROWID + " ASC";
		      }
		      break;
		      
		    case URI_VIDEOS_ID:
		      String id = uri.getLastPathSegment();
		      //Log.d(LOG_TAG, "URI_VIDEOS_ID, " + id);

		      if (TextUtils.isEmpty(selection)) {
		        selection = KEY_ROWID + " = " + id;
		      } else {
		        selection = selection + " AND " + KEY_ROWID + " = " + id;
		      }
		      break;
	    default:
	      throw new IllegalArgumentException("Wrong URI: " + uri);
	    }
	    
	    SQLiteDatabase db = mHelper.getReadableDatabase();
	    Cursor cursor = db.query(VIDEO_TABLE, projection, selection,
	        selectionArgs, null, null, sortOrder);

	    cursor.setNotificationUri(getContext().getContentResolver(),
	        VIDEOS_CONTENT_URI);
	    return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
