package ua.pp.seregamakarov.searchvideo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;
import com.google.gson.Gson;

import android.content.Context;
import android.os.Handler;

public class YahooSearchModel extends SearchModel {
	
	private String searchServiceName = "yahoo";
    private boolean failConnection = false;
	
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
                try {
                    failConnection = false;
                    while(!checkInternetConn()) {
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                        Thread.sleep(200);
                    }

                    String bingUrl = "https://api.datamarket.azure.com/Data.ashx/Bing/Search/Video?Query=%27site%3ayahoo.com%20"
                            + query.replaceAll("\\s+", "+")
                            + "%27&$top="
                            + getNumberOfItemsPerPage()
                            + "&$skip="
                            + skip
                            + "&$format=json";
                    try {
                        URL url = new URL(bingUrl);
                        URLConnection urlConnection = url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Basic "
                                + accountKeyEnc);

                        BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        BingJsonResult results = new Gson().fromJson(reader, BingJsonResult.class);
                        for(BingJsonResult.D.R videoResult : results.d.results) {
                            insertVideoLink(query.replaceAll("\\s+", " "), videoResult.MediaUrl, videoResult.Title, searchServiceName);
                            itemCompleted();
                            Thread.sleep(150);
                            if (Thread.currentThread().isInterrupted())
                                break;
                        }
                    } catch (NullPointerException nullEx) {
                        failConnection = true;
                        Log.e("YoutubeSearch", "NullPointerException");
                    } catch (ConnectException e) {
                        failConnection = true;
                        Log.e("YoutubeSearch", "ConnectException");
                    }
                } catch (InterruptedException e) {
                    break;
                }catch (Exception e) {
                    e.printStackTrace();
                    //insertVideoLink(query.replaceAll("\\s+", " "), "", e.toString(), searchServiceName);
                }
                if (!failConnection) {
                    skip+= getNumberOfItemsPerPage();
                    iter++;
                    stepCompleted();
                }
            }
		searchCompleted();
	}
}
