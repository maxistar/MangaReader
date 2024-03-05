package com.maxistar.mangabrowser;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class FavoritesActivity extends ListActivity {

    private FavoritesAdapter mAdapter;
    private List <MangaItem> mData;
    protected ImageLoader imageloader;

    static Uri filesUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        imageloader = new ImageLoader(this.getApplicationContext());
        //mData = MangaUtils.getFavoritesListNew(filesUri, this.getApplicationContext());
        mData = new ArrayList<MangaItem>();
        mAdapter = new FavoritesAdapter();
        this.setListAdapter(mAdapter);

        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                    MangaItem sri = mData.get(position);
                    showManga(sri);
            }
        });
        this.registerForContextMenu(lv);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // You might want to tweak these to WRAP_CONTENT
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    }

    void showManga(MangaItem item){
        Intent intent = new Intent(FavoritesActivity.this,
                VolumesActivity.class);
        intent.putExtra(MStrings.MANGA, item);
        startActivity(intent);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.activity_favorites_context, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        MangaItem item1 = mData.get(info.position);
        switch (item.getItemId()) {
        case R.id.menu_remove_from_favorites:
            this.removeFromFavorites(item1);
        break;
        case R.id.menu_view_volume:
            this.showManga(item1);
        break;
        default:
        }
        return true;
    }


    void removeFromFavorites(MangaItem item){
        MangaUtils.removeFavorite(this.getApplicationContext(), item);
        mData = MangaUtils.getFavoritesList(this.getApplicationContext());
        mAdapter.notifyDataSetChanged();
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_favorites, menu);
        return true;
    }*/

    private class FavoritesAdapter extends BaseAdapter {

        private LayoutInflater mInflater;
 
        public FavoritesAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        
        @Override
        public int getCount() {
            return mData.size();
        }
 
        @Override
        public MangaItem getItem(int position) {
            return mData.get(position);
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.search_item, null);
            }
            ((TextView)convertView.findViewById(R.id.text)).setText(mData.get(position).name);
            
            if (mData.get(position).thumnail_url!=null){
                imageloader.displayImage(mData.get(position).thumnail_url, (ImageView)convertView.findViewById(R.id.preview));
            } else {
                ((ImageView)convertView.findViewById(R.id.preview)).setImageResource(R.drawable.mangaloading);
            }

            return convertView;
        } 
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /** Called before the activity is destroyed. */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
