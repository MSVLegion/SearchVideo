package ua.pp.seregamakarov.searchvideo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Хьюстон
 * Date: 01.04.13
 * Time: 18:35
 * To change this template use File | Settings | File Templates.
 */
public class YoutubeJsonResult {
    Feed feed;

    static class Feed {
        List<Entry> entry;

        static class Entry {
            Title title;
            List<Link> link;

            static class Title {
                public String $t;
            }

            static class Link {
                public String rel;
                public String href;
            }
        }
    }
}
