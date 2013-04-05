package ua.pp.seregamakarov.searchvideo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Хьюстон
 * Date: 28.03.13
 * Time: 22:26
 * To change this template use File | Settings | File Templates.
 */
public class BingJsonResult {
    D d;

    static class D {
        List<R> results;

        static class R {
            public String MediaUrl;
            public String Title;

            public String toString() {
                return "\n" + Title + "\n" + MediaUrl + "\n";
            }
        }
    }

    public String toString() {
        return d.results.toString();
    }
}
