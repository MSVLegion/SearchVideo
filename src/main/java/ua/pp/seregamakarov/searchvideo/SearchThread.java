package ua.pp.seregamakarov.searchvideo;

import java.net.URISyntaxException;

public class SearchThread implements Runnable {
	private SearchModel model;
	
	public SearchThread(SearchModel model) {
		setModel(model);
	}
	
	public void setModel(SearchModel model) {
		this.model = model;
	}

	public SearchModel getModel() {
		return model;
	}

	public void run() {
		try {
			model.getResults();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		Thread.currentThread().interrupt();
	}
}
