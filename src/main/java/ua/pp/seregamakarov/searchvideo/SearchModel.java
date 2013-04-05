package ua.pp.seregamakarov.searchvideo;

import java.net.URISyntaxException;


import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SearchModel extends DefaultHandler {
	private static final int STEP_COMPLETED = 1;
	private static final int SEARCH_COMPLETED = 2;
	private String query;
	private Handler guiHandler;
	private Context activityContext;
	private static final int elementsPerPageCount = 10; // 10 YouTube API recommended
	private static final int pagesCount = 15;

	public SearchModel(Handler handler, Context context) {
		this.guiHandler = handler;
		this.setActivityContext(context);
	}

	public void setQuery(String query) {
		this.query = query.trim();
	}

	public String getQuery() {
		return query;
	}

	public String toString() {
		return "Search system";
	}

	public abstract void getResults() throws URISyntaxException;
	
	public int getPagesCount() {
		return pagesCount;
	}
	
	public int getElementsPerPageCount() {
		return elementsPerPageCount;
	}

	private Handler getGuiHendler() {
		return guiHandler;
	}

	private void setGuiHendler(Handler guiHendler) {
		this.guiHandler = guiHendler;
	}
	
	public void stepCompleted() {
		getGuiHendler().sendEmptyMessage(STEP_COMPLETED);
	}
	
	public void searchCompleted() {
		getGuiHendler().sendEmptyMessage(SEARCH_COMPLETED);
	}

	private Context getActivityContext() {
		return activityContext;
	}

	private void setActivityContext(Context activityContext) {
		this.activityContext = activityContext;
	}
	
	protected void insertVideoLink(String keyWords, String url, String title, String website) {
		// Defines a new Uri object that receives the result of the insertion
    	Uri mNewUri;
    	
    	ContentValues initialValues = new ContentValues();
        initialValues.put(VideoListProvider.KEY_KWORDS, keyWords);
        initialValues.put(VideoListProvider.KEY_URL, url);
        initialValues.put(VideoListProvider.KEY_TITLE, title);
        initialValues.put(VideoListProvider.KEY_WEBSITE, website);
        
        mNewUri = getActivityContext().getContentResolver().insert(
    		VideoListProvider.VIDEOS_CONTENT_URI,   // the user videos content URI
    		initialValues                          // the values to insert
    	);
	}
}
