/**
 * http://mangable.com
 * http://mangable.com/search/?series_contains=1&series_name=san&artist_contains=1&artist_name=&author_contains=1&author_name=&year_when=1&year=&rating_on=1&rating=x&completed=1&sort=asc&orderby=name&page=1
 * pagination by 40
 * ganres: 
 * 
 * 
 */

package com.maxistar.mangabrowser.adapters;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;

public class Mangable extends BaseSearchAdapter {

    void init() {
        server_address = "http://mangable.com";
        settings_key = "source_mangable";
        name = "Mangable";
        language = "en";
    }

    /**
     * get request to
     * http://mangable.com/search/?series_contains=1&series_name=san
     * &artist_contains
     * =1&artist_name=&author_contains=1&author_name=&year_when=1
     * &year=&rating_on=1&rating=x&completed=1&sort=asc&orderby=name&page=1
     * returns null if error happened
     */
    public SearchResult search(String word, int page) {
        try {
            String results = this
                    .getGetData("http://mangable.com/search/?series_contains=1&series_name="
                            + URLEncoder.encode(word, "ISO-8859-1")
                            + "&artist_contains=1&artist_name=&author_contains=1&author_name=&year_when=1&year=&rating_on=1&rating=x&completed=1&sort=asc&orderby=name&page="
                            + (page + 1));

            Pattern p = Pattern
                    .compile("<a href=\"([^\"]+)\" class=\"(ongoing|complete)\">([^<]+)</a>");
            Matcher m = p.matcher(results);

            SearchResult result = new SearchResult();

            while (m.find()) {
                MangaItem item = new MangaItem(m.group(3), m.group(1), 0,
                        TYPE_MANGABLE);
                String url = m.group(1);
                item.thumnail_url = "http://mangable.com/files/images/logos/"
                        + url.substring(1, url.length() - 1) + ".jpg";
                result.addItem(item);

                // make image link
                // http://mangable.com/angel_sanctuary/
                // http://mangable.com/files/images/logos/angel_sanctuary.jpg

            }

            return result;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<VolumeItem> getVolumes(MangaItem item) {
        ArrayList<VolumeItem> result = new ArrayList<VolumeItem>();

        try {
            String results = this.getGetData(server_address + item.url);

            Pattern p = Pattern
                    .compile("<a href=\"([^\"]+)\" class=\"ongoing\">\\s*<p class=\"audi\">\\s*<b>([^<]+)</b>");
            Matcher m = p.matcher(results);


            while (m.find()) {
                VolumeItem item1 = new VolumeItem(
                        m.group(2),
                        m.group(1),
                        TYPE_MANGABLE
                );
                result.add(item1);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;

    }

    /*
    public void downloadImages(VolumeItem item, String foldername) {
        try {
            String page;
            page = this.getGetData(item.url);
            int pos = page.indexOf("<div id=\"select_page\">");
            page = page.substring(pos);

            pos = page.indexOf("</select>");
            page = page.substring(0, pos);

            Pattern p = Pattern.compile("<option[^>]*value=\"([^\"]+)\"[^>]*>");
            Matcher m = p.matcher(page);
            int num = 1;
            ArrayList<String> list = new ArrayList<String>();
            while (m.find()) {
                list.add(item.url + m.group(1) + "/");
            }

            for (String addr : list) {
                downloadImage(addr, num, foldername);
                if (this.listener != null) {
                    this.listener.notifyProgress(((float) num)
                            / ((float) list.size()));
                }
                num++;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }*/

    /*
    boolean downloadImage(String url, int num, String foldername) {
        try {
            String results = this.getGetData(url);
            Pattern p = Pattern
                    .compile("<a href=\"([^\"]+)\"><img src=\"([^\"]+)\" id=\"image\"></a>");
            Matcher m = p.matcher(results);
            if (m.find()) {
                // download file
                downloadFile(m.group(2), num, foldername);
                return true;
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }
    */
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
    }*/

    @Override
    public ArrayList<String> getImageUrls(VolumeItem item) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            String page;
            page = this.getGetData(item.url);
            int pos = page.indexOf("<div id=\"select_page\">");
            page = page.substring(pos);

            pos = page.indexOf("</select>");
            page = page.substring(0, pos);

            Pattern p = Pattern.compile("<option[^>]*value=\"([^\"]+)\"[^>]*>");
            Matcher m = p.matcher(page);
            //int num = 1;
            ArrayList<String> list = new ArrayList<String>();
            while (m.find()) {
                list.add(item.url + m.group(1) + "/");
            }

            for (String addr : list) {
                String results = this.getGetData(addr);
                Pattern p2 = Pattern
                        .compile("<a href=\"([^\"]+)\"><img src=\"([^\"]+)\" id=\"image\"></a>");
                Matcher m2 = p2.matcher(results);
                if (m2.find()) {
                    result.add(m2.group(2));
                    // download file
                    //downloadFile(m2.group(2), num, foldername);
                    //return true;
                }
                //downloadImage(addr, num, foldername);
                //if (this.listener != null) {
                //	this.listener.notifyProgress(((float) num)
                //			/ ((float) list.size()));
                //}
                //num++;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
}
