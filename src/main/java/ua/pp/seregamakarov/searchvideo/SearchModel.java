package ua.pp.seregamakarov.searchvideo;

import java.net.URISyntaxException;


import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import org.xml.sax.helpers.DefaultHandler;

public abstract class SearchModel extends DefaultHandler {
	private String query;
	private Handler guiHandler;
	private Context activityContext;

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
		return Search.NUMBER_OF_PAGE;
	}
	
	public int getNumberOfItemsPerPage() {
		return Search.ITEMS_PER_PAGE;
	}

	private Handler getGuiHendler() {
		return guiHandler;
	}

	private void setGuiHendler(Handler guiHendler) {
		this.guiHandler = guiHendler;
	}

	public void itemCompleted() {
		getGuiHendler().sendEmptyMessage(Search.ITEM_COMPLETED);
	}
	
	public void stepCompleted() {
		getGuiHendler().sendEmptyMessage(Search.STEP_COMPLETED);
	}
	
	public void searchCompleted() {
		getGuiHendler().sendEmptyMessage(Search.SEARCH_COMPLETED);
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
			initialValues						  // the values to insert
		);
	}

	public boolean checkInternetConn() {
		Context context = getActivityContext();
		ConnectivityManager conMgr =  (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;
		return true;
	}
}
