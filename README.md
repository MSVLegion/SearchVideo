## SearchVideo

This is my first app on Android.

![SearchVideoexample](https://raw.github.com/MSVLegion/Screenshots/master/SearchVideo/Preview/search_video_device_both.png)

## Terms of Reference

To develop an Android OS application for queries execution to Yahoo! and YouTube search engines. The application should have a field for keywords input and a table with search results.

### Requirements:
- simultaneous search in Yahoo! and Youtube
- application should process first 30 pages of search results and update data in the table
- table should contain one column. Each row in the table should have a heading with search results for a keyword. Click on a table row should open an Internet browser and a web 
page associated with the search result.
- next serch should clear the table
- each row should show YouTube or Yahoo! icon, depending on corresponding query reply
- search results should to be saved in SQLite database (keywords, URLs, headings, and name of a search system replied to a query).
- the last keywords input by a user should be saved in the user profile (user defaults) and automatically displayed in the search field when search beeing run next time
____________________________________
Also added some additional features.

## License

* [Apache Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
