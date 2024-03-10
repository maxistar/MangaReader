package com.maxistar.mangabrowser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.documentfile.provider.DocumentFile;

public class VolumeActivity extends Activity implements MangaLoader.OnProgressUpdateListener {

    VolumeItem item;
    int item_id = 0;
    int totalVolumes; //total volumes in manga
    MangaItem manga;
    ArrayList <DocumentFile> files;
    ImagePager imagePager;
    VolumesCache cache;
    RelativeLayout navigationBar;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        } else {
            initState();
        }

        // cache = VolumesCache.getCachedItems(manga, getApplicationContext());
        totalVolumes = 1;//cache.items.size();
        //item = cache.items.get(item_id);

        setContentView(R.layout.activity_volume);


        imagePager = (ImagePager) findViewById(R.id.view_pager);
        this.navigationBar = (RelativeLayout) findViewById(R.id.navigation_bar);
        progress = (ProgressBar) findViewById(R.id.progressBar1);
        progress.setMax(100);



        View empty = (View) findViewById(android.R.id.empty);
        // if(MangaLoader.getVolumeStatus(manga, item) == MangaLoader.MANGA_LOADED){
            empty.setVisibility(View.GONE);
            initBitmaps();
        //} else {
        //    imagePager.setVisibility(View.GONE);
        //    downloadImages();
        //    VolumesActivity.update_flag = true;
        //    initAds(); //do not show ads if images are loaded already
        //    this.hideNavigationBar();
        //}

        setupNavigationButtons();
    }

    void setupNavigationButtons() {
        Button button = (Button) this.findViewById(R.id.previous_vol);
        if (item_id == this.totalVolumes - 1) {
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readPrevious();
            }
        });

        button = (Button) this.findViewById(R.id.read_again);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readAgain();
            }
        });

        button = (Button) this.findViewById(R.id.next_vol);
        if (item_id == 0) {
            button.setVisibility(View.GONE);
        }
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                readNext();
            }
        });
    }

    void readNext() {
        Intent intent = new Intent(this,
                VolumeActivity.class);
        intent.putExtra(MStrings.ITEM, item_id - 1);
        intent.putExtra(MStrings.MANGA, this.manga);
        startActivity(intent);
    }

    void readAgain() {
        Intent intent = new Intent(this,
                VolumeActivity.class);
        item.page_num = 0;
        this.saveCache();
        intent.putExtra(MStrings.ITEM, item_id);
        intent.putExtra(MStrings.MANGA, this.manga);
        startActivity(intent);
    }

    void readPrevious() {
        Intent intent = new Intent(this,
                VolumeActivity.class);
        intent.putExtra(MStrings.ITEM, item_id + 1);
        intent.putExtra(MStrings.MANGA, this.manga);
        startActivity(intent);
    }

    void onPageShown(int page_num){
        checkNavigationVisibility();
    }

    void checkNavigationVisibility() {
        if (this.files.size() == this.item.page_num + 1) {
            this.showNavigationBar();
        } else {
            this.hideNavigationBar();
        }
    }

    void showNavigationBar(){
        this.navigationBar.setVisibility(View.VISIBLE);
    }

    void hideNavigationBar(){
        this.navigationBar.setVisibility(View.GONE);
    }

    void initAds() {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // You might want to tweak these to WRAP_CONTENT
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
    }

    void restoreState(Bundle state) {
        item_id = state.getInt("item_id");
        manga = (MangaItem)state.getSerializable("manga");
        item = (VolumeItem) state.getSerializable(MStrings.VOLUME);
    }
    
    void initState() {
        item_id = this.getIntent().getExtras().getInt(MStrings.ITEM);
        manga = (MangaItem)this.getIntent().getExtras().getSerializable(MStrings.MANGA);
        item = (VolumeItem) this.getIntent().getExtras().getSerializable(MStrings.VOLUME);
    }
  
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("manga", manga);
        outState.putInt("item_id", item_id);
    }

    void initBitmaps(){
        /*files = new ArrayList<File>();
        String foldername = Environment.getExternalStorageDirectory()
                + MStrings.SLASH + MStrings.MANGABROWSER
                + MStrings.SLASH
                + MangaUtils.getFolderName(manga.manga_type) + MStrings.SLASH
                + manga.getFolderName() + MStrings.SLASH
                + item.getFolderName();
        File dir = new File(foldername);

        if (!dir.exists()) {
            showToast(l(R.string.Network_Error));
            return;
        }*/

        files = new ArrayList<DocumentFile>();

        DocumentFile documentsTree = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(item.url));
        if (documentsTree != null) {
            DocumentFile[] childDocuments = documentsTree.listFiles();

            for(DocumentFile file: childDocuments) {
                // MangaItem item = new MangaItem(file.getName(), file.getUri().toString(), 0, 0);
                files.add(file);
            }
        }



        ImagePager viewPager = (ImagePager) findViewById(R.id.view_pager);
        viewPager.setVisibility(View.VISIBLE);

        /*
        Collections.addAll(files, dir.listFiles());
        Collections.sort(files,new Comparator<File>(){
            @Override
            public int compare(File lhs, File rhs) {
                // TODO Auto-generated method stub
                return lhs.getName().compareTo(rhs.getName());
            }}); */
        imagePager.setFiles(files, item, this);

        checkNavigationVisibility();
    }

    protected void showToast(String toast_str) {
        Context context = getApplicationContext();
        CharSequence text = toast_str;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void saveCache(){
        item.read_flag = true;
        VolumesCache.saveCache(manga, cache.items, this.getApplicationContext());
    }

    void downloadImages(){
        MangaLoader.setProgressListener(this);
        MangaLoader.downloadManga(manga, item);
    }

    @Override
    public void onDestroy(){
        MangaLoader.removeProgressListener(this);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_volume, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu){
        if (MangaUtils.isItemFavorited(getApplicationContext(), manga)) {
            menu.findItem(R.id.add_to_favorites).setVisible(false);
            menu.findItem(R.id.remove_from_favorites).setVisible(true);
        } else {
            menu.findItem(R.id.add_to_favorites).setVisible(true);
            menu.findItem(R.id.remove_from_favorites).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh_images:
            downloadImages();
            VolumesActivity.update_flag = true;
            ImagePager viewPager = (ImagePager) findViewById(R.id.view_pager);
            View empty = (View) findViewById(android.R.id.empty);
            viewPager.setVisibility(View.GONE);
            empty.setVisibility(View.VISIBLE);
            return true;
        case R.id.add_to_favorites:
            MangaUtils.addFavorite(getApplicationContext(), manga);
            return true;
        case R.id.remove_from_favorites:
            MangaUtils.removeFavorite(getApplicationContext(), manga);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onProgressUpdate(VolumeItem item, float progress) {
        // TODO Auto-generated method stub
        if (!item.equals(this.item)) {
            return;
        }
        this.progress.setProgress((int)(100*progress));
    }

    @Override
    public void onDownloadComplete(VolumeItem item) {
        // TODO Auto-generated method stub

        if (!item.equals(this.item)) return;
        MangaLoader.setProgressListener(null);
        //ImagePager viewPager = (ImagePager) findViewById(R.id.view_pager);
        View empty = (View) findViewById(android.R.id.empty);

        empty.setVisibility(View.GONE);

        initBitmaps();

    }

    String l(int id) {
        return getApplicationContext().getResources().getString(id);
    }

    @Override
    public void onDownloadStarted(VolumeItem item) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,
            VolumesActivity.class);
        intent.putExtra(MStrings.MANGA, this.manga);
        startActivity(intent);
    }
}
