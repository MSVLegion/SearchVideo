package ua.pp.seregamakarov.searchvideo;

import android.content.SearchRecentSuggestionsProvider;

public class SuggestionProvider extends SearchRecentSuggestionsProvider {
  public final static String AUTHORITY = "ua.pp.seregamakarov.searchvideo.SuggestionProvider";
  public final static int MODE = DATABASE_MODE_QUERIES;

  public SuggestionProvider() {
    setupSuggestions(AUTHORITY, MODE);
  }
}