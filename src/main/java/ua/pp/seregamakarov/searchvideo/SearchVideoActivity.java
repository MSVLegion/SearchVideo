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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SearchVideoActivity extends SherlockFragmentActivity implements  OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
	
	final String LOG_TAG = "mainActivity";
	final Uri VIDEOS_URI = Uri
		      .parse("content://ua.pp.seregamakarov.searchvideo.providers.VideoList/videos");
	private VideoListAdapter mAdapter;

	private MultipleSearch mMultipleSearch;
	private ProgressDialog mProgressDialog;
	private ImageButton mStopButton;
    private View vProgressPanel;
    private TextView tvNumVideos;
    private TextView tvProgressPercent;
	private SharedPreferences preference;

	private boolean activityIsAlive;
	public int searchCount = -1;
    private int numFoundVideo = 0;
    private int progressSize = 100;
    private int currStep = 0;
	private ProgressBar mProgress;
    private Handler mHandler;
	
    VideoListFragment mVideoListFragment;
    
	@Override
	protected void onDestroy() {
		Log.i("Activity, ", "onDestroy()");
		activityIsAlive = false;
		if (searchCount>0) {
			mMultipleSearch.serviceShutdown();
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

    }
    
	private void init() {
		
		mVideoListFragment = (VideoListFragment) getSupportFragmentManager().findFragmentById(R.id.frgmList);
		
		activityIsAlive = true;
		mStopButton = (ImageButton) findViewById(R.id.cancel_search);
		mStopButton.setOnClickListener(this);

        preference = PreferenceManager.getDefaultSharedPreferences(this);
        
        mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setCancelable(true);
		
		mMultipleSearch = new MultipleSearch(this);
		
		mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case Search.ITEM_COMPLETED:
                        numFoundVideo++;
                        tvNumVideos = (TextView) findViewById(R.id.download_count);
                        tvNumVideos.setText(String.format(getResources().getString(R.string.num_results), numFoundVideo));
                        break;
                    case Search.STEP_COMPLETED:
                        currStep++;
                        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
                        mProgress.incrementProgressBy(Search.PROGRESS_BAR_STEP_SIZE);
                        tvProgressPercent = (TextView) findViewById(R.id.download_percent);
                        tvProgressPercent.setText(String.format(getResources().getString(R.string.progress_percent), (int) (100. / progressSize * currStep)));
                        break;
                    case Search.SEARCH_COMPLETED:
                        searchCount--;
                        if ((searchCount==0)&(activityIsAlive)) {
                            endOfSearch();
                        }
                        break;
                }
            };
	    };
        try {
            if (preference.getBoolean("last", false)) {
                showResults();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    HashMap<String, SearchModel> searchMap = new HashMap<String, SearchModel>();
                    if (preference.getBoolean("pref_service_youtube_key", true)) {
                        searchMap.put("youtubeSearch", new YoutubeSearchModel(mHandler, this));
                    }
                    if (preference.getBoolean("pref_service_yahoo_key", true)) {
                        searchMap.put("yahooSearch", new YahooSearchModel(mHandler, this));
                    }
                    searchCount = searchMap.size();
                    if (searchCount>0) {
                        clearTable();
                        mMultipleSearch.setQuery(query);
                        mMultipleSearch.setSearchMap(searchMap);

                        numFoundVideo = 0;
                        tvNumVideos = (TextView) findViewById(R.id.download_count);
                        tvNumVideos.setText(String.format(getResources().getString(R.string.num_results), numFoundVideo));

                        mProgress = (ProgressBar) findViewById(R.id.progress_bar);
                        mProgress.setProgress(0);
                        progressSize = searchCount*Search.NUMBER_OF_PAGE;
                        mProgress.setMax(progressSize);
                        currStep = 0;

                        tvProgressPercent = (TextView) findViewById(R.id.download_percent);
                        tvProgressPercent.setText(String.format(getResources().getString(R.string.progress_percent), 0));
                        //mProgress.setVisibility(View.VISIBLE);
                        //mStopButton = (ImageButton) findViewById(R.id.cancel_search);
                        //mStopButton.setVisibility(View.VISIBLE);

                        vProgressPanel = findViewById(R.id.progress_panel);
                        vProgressPanel.setVisibility(View.VISIBLE);
                        showResults();
                        mMultipleSearch.run();
                    } else {
                        Toast toast = Toast.makeText(this, getResources().getString(R.string.info_not_selected_search_service), Toast.LENGTH_LONG);
                        toast.show();
                    }
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

		mVideoListFragment.setListAdapter(mAdapter);
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
		MenuInflater inflater = getSupportMenuInflater();
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

	//@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case  R.id.cancel_search:
				mMultipleSearch.serviceShutdown();
				break;
		}
		
	}

	private void endOfSearch() {
        vProgressPanel = findViewById(R.id.progress_panel);
        vProgressPanel.setVisibility(View.GONE);
		Toast toast = Toast.makeText(this, R.string.search_completed, Toast.LENGTH_SHORT);
		toast.show();
		showResultCount();
	}

	//@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri baseUri;
		baseUri = VIDEOS_URI;
		return new CursorLoader(this, baseUri,
					null, null, null, null);
	}

	//@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		mAdapter.changeCursor(cursor);
	}

	//@Override
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