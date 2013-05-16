package ua.pp.seregamakarov.searchvideo;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import android.content.Context;


public class MultipleSearch {
    private String query;
    private HashMap<String, SearchModel> searchMap;
    private ExecutorService service;
    private boolean firstRunWas;
    private Context activityContext;

    public MultipleSearch(Context context) {
        firstRunWas = false;
        activityContext =  context;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public void setSearchMap(HashMap<String, SearchModel> searchMap) {
        this.searchMap = searchMap;
    }

    public HashMap<String, SearchModel> getSearchMap() {
        return searchMap;
    }

    public void run() {
        service = Executors.newCachedThreadPool();
        for (String key : searchMap.keySet()) {
            SearchModel model = searchMap.get(key);
            model.setQuery(query);
            
            service.submit(new SearchThread(model));
        }
        firstRunWas = true;
    }
    
    public void serviceShutdown() {
        
        service.shutdown(); // Disable new tasks from being submitted
        try {
             // Wait a while for existing tasks to terminate
             if (!service.awaitTermination(2, TimeUnit.SECONDS)) {
                 service.shutdownNow(); // Cancel currently executing tasks
             // Wait a while for tasks to respond to being cancelled
             if (!service.awaitTermination(2, TimeUnit.SECONDS))
                 System.err.println("Pool did not terminate");
             }
        } catch (InterruptedException ie) {
             // (Re-)Cancel if current thread also interrupted
            service.shutdownNow();
             // Preserve interrupt status
            //Thread.currentThread().interrupt();
        }
    }
    
    public boolean isLive() {
        if (!firstRunWas) {
            return false;
        } else {
            boolean res = !service.isShutdown();
            return res;
        }
    }
    
}
