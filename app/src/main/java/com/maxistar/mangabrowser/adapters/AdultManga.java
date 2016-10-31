/**
 * search form located on 
 * 
 * 
 * genres for browsing/ search
 * 
 * Action
Adventure
Comedy
Demons
Drama
Ecchi
Fantasy
Gender Bender
Harem
Historical
Horror
Josei
Magic
Martial Arts
Mature
Mecha
Military
Mystery
One Shot
Psychological
Romance
School Life
Sci-Fi
Seinen
Shoujo
Shoujoai
Shounen
Shounenai
Slice of Life
Smut
Sports
Super Power
Supernatural
Tragedy
Vampire
Yaoi
Yuri

 * 
 * 
 */

package com.maxistar.mangabrowser.adapters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;

public class AdultManga extends BaseSearchAdapter {
	
	void init(){
		server_address = "http://adultmanga.ru";
		settings_key = "source_adult_manga";
		name = "Adult Manga";
		language = "ru";	
	}
	
	public SearchResult search(String word, int page) {
		/*
		
		*/

		try {
			List<NameValuePair> postParams = new ArrayList<NameValuePair>();
			postParams.add(new BasicNameValuePair("q", word));
			
			String results = this
					.getPostData("http://adultmanga.ru/search/",
					postParams);
			
			int pos = results.indexOf("<div id=\"mangaResults\">");
			results = results.substring(pos);
			
			
//			pos = results.indexOf("</table>");
//			results = results.substring(0, pos);
			

			Pattern p = Pattern
			.compile("<a\\s*href=\"([^\"]+)\"\\s*[^>]*>[^<]*<img[^>]+src=\"([^\"]+)\"[^>]*title=\"([^\"]+)\"[^>]*");

			Matcher m = p.matcher(results);

			SearchResult result = new SearchResult();

			while (m.find()) {
				MangaItem item = new MangaItem(m.group(3),
						m.group(1), 0, TYPE_ADULT_MANGA);
				item.thumnail_url = m.group(2);//m.group(1);
				result.addItem(item);
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
			int pos = results.indexOf("<div class=\"expandable chapters-link\" data-height=\"800\">");
			results = results.substring(pos);

			
			
			pos = results.indexOf("</table>");
			results = results.substring(0,pos);

			
			//<div class="expandable chapters-link" data-height="800">
			
			Pattern p = Pattern
					.compile("<a href=\"([^\"]+)\"[^<]*>([^<]+)</a>");
			Matcher m = p.matcher(results);

			//Pattern p1 = Pattern.compile("\\s+");

			
			while (m.find()) {
				String title = m.group(2).trim();
				//title = title.replace("\n", " ");
				//title = title.replace("\r", " ");
				//title = title.replace("\t", " ");
				title = title.replaceAll("[\\s]+", " ");
				//p1.
				
				VolumeItem item1 = new VolumeItem(title,
						this.server_address + m.group(1), TYPE_ADULT_MANGA);
				result.add(item1);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	/*
	public void downloadImages(VolumeItem item, String foldername){
		
		String page;
		try {
			page = this.getGetData(server_address + item.url);
			
			//
			if (page.indexOf("нажмите сюда, чтобы продолжить чтение")!=-1){
				page = this.getGetData(server_address + item.url + "?mature=1");				
			}
			
			int pos = page.indexOf("var pictures = ");
			page = page.substring(pos+"var pictures = ".length());

			pos = page.indexOf("var prevLink");
			page = page.substring(0, pos-3);
			
			JSONArray result = new JSONArray(page);
			
			int num = 1;
			
			for(int i=0;i<result.length();i++){
				JSONObject obj = result.getJSONObject(i);
				downloadImage(obj.getString("url"), num, foldername);
				if (this.listener!=null){ 
					this.listener.notifyProgress(((float)num)/((float)result.length()));
				}
				num++;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	/*
	boolean downloadImage(String url, int num, String foldername){
		try {
				downloadFile(url, num, foldername);
				return true;
	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}*/
	
	/*
	void downloadFile(String url,int num,String foldername) throws Exception{
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
		String page;
		try {
			page = this.getGetData(item.url);
			if (page.indexOf("нажмите сюда, чтобы продолжить чтение")!=-1){
				page = this.getGetData(item.url + "?mature=1");				
			}
			int pos = page.indexOf("var pictures = ");
			page = page.substring(pos+"var pictures = ".length());
			pos = page.indexOf("var prevLink");
			page = page.substring(0, pos-3);
			
			JSONArray result1 = new JSONArray(page);
			for(int i=0;i<result1.length();i++){
				JSONObject obj = result1.getJSONObject(i);
				result.add(obj.getString("url"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
