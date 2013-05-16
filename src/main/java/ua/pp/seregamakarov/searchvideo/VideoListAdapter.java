package ua.pp.seregamakarov.searchvideo;

import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;

public class VideoListAdapter extends SimpleCursorAdapter {

    public VideoListAdapter(Context context, int layout, Cursor c,
            String[] from, int[] to) {
        super(context, layout, c, from, to);
    }
    
    @Override
    public void setViewImage(ImageView v, String value) {
        if (value.equalsIgnoreCase("youtube")) {
            v.setImageResource(R.drawable.ic_youtube);
        } else if (value.equalsIgnoreCase("yahoo")) {
            v.setImageResource(R.drawable.ic_yahoo);
        } else {
            v.setImageResource(R.drawable.ic_unknown);
        }
    }

}
