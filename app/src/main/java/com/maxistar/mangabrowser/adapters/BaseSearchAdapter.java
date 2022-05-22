package com.maxistar.mangabrowser.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.maxistar.mangabrowser.MStrings;
import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.MangaUtils;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;

abstract public class BaseSearchAdapter {
    public static final int BUFFER_SIZE_8 = 8 * 1024;
    public static final int TYPE_MANGA_READER = 0;
    public static final int TYPE_MANGABLE = 1;
    public static final int TYPE_ADULT_MANGA = 2;
    public static final int TYPE_MANGA_EDEN = 3;
    public static final int TYPE_MANGA_HERE = 4;
    public static final int TYPE_READ_MANGA_ME = 5;

    protected String settings_key;
    protected String server_address;
    protected String name;
    protected String language;

    public BaseSearchAdapter() {
        this.init();
    }

    abstract void init();
    /**
     * Returns settings key
     */
    public String getSettingsKey(){
        return settings_key;
    };
    public String getName(){
        return name;
    }
    public String getServerAddress(){
        return server_address;
    }
    public String getLanguage(){
        return language;
    }

    OnDownloadProgressListener listener = null;

    abstract public SearchResult search(String word, int page);
    abstract public ArrayList<VolumeItem> getVolumes(MangaItem item);

    abstract public ArrayList<String> getImageUrls(VolumeItem item);

    public void downloadImages(VolumeItem item, String foldername) {
        int num = 1;
        ArrayList<String> list = this.getImageUrls(item);
        for (String addr: list) {
            try {
                downloadFile(addr, num, foldername);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (this.listener != null) {
                this.listener.notifyProgress(((float) num)
                        / ((float) list.size()));
            }
            num++;

        }
    }

    String getGetData(String address) throws Exception {
        if (address.indexOf("http") != 0 && address.indexOf("//") != 0) {
            address = this.server_address + address;
        }

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(new URI(address));

        HttpResponse response = client.execute(request);
        InputStream is = response.getEntity().getContent();

        StringBuilder data = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), BUFFER_SIZE_8);
        String line = reader.readLine();
        while (line != null) {
            data.append(line);
            line = reader.readLine();
        }
        return data.toString();
    }

    String getPostData(
            String address,
            List<NameValuePair> postParams
    ) throws Exception {
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter("http.protocol.content-charset","UTF-8");

        HttpPost request = new HttpPost(new URI(address));

        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParams,"UTF-8");
        // entity.setContentEncoding(HTTP.UTF_8);
        // entity.setContentType("application/x-www-form-urlencoded");
        request.setEntity(entity);
        request.setHeader("Content-Type", "application/x-www-form-urlencoded");
        request.setHeader("Accept", "application/x-www-form-urlencoded");

        HttpResponse response = client.execute(request);
        InputStream is = response.getEntity().getContent();

        StringBuilder data = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), BUFFER_SIZE_8);
        String line = reader.readLine();
        while (line != null) {
            data.append(line);
            line = reader.readLine();
        }
        return data.toString();
    }



    public void setOnDownloadProgressListner(OnDownloadProgressListener listener){
        this.listener = listener;
    }

    public interface OnDownloadProgressListener {
        void notifyProgress(float progress);
    }

    /**
     * returns available search adapters
     * @return
     */
    static public List<BaseSearchAdapter> getSearchAdapters(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        ArrayList<BaseSearchAdapter> list = new ArrayList<BaseSearchAdapter>();
        List<BaseSearchAdapter> all = BaseSearchAdapter.getAvailableAdapters();
        for (BaseSearchAdapter adapter: all) {
            boolean used = sharedPreferences.getBoolean(adapter.getSettingsKey(), true);
            if (used) {
                list.add(adapter);
            }
        }
        return list;
    }

    static public String getFolderName(int type) {
        switch (type) {
            case BaseSearchAdapter.TYPE_MANGA_READER:
                return MStrings.MANGAREADER;
            case BaseSearchAdapter.TYPE_MANGABLE:
                return MStrings.MANGABLE;
            case BaseSearchAdapter.TYPE_ADULT_MANGA:
                return MStrings.ADULTMANGA;
            case BaseSearchAdapter.TYPE_MANGA_EDEN:
                return MStrings.MANGAEDEN;
            case BaseSearchAdapter.TYPE_MANGA_HERE:
                return MStrings.MANGAHERE;
            case BaseSearchAdapter.TYPE_READ_MANGA_ME:
                return MStrings.READMANGAME;
            default:
                return MStrings.UNKNOWN;
        }
    }

    /**
     * returns all search adapters
     * @return
     */
    static public List<BaseSearchAdapter> getAvailableAdapters() {
        ArrayList<BaseSearchAdapter> list = new ArrayList<BaseSearchAdapter>();
        list.add(new MangaReader());
        list.add(new Mangable());
        list.add(new AdultManga());
        list.add(new MangaEden());
        list.add(new MangaHere());
        list.add(new ReadMangaMe());
        return list;
    }

    /**
     * returns adapter by code
     * @return
     */
    static public BaseSearchAdapter getSearchAdapter(int code) {
        if (code == BaseSearchAdapter.TYPE_MANGA_READER) {
            return new MangaReader();
        }
        if (code == BaseSearchAdapter.TYPE_MANGABLE) {
            return new Mangable();
        }
        if (code == BaseSearchAdapter.TYPE_ADULT_MANGA) {
            return new AdultManga();
        }
        if (code == BaseSearchAdapter.TYPE_MANGA_EDEN) {
            return new MangaEden();
        }
        if (code == BaseSearchAdapter.TYPE_MANGA_HERE) {
            return new MangaHere();
        }
        if (code == BaseSearchAdapter.TYPE_READ_MANGA_ME) {
            return new ReadMangaMe();
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    void downloadFile(String url, int num, String foldername) throws Exception {
        if (url.indexOf("http") != 0 && url.indexOf("//") != 0) {
            url = this.server_address + url;
        }

        int try_num = 10; // count of tries
        do {
            String filename = String.format("%s/%06d.jpg", foldername, num);
            File f = new File(filename);
            if (!f.exists()) {
                f.delete();
                f.createNewFile();
            }
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            MangaUtils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            if (f.length() > 0)	break;
            try_num--;
        } while (try_num > 0);
    }

    /*
    void downloadFile(String url, int num, String foldername) throws Exception {
        int try_num = 10; // count of tries
        do {
            String filename = String.format("%s/%06d.jpg", foldername, num);
            File f = new File(filename);
            if (!f.exists()) {
                f.delete();
                f.createNewFile();
            }
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imageUrl
                    .openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            MangaUtils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            if (f.length() > 0)	break;
            try_num--;
        } while (try_num > 0);
    }
    */
}
