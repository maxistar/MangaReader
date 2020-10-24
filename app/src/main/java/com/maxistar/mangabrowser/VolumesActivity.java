package com.maxistar.mangabrowser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;

public class VolumesActivity extends ListActivity implements
		MangaLoader.OnProgressUpdateListener {
	MangaItem item;

	static boolean update_flag = false;

	private VolumesAdapter mAdapter;
	private ArrayList<VolumeItem> mData;

	private Map<String, ImageView> itemViews = new HashMap<String, ImageView>();
	private Map<ImageView, String> viewItems = new HashMap<ImageView, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		item = (MangaItem) this.getIntent().getExtras()
				.getSerializable(MStrings.MANGA);
		setContentView(R.layout.activity_volumes);

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//VolumeItem item1 = mData.get(position);

				Intent intent = new Intent(
						VolumesActivity.this,
						VolumeActivity.class
				);
				intent.putExtra(MStrings.ITEM, position);
				intent.putExtra(MStrings.MANGA, item);
				startActivity(intent);
			}
		});
		this.registerForContextMenu(lv);

		MangaLoader.setProgressListener(this);
	}
	
	void copyCachedDataToList() {
		VolumesCache cache = VolumesCache.getCachedItems(
				item,
				getApplicationContext()
		);
		if (cache == null) return; //nothing to copy
		TreeMap<String, VolumeItem> items_by_url = new TreeMap<String, VolumeItem>();
		for (VolumeItem item: cache.items) {
			items_by_url.put(item.url, item);
		}
		for (VolumeItem item: mData) {
			if (!items_by_url.containsKey(item.url)) continue; //nothing to copy
			VolumeItem old_item = items_by_url.get(item.url);
			item.read_flag = old_item.read_flag;
			item.page_num = old_item.page_num;
		}
		//good job!
	}

	void saveCache() {
		VolumesCache.saveCache(item, mData, getApplicationContext());
	}

	@Override
	protected void onResume() {
		super.onResume();

		VolumesCache cache = VolumesCache.getCachedItems(
				item,
				getApplicationContext()
		);
		if (cache == null || (cache != null && cache.info == null)) {
			mData = new ArrayList<VolumeItem>();
			mAdapter = new VolumesAdapter();
			this.setListAdapter(mAdapter);
			LoadTask task = new LoadTask();
			task.execute();
		} else {
			mData = cache.items;
			mAdapter = new VolumesAdapter();
			this.setListAdapter(mAdapter);
		}

		this.mAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MangaLoader.removeProgressListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_volumes, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (MangaUtils.isItemFavorited(getApplicationContext(), item)) {
			menu.findItem(R.id.add_to_favorites).setVisible(false);
			menu.findItem(R.id.remove_from_favorites).setVisible(true);
		} else {
			menu.findItem(R.id.add_to_favorites).setVisible(true);
			menu.findItem(R.id.remove_from_favorites).setVisible(false);
		}

		if (this.mData.size() > 0) {
			menu.findItem(R.id.menu_download_all).setVisible(true);
		} else {
			menu.findItem(R.id.menu_download_all).setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh_volumes:
			mData.clear();
			mAdapter.notifyDataSetChanged();

			LoadTask task = new LoadTask();
			task.execute();
			return true;
		case R.id.add_to_favorites:
			MangaUtils.addFavorite(getApplicationContext(), this.item);
			return true;
		case R.id.remove_from_favorites:
			MangaUtils.removeFavorite(getApplicationContext(), this.item);
			return true;
		case R.id.menu_download_all:
			Iterator<VolumeItem> it = this.mData.iterator();
			while (it.hasNext()) {
				VolumeItem item1 = it.next();
				MangaLoader.downloadManga(this.item, item1);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCreateContextMenu(
			ContextMenu menu,
			View v,
			ContextMenuInfo menuInfo
	) {
		getMenuInflater().inflate(R.menu.activity_volumes_context, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item2) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item2
				.getMenuInfo();
		VolumeItem item1 = mData.get(info.position);

		switch (item2.getItemId()) {
			case R.id.menu_download_volume:
				MangaLoader.downloadManga(item, item1);
				updateImageForItem(item1);
				return true;
			case R.id.menu_mark_read:
				item1.read_flag = true;
				saveCache();
				this.mAdapter.notifyDataSetChanged();
				return true;
			case R.id.menu_mark_unread:
				item1.read_flag = false;
				saveCache();
				this.mAdapter.notifyDataSetChanged();
				return true;
		}
		return super.onContextItemSelected(item2);
	}

	void updateImageForItem(VolumeItem v) {
		if (!this.itemViews.containsKey(v.getUniqueKey())) {
			return;
		}
		ImageView imageView = this.itemViews.get(v.getUniqueKey());
		if (!this.viewItems.containsKey(imageView)) {
			return; // just to make sure
		}

		String v2 = this.viewItems.get(imageView);
		if (!v2.equals(v.getUniqueKey())) {
			return; // item recycled
		}

		// this.itemViews.get(item.get)
		this.displayImage(item, v, imageView);
	}

	private class VolumesAdapter extends BaseAdapter {

		private LayoutInflater mInflater;

		public VolumesAdapter() {
			mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public VolumeItem getItem(int position) {
			return mData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.volume_item, null);
			} else {
			}

			VolumeItem it = mData.get(position);
			TextView tv = ((TextView) convertView.findViewById(R.id.text));
			if (it.read_flag) {
				tv.setText(it.name);
			} else {
				tv.setText(Html.fromHtml("<b>" + it.name + "</b>"));
			}
			// tv.setW
			ImageView view = (ImageView) convertView
					.findViewById(R.id.favorites);

			displayImage(item, mData.get(position), view);

			return convertView;
		}

	}

	private void displayImage(
			MangaItem manga,
			VolumeItem volume,
			ImageView imageView
	) {
		itemViews.put(volume.getUniqueKey(), imageView);
		viewItems.put(imageView, volume.getUniqueKey());

		int status = MangaLoader.getVolumeStatus(manga, volume);
		if (status == MangaLoader.MANGA_LOADED) {
			imageView.setImageResource(R.drawable.stored);
		} else if (status == MangaLoader.MANGA_LOADING) {
			imageView.setImageResource(R.drawable.download);
		} else {
			imageView.setImageResource(R.drawable.globe);
		}
	}

	private class LoadTask extends AsyncTask<Void, String, String> {
		// ProgressBar pb;
		// ProgressBar pbar;

		@Override
		protected String doInBackground(Void... urls) {
			// parse
			BaseSearchAdapter adapter = BaseSearchAdapter
					.getSearchAdapter(item.manga_type);
			mData = adapter.getVolumes(item);
			
			return null;
		}

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			TextView text = (TextView) findViewById(android.R.id.empty);
			text.setText(l(R.string.Loading_));
			mData.clear();
			mAdapter.notifyDataSetChanged();

		}

		@Override
		protected void onPostExecute(String result) {
			TextView text = (TextView) findViewById(android.R.id.empty);
			text.setText(l(R.string.List_is_empty));
			mAdapter.notifyDataSetChanged();
			copyCachedDataToList();
			saveCache();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
		}
	}

	@Override
	public void onProgressUpdate(VolumeItem item, float progress) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDownloadComplete(VolumeItem v) {
		if (!this.itemViews.containsKey(v.getUniqueKey())) {
			return;
		}
		ImageView imageView = this.itemViews.get(v.getUniqueKey());
		if (!this.viewItems.containsKey(imageView)) {
			return; // just to make sure
		}

		String v2 = this.viewItems.get(imageView);
		if (!v2.equals(v.getUniqueKey())) {
			return; // item recycled
		}

		// this.itemViews.get(item.get)
		this.displayImage(item, v, imageView);
	}

	String l(int id) {
		return getApplicationContext().getResources().getString(id);
	}

	@Override
	public void onDownloadStarted(VolumeItem v) {
		if (!this.itemViews.containsKey(v.getUniqueKey())) {
			return;
		}
		ImageView imageView = this.itemViews.get(v.getUniqueKey());
		if (!this.viewItems.containsKey(imageView)) {
			return; // just to make sure
		}

		String v2 = this.viewItems.get(imageView);
		if (!v2.equals(v.getUniqueKey())) {
			return; // item recycled
		}

		// this.itemViews.get(item.get)
		this.displayImage(item, v, imageView);
	}
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this,
			MainActivity.class);
		startActivity(intent);
	}
}
