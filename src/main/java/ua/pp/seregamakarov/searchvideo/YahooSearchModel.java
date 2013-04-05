package ua.pp.seregamakarov.searchvideo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import com.google.gson.Gson;
import org.w3c.dom.Document;

import android.content.Context;
import android.os.Handler;

public class YahooSearchModel extends SearchModel {
	
	private String searchServiceName = "yahoo";
	
	public YahooSearchModel(Handler handler, Context context) {
		super(handler, context);
	}
	
	@Override
	public void getResults() throws URISyntaxException {
		int skip = 0;
		String query = getQuery().trim();

		// String accountKey = "OuiQZ9Gzuj8Kj6TGsCIqf0FRt/Ke38TJTRJjyfeHxoo=";
		// byte[] accountKeyBytes = Base64.encode((accountKey + ":" + accountKey).getBytes());
		byte[] accountKeyBytes = "T3VpUVo5R3p1ajhLajZUR3NDSXFmMEZSdC9LZTM4VEpUUkpqeWZlSHhvbz06T3VpUVo5R3p1ajhLajZUR3NDSXFmMEZSdC9LZTM4VEpUUkpqeWZlSHhvbz0="
				.getBytes();
		String accountKeyEnc = new String(accountKeyBytes);

		int iter = 0;
		while((iter<getPagesCount())&(!Thread.currentThread().isInterrupted())){
			String bingUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/Video?Query=%27site%3ayahoo.com%20"
					+ query.replaceAll("\\s+", "+")
					+ "%27&$top="
					+ getElementsPerPageCount()
					+ "&$skip="
					+ skip
					+ "&$format=json";

			Document document = null;
			try {
				URL url = new URL(bingUrl);
				URLConnection urlConnection = url.openConnection();
				urlConnection.setRequestProperty("Authorization", "Basic "
						+ accountKeyEnc);

                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                BingJsonResult results = new Gson().fromJson(reader, BingJsonResult.class);
                for(BingJsonResult.D.R videoResult : results.d.results) {
                    insertVideoLink(query.replaceAll("\\s+", " "), videoResult.MediaUrl, videoResult.Title, searchServiceName);
                }
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			skip+=getElementsPerPageCount();
			iter++;
			stepCompleted();
		}
		searchCompleted();
	}
}
