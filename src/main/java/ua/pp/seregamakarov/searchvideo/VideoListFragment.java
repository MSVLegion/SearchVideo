package ua.pp.seregamakarov.searchvideo;

import android.support.v4.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class VideoListFragment extends ListFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.videolist_fragment, null);
    }
    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((SearchVideoActivity)getActivity()).getVideoURL(id)));
          startActivity(browseIntent);
        /*if ((((SearchVideoActivity)getActivity()).searchCount)>0) {
            Toast toast = Toast.makeText(getActivity(), getResources().getString(R.string.wait_or_stop), Toast.LENGTH_LONG);
            toast.show();
        } else {
            Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(((SearchVideoActivity)getActivity()).getVideoURL(id)));
                startActivity(browseIntent);
        }*/
    }
}
