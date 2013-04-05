package ua.pp.seregamakarov.searchvideo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;

import android.content.Context;
import android.os.Handler;
import org.xml.sax.Attributes;

public class YoutubeSearchModel extends SearchModel {
	private String searchServiceName = "youtube";
    private boolean isEntry = false;
    private boolean isEntryTitle = false;
    private String title = "";
    private String link = "";

	public YoutubeSearchModel(Handler handler, Context context) {
		super(handler, context);
	}

    @Override
    public void startDocument(){
    }

    @Override
    public void startElement(String namespaceURI, String localName,
                             String rawName, Attributes attrs) {
        if (rawName.equalsIgnoreCase("entry")) {
            isEntry = true;
        }
        if (isEntry) {
            if (rawName.equalsIgnoreCase("title")) {
                isEntryTitle = true;
            }
            if (rawName.equalsIgnoreCase("link")) {
                if (attrs.getValue("rel").equalsIgnoreCase("alternate")) {
                    this.link = attrs.getValue("href");
                }

            }
        }
    }

    @Override
    public void characters(char ch[], int start, int length) {
        if (isEntryTitle) {
            this.title = new String(ch, start, length);
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName,
                           String rawName) {
        if (isEntry) {
            if (rawName.equalsIgnoreCase("title")) {
                isEntryTitle = false;
            }
        }
        if (rawName.equalsIgnoreCase("entry")) {
            isEntry = false;
            insertVideoLink(getQuery().replaceAll("\\s+", " "), link, title, searchServiceName);
        }
    }

    @Override
    public void endDocument() {
    }

	@Override
	public void getResults() throws URISyntaxException {
		String query = getQuery().trim();
		int offset = 1;
        //pp git change
		
		int iter = 0;
		while((iter<getPagesCount())&(!Thread.currentThread().isInterrupted())){
			String urlString = "http://gdata.youtube.com/feeds/api/videos?"
                    + "alt=json"
					+ "&q=" + query.replaceAll("\\s+", "+")
					+ "&start-index=" + String.valueOf(offset)
					+ "&max-results="
					+ String.valueOf(getElementsPerPageCount()) + "&v=2";

            try {
                URL url = new URL(urlString);
                URLConnection urlConnection = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

                YoutubeJsonResult results = new Gson().fromJson(reader, YoutubeJsonResult.class);
                for(YoutubeJsonResult.Feed.Entry videoEntry : results.feed.entry) {
                    String title = videoEntry.title.$t;
                    String href = "";
                    for(YoutubeJsonResult.Feed.Entry.Link videoLink : videoEntry.link) {
                        if (videoLink.rel.equalsIgnoreCase("alternate")) {
                            href = videoLink.href;
                        }
                    }
                    insertVideoLink(query.replaceAll("\\s+", " "), href, title, searchServiceName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
			offset+=getElementsPerPageCount();
			iter++;
			stepCompleted();
		}
		searchCompleted();
	}
}
