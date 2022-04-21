/*
 * http://www.mangahere.com/
 * http://www.mangahere.com/search.php?direction=&name_method=cw&name=san&author_method=cw&author=&artist_method=cw&artist=&genres[Action]=0&genres[Adventure]=0&genres[Comedy]=0&genres[Doujinshi]=0&genres[Drama]=0&genres[Ecchi]=0&genres[Fantasy]=0&genres[Gender+Bender]=0&genres[Harem]=0&genres[Historical]=0&genres[Horror]=0&genres[Josei]=0&genres[Martial+Arts]=0&genres[Mature]=0&genres[Mecha]=0&genres[Mystery]=0&genres[One+Shot]=0&genres[Psychological]=0&genres[Romance]=0&genres[School+Life]=0&genres[Sci-fi]=0&genres[Seinen]=0&genres[Shoujo]=0&genres[Shoujo+Ai]=0&genres[Shounen]=0&genres[Shounen+Ai]=0&genres[Slice+of+Life]=0&genres[Sports]=0&genres[Supernatural]=0&genres[Tragedy]=0&released_method=eq&released=&is_completed=&advopts=1
 * 
 */
package com.maxistar.mangabrowser.adapters;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;

public class MangaHere extends BaseSearchAdapter {

    void init(){
        server_address = "http://www.mangahere.cc";
        name = "MangaHere";
        settings_key = "source_mangahere";
        language = "en";
    }

    public SearchResult search(String word, int page) {
        //http://www.mangaeden.com/en-directory/?title=Bleach&author=&artist=&releasedType=0&released=
        SearchResult result = new SearchResult();
        try {

            String results = this.getGetData("http://www.mangahere.cc/search?title="+word+"");

            int pos = results.indexOf("<div class=\"manga-list-4 mt15\">");
            results = results.substring(pos);

            Pattern p = Pattern
            .compile("<a href=\"([^\"]+)\" title=\"([^\"]+)\">[^<]+");

            Matcher m = p.matcher(results);


            while (m.find()) {

                MangaItem item = new MangaItem(
                        m.group(2),
                        m.group(1),
                        0,
                        TYPE_MANGA_HERE
                );
                item.thumnail_url = "";
                result.addItem(item);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    public ArrayList<VolumeItem> getVolumes(MangaItem item) {
        ArrayList<VolumeItem> result = new ArrayList<VolumeItem>();
        try {
            String results = this.getGetData(item.url);

            int pos = results.indexOf("<div class=\"detail_list\">");
            results = results.substring(pos);

            pos = results.indexOf("<ul class=\"tab_comment clearfix\">");
            results = results.substring(0, pos);

            Pattern p = Pattern
                    .compile("<a[^>]+href=\"([^\"]+)\" >([^<]*)</a>\\s*<span class=\"mr6\"></span>([^<]*)</span>");
            Matcher m = p.matcher(results);


            while (m.find()) {
                VolumeItem item1 = new VolumeItem(m.group(2).trim()+" : "+m.group(3).trim(), m.group(1),
                        TYPE_MANGA_HERE);
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
            int pos = page.indexOf("<select class=\"wid60\" onchange=\"change_page(this)\">");
            page = page.substring(pos);

            pos = page.indexOf("</select>");
            page = page.substring(0, pos);

            Pattern p = Pattern.compile("<option[^>]*value=\"([^\"]+)\"[^>]*>");
            Matcher m = p.matcher(page);
            int num = 1;
            ArrayList<String> list = new ArrayList<String>();
            while (m.find()) {
                list.add(m.group(1));
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
                    .compile("<img[^<]+src=\"([^\"]+)\"[^<]+id=\"image\"[^<]+>");
            Matcher m = p.matcher(results);
            if (m.find()) {
                // download file
                downloadFile(m.group(1), num, foldername);
                return true;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }*/

    @Override
    public ArrayList<String> getImageUrls(VolumeItem item) {
        ArrayList<String> result = new ArrayList<String>();
        try {
            String page;
            page = this.getGetData(item.url);
            int pos = page.indexOf("<select class=\"wid60\" onchange=\"change_page(this)\">");
            page = page.substring(pos);

            pos = page.indexOf("</select>");
            page = page.substring(0, pos);

            Pattern p = Pattern.compile("<option[^>]*value=\"([^\"]+)\"[^>]*>");
            Matcher m = p.matcher(page);
            //int num = 1;
            ArrayList<String> list = new ArrayList<String>();
            while (m.find()) {
                list.add(m.group(1));
            }

            for (String addr : list) {
                String results = this.getGetData(addr);
                Pattern p2 = Pattern
                        .compile("<img[^<]+src=\"([^\"]+)\"[^<]+id=\"image\"[^<]+>");
                Matcher m2 = p2.matcher(results);
                if (m2.find()) {
                    result.add(m2.group(1));
                    // download file
                    //downloadFile(m.group(1), num, foldername);
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
