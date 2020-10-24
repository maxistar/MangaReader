package com.maxistar.mangabrowser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;

public class SearchActivity extends ListActivity {
	
	private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPES_COUNT = 2;
	
    
	private MyCustomAdapter mAdapter;
    private ArrayList<ListItem> mData = new ArrayList<ListItem>();

    protected ImageLoader imageloader;
    
    EditText text;
    TextView empty_text;
    static SearchTask task = null;
    Button button;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		imageloader = new ImageLoader(this.getApplicationContext());
		mAdapter = new MyCustomAdapter();
		this.setListAdapter(mAdapter);
		
		
		
		button = (Button)this.findViewById(R.id.button1);
		button.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				doSearch();
			}});
		
		text = (EditText)this.findViewById(R.id.editText1);
		empty_text = (TextView)this.findViewById(R.id.status_text);
		
		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListItem item = mData.get(position);
				if (item.type==TYPE_ITEM){
					MangaItem sri = item.item;
					Intent intent = new Intent(SearchActivity.this,
						VolumesActivity.class);
					intent.putExtra(MStrings.MANGA, sri);
					startActivity(intent);
				}
			}
		});
		this.registerForContextMenu(lv);
		
		if (savedInstanceState!=null){
        	restoreState(savedInstanceState);
        } else {
        	initState();
        }

		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT
		); // You might want to tweak these to WRAP_CONTENT
	    lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
	}
	
	void restoreState(Bundle state) {
        String term = state.getString("term");
        text.setText(term);
        mData = (ArrayList<ListItem>) state.getSerializable("items");
        if (task!=null){
            empty_text.setText(l(R.string.Loading_));
            button.setEnabled(false);
            text.setEnabled(false);
        } else {
        	empty_text.setText(l(R.string.List_is_empty));
        	button.setEnabled(true);
        	text.setEnabled(true);
        }
        mAdapter.notifyDataSetChanged();
    }
	
	@Override
	protected void onPause() {
		super.onPause();
		if (task != null) task.setOwner(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (task != null) task.setOwner(this);
	}


	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
    void initState() {
    	SearchCache cache = SearchCache.getCachedItems(getApplicationContext());
    	if (cache != null) {
    		mData = cache.items;
    		text.setText(cache.term);
    	} else {
    		mData = new ArrayList<ListItem>();
    	}
        mAdapter.notifyDataSetChanged();
    }
  
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String term = text.getText().toString();
        outState.putString("term", term);
        outState.putSerializable("items", mData);
    }
	
	protected void doSearch() {
		task = new SearchTask();
		task.setOwner(this);
		task.execute(text.getText().toString());
	}
	
	
	@Override
	public void onCreateContextMenu(
			ContextMenu menu,
			View v,
			ContextMenuInfo menuInfo
	) {
		getMenuInflater().inflate(R.menu.activity_search_context, menu);
	}	
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		MangaItem manga = this.mData.get(info.position).item;
		switch (item.getItemId()) {
		
		case R.id.menu_view_manga:
			
			Intent intent = new Intent(SearchActivity.this,
				VolumesActivity.class);
			intent.putExtra(MStrings.MANGA, manga);
			startActivity(intent);
			
			
			return true;
		
		}
		return super.onContextItemSelected(item);
	}
	

	void showToast(String msg) {
		Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_search, menu);
		return true;
	}
	*/
	
	private class MyCustomAdapter extends BaseAdapter {
		 
        private LayoutInflater mInflater;
 
        public MyCustomAdapter() {
            mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
 
        public void addItem(MangaItem item) {
			ListItem li = new ListItem();
			li.type = TYPE_ITEM;
			li.item = item;
            mData.add(li);
        }
        
        public void addSeparator(String name, String lang) {
			ListItem li = new ListItem();
			li.type = TYPE_SEPARATOR;
			li.name = name;
			li.lang = lang;
            mData.add(li);
        }
        
        @Override
        public int getItemViewType(int position) {
            return mData.get(position).type;
        }
 
        @Override
        public int getViewTypeCount() {
            return TYPES_COUNT;
        }
 
        @Override
        public int getCount() {
            return mData.size();
        }
 
        @Override
        public ListItem getItem(int position) {
            return mData.get(position);
        }
 
        @Override
        public long getItemId(int position) {
            return position;
        }
 
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            //ViewHolder holder = null;
            ListItem item = this.getItem(position);
            if (convertView == null) {
            	if (item.type == TYPE_ITEM){
            		convertView = mInflater.inflate(R.layout.search_item, null);
            	} else {
            		convertView = mInflater.inflate(R.layout.search_separator, null);
            	}
                //holder = new ViewHolder();
                //holder.textView = (TextView)convertView.findViewById(R.id.text);
                //convertView.setTag(holder);
            } else {
                //holder = (ViewHolder)convertView.getTag();
            }
            //holder.textView.setText(this.getItem(position));
        	if (item.type == TYPE_ITEM) {
        		MangaItem item1 = mData.get(position).item;
        		((TextView)convertView.findViewById(R.id.text)).setText(item1.name);
        		
        		ImageView view = (ImageView)convertView.findViewById(R.id.favorites);
        		convertView.setTag(item1); //just in order simpler access
        		if (MangaUtils.isItemFavorited(SearchActivity.this.getApplicationContext(),item1)) {
        			view.setImageResource(R.drawable.favorited);
        		} else {
        			view.setImageResource(R.drawable.unfavorited);
        		}
        		view.setTag(item1);
        		//MangaUtils.setPreviewImage((ImageView)convertView.findViewById(R.id.preview),item1.thumnail_url, this);
        		if (item1.thumnail_url!=null) {
        			imageloader.displayImage(item1.thumnail_url, (ImageView)convertView.findViewById(R.id.preview));
        		} else {
            		((ImageView)convertView.findViewById(R.id.preview)).setImageResource(R.drawable.mangaloading);
            	}
        		//view.setI
        		
        		
        		view.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						MangaItem item1 = (MangaItem)arg0.getTag();
						ImageView iv = (ImageView) arg0;
						if (MangaUtils.isItemFavorited(SearchActivity.this.getApplicationContext(),item1)){
							MangaUtils.removeFavorite(SearchActivity.this.getApplicationContext(),item1);
							iv.setImageResource(R.drawable.unfavorited);
						} else {
							MangaUtils.addFavorite(SearchActivity.this.getApplicationContext(),item1);
							iv.setImageResource(R.drawable.favorited);							
						}
					}
        		});
        	} else {
                ((TextView)convertView.findViewById(R.id.text)).setText(item.name);        		
                ((TextView)convertView.findViewById(R.id.lang)).setText(item.lang);        		
        	}
            return convertView;
        }
 
    }
 
    public static class ListItem implements Serializable  {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public MangaItem item;
        public int type;
        public String name;
        public String lang;
    }
    
    private static class SearchTask extends AsyncTask<String, String, String> {
    	SearchActivity owner = null;
    	
    	public void setOwner(SearchActivity owner){
    		this.owner = owner;
    	}
    	
        @Override
        protected String doInBackground(String... urls) {
        	String text = urls[0];
        	
        	List<BaseSearchAdapter> adapters = BaseSearchAdapter.getSearchAdapters(owner.getApplicationContext());
        	
        	for(BaseSearchAdapter adapter: adapters){
        		if (owner == null) continue;
        		SearchResult result = adapter.search(text,0);
        		if (null != result && result.items.size() > 0) {
        			owner.mAdapter.addSeparator(adapter.getName(), adapter.getLanguage());
        			Iterator<MangaItem> it = result.items.iterator();
        			while (it.hasNext()) {
        				MangaItem item = it.next();
        				owner.mAdapter.addItem(item);
        			}
        		}
        	}
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (owner == null) return;
            owner.empty_text.setText(owner.l(R.string.Loading_));
            owner.mData.clear();
            owner.text.setEnabled(false);
            owner.button.setEnabled(false);
            owner.mAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(String result) {
        	owner.mAdapter.notifyDataSetChanged();
        	if (owner == null) return;
            owner.empty_text.setText(owner.l(R.string.List_is_empty));
            owner.text.setEnabled(true);
            owner.button.setEnabled(true);
            SearchActivity.task = null; //we have done
            
            SearchCache.saveCache(
            		owner.mData,
					owner.text.getText().toString(),
					owner.getApplicationContext()
			);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }
    
    String l(int id) {
		return getApplicationContext().getResources().getString(id);
	}

    static class SearchCache implements Serializable {
    	private static final long serialVersionUID = 1L;
    	ArrayList<ListItem> items = null;
    	String term = "";
    	
    	static void saveCache(ArrayList<ListItem> mData, String term, Context context) {
        	SearchCache cache = new SearchCache();
        	cache.items = mData;
        	cache.term = term;
    		
    		String filename = "search";
    		FileOutputStream fos;
    		try {
    			fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
    			ObjectOutputStream objectOut = new ObjectOutputStream(fos);
    			objectOut.writeObject(cache);
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}

    	static SearchCache getCachedItems(Context context) {
    		String filename = "search";
    		try {
    			FileInputStream fis = context.getApplicationContext().openFileInput(filename);
    			ObjectInputStream objectIn = new ObjectInputStream(fis);
    			return (SearchCache) objectIn.readObject();
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (StreamCorruptedException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (ClassNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		return null;
    	}
    }
}
