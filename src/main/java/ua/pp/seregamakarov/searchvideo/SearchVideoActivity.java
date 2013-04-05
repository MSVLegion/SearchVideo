package ua.pp.seregamakarov.searchvideo;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;

public class SearchVideoActivity extends FragmentActivity implements  OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	
	final String LOG_TAG = "mainActivity";
	final Uri VIDEOS_URI = Uri
		      .parse("content://ua.pp.seregamakarov.searchvideo.providers.VideoList/videos");
	private VideoListAdapter mAdapter;

	private MultipleSearch searchManager;
	private ProgressDialog mProgressDialog;
	private ImageButton mStopButton;
	private TextView mHeaderText;
	private SharedPreferences preference;
	
	private static final int STEP_SIZE = 1;
	private static final int STEP_COMPLETED = 1;
	private static final int SEARCH_COMPLETED = 2;
	private boolean activityIsAlive;
	public int searchCount = -1;
	private ProgressBar mProgress;
    private Handler mHandler;
	
    VideoListFragment videoListFragment;
    
	@Override
	protected void onDestroy() {
		Log.i("Activity, ", "onDestroy()");
		activityIsAlive = false;
		if (searchCount>0) {
			searchManager.serviceShutdown();
		}
		super.onDestroy();
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);

        init();
        handleIntent(getIntent());
        try {
        	if (preference.getBoolean("last", false)) {
        		showResults();
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
	private void init() {
		
		videoListFragment = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.frgmList);
		
		activityIsAlive = true;
		mStopButton = (ImageButton) findViewById(R.id.cancel_search);
		mStopButton.setOnClickListener(this);

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        
        mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(true);
		
		searchManager = new MultipleSearch(this);
		
		mHandler = new Handler() {
	      public void handleMessage(android.os.Message msg) {
	    	  switch (msg.what) {
	          case STEP_COMPLETED:
	  			mProgress = (ProgressBar) findViewById(R.id.progress_bar);
	        	mProgress.incrementProgressBy(STEP_SIZE);
	            break;
	          case SEARCH_COMPLETED:
	        	searchCount--;
	        	if ((searchCount==0)&(activityIsAlive)) {
	        		endOfSearch();
	        	}
	            break;
	          }
	      };
	    };
	}
    
	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		handleIntent(intent);
	}
    
    private void handleIntent(Intent intent) {
    	if (Intent.ACTION_SEARCH.equals(intent.getAction())) { 
    		if (searchCount>0) {
    			Toast toast = Toast.makeText(this, getResources().getString(R.string.wait_or_stop), Toast.LENGTH_LONG);
    			toast.show();
    		} else {
	            String query = intent.getStringExtra(SearchManager.QUERY);
	            
	            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
	                SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
	            
	            if (preference.getBoolean("recent", false)) {
	            	suggestions.saveRecentQuery(query, null);
	            }
	  			
	  			try { 				
	  				clearTable();

	  				HashMap<String, SearchModel> map = new HashMap<String, SearchModel>();
	  				map.put("youtubeSearch", new YoutubeSearchModel(mHandler, this));
	  				map.put("yahooSearch", new YahooSearchModel(mHandler, this));
	
	  				searchManager.setQuery(query);
	  				searchManager.setSearchMap(map);
	  				
	  				mHeaderText = (TextView) findViewById(R.id.header_text);
	  				mHeaderText.setText(R.string.search_wait);
	  				
	  				mProgress = (ProgressBar) findViewById(R.id.progress_bar);
	  				searchCount = map.size();
	  				mProgress.setProgress(0);
	  				mProgress.setMax(searchCount*getResources().getInteger(R.integer.searchPagesCount));    				
	  				mProgress.setVisibility(View.VISIBLE);
	  				
	  				mStopButton = (ImageButton) findViewById(R.id.cancel_search);
	  				mStopButton.setVisibility(View.VISIBLE);
	  				
	  				showResults();
                    searchManager.run();
	  				
	  			} catch (Exception e) {
	  				e.printStackTrace();
	  			}
    		}
          }
    }
    
    private void showResults() {
		String[] from = new String[] {VideoListProvider.KEY_TITLE, VideoListProvider.KEY_WEBSITE};
		int[] to = new int[] { R.id.titleVideo,  R.id.logoImg};

		mAdapter = new VideoListAdapter(this,
				R.layout.record, null, from, to);

		videoListFragment.setListAdapter(mAdapter);
		getSupportLoaderManager().initLoader(0, null, this);   
	}

    private void clearTable() {
    	int mRowsDeleted = 0;
    	mRowsDeleted = getContentResolver().delete(
    	    VIDEOS_URI,
    	    null,
    	    null
    	);
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
            case R.id.search:
                onSearchRequested();
                return true;
            case R.id.settings:
                Intent intent = new Intent(this, PrefActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case  R.id.cancel_search:
				mHeaderText = (TextView) findViewById(R.id.header_text);
				mHeaderText.setText(getResources().getString(R.string.stopping_searching));
				searchManager.serviceShutdown();
				break;
		}
		
	}

	private void endOfSearch() {
		mProgress = (ProgressBar) findViewById(R.id.progress_bar);
		mProgress.setVisibility(View.GONE);
		
		mStopButton = (ImageButton) findViewById(R.id.cancel_search);
		mStopButton.setVisibility(View.GONE);
		
		mHeaderText = (TextView) findViewById(R.id.header_text);
		mHeaderText.setText(getResources().getString(R.string.search_instructions));
		
		Toast toast = Toast.makeText(this, R.string.search_completed, Toast.LENGTH_SHORT);
		toast.show();
		showResultCount();
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri baseUri;
		baseUri = VIDEOS_URI;

		return new CursorLoader(this, baseUri,
					null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		mAdapter.changeCursor(null);
	}

	public String getVideoURL(long id){
		  String URL = "";
		  String[] columnsToTake = { VideoListProvider.KEY_ROWID, VideoListProvider.KEY_URL };
		  Uri uri = ContentUris.withAppendedId(VIDEOS_URI, id);
		  Cursor cursor = getContentResolver().query(uri, columnsToTake, null,
		        null, null);
		  try{
			  if (cursor.moveToFirst()) {
				  URL = cursor.getString(cursor.getColumnIndex(VideoListProvider.KEY_URL));
			  }
		  } finally{
			  cursor.close();
		  }
		  return URL;
	}
	
	public void showResultCount(){
		String[] columnsToTake = { VideoListProvider.KEY_ROWID, VideoListProvider.KEY_KWORDS };
		Cursor cursor = getContentResolver().query(VIDEOS_URI, columnsToTake, null,
	  	        null, null);
		try {
		  int count = cursor.getCount();
			if (count>0) {
				cursor.moveToFirst();
				String queryString = cursor.getString(cursor.getColumnIndex(VideoListProvider.KEY_KWORDS));
		        String countString = getResources().getQuantityString(R.plurals.search_results,
		                                count, new Object[] {count, queryString});
				
				Toast toast = Toast.makeText(this, countString, Toast.LENGTH_SHORT);
				toast.show();
			}
		} finally {
		  cursor.close();
		}
	}	
    
}