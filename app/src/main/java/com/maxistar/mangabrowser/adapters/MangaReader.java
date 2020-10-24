/**
 * search form located on 
 * http://www.mangareader.net/search/?w=San&rd=0&status=0&order=0&genre=0000000000000000000000000000000000000&p=0
 * http://www.mangareader.net/search/?w=San&rd=0&status=0&order=0&genre=0000000000000000000000000000000000000&p=0
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;

public class MangaReader extends BaseSearchAdapter {
	
	void init() {
		server_address = "http://www.mangareader.net";
		settings_key = "source_manga_reader";
		name = "Manga Reader";
		language = "en";
	}
	
	public SearchResult search(String word, int page) {
		/*
		
		*/
		SearchResult result = new SearchResult();

		try {
			String results = this
					.getGetData("http://www.mangareader.net/search/?w="
							+ URLEncoder.encode(word, "ISO-8859-1")
							+ "&rd=0&status=0&order=0&genre=0000000000000000000000000000000000000&p="
							+ page);
			
			int pos = results.indexOf("<div id=\"bodyalt\">");
			results = results.substring(pos);

			//check if there is Naruto, Bleach and Fairy Tail - means nothing found
			if (results.contains("Naruto") && results.contains("Bleach") && results.contains("Fairy Tail")) return result;

			Pattern p = Pattern
			.compile("<div\\s+class=\"imgsearchresults\"\\s+style=\"background-image:url\\('([^']+)'\\)\"></div>"+
					 "<div\\s+class=\"result_info c4\">\\s*<div\\s+class=\"manga_name\">\\s*<div>\\s*"+
					 "<h3><a\\s*href=\"([^\"]+)\">([^<]+)</a>");

			Matcher m = p.matcher(results);

			while (m.find()) {
				MangaItem item = new MangaItem(
						m.group(3),
						m.group(2),
						0,
						TYPE_MANGA_READER
				);
				item.thumnail_url = m.group(1);
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
			String results = this.getGetData(server_address + item.url);
			

			Pattern p = Pattern
					.compile("<td>\\s*<div class=\"chico_manga\"></div>\\s*<a href=\"([^\"]+)\">([^<]+)</a>([^<]+)</td>");
			Matcher m = p.matcher(results);

			while (m.find()) {
				
				VolumeItem item1 = new VolumeItem(m.group(2) + m.group(3),
						server_address + m.group(1), TYPE_MANGA_READER);
				result.add(item1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Collections.reverse(result);
		return result;
	}

	/*
	public void downloadImages(VolumeItem item, String foldername){
		
		String page;
		try {
			page = this.getGetData(server_address + item.url);
			int pos = page.indexOf("<select id=\"pageMenu\" name=\"pageMenu\">");
			page = page.substring(pos);

			pos = page.indexOf("</select>");
			page = page.substring(0, pos);
			
			Pattern p = Pattern
					.compile("<option[^>]*value=\"([^\"]+)\"[^>]*>");
			Matcher m = p.matcher(page);
			int num = 1;
			ArrayList<String> list = new ArrayList<String>();
			while (m.find()) {
				list.add(m.group(1));
			}
			 
			for(String addr: list){
				downloadImage(addr, num, foldername);
				if (this.listener!=null){ 
					this.listener.notifyProgress(((float)num)/((float)list.size()));
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
		//<div id="imgholder"><a href="([^\"]+)"><img id="img" width="[^\"]+" height="[^\"]+" src="([^\"]+)" alt="[^\"]+" name=\"img\" /></a>

		try {
			String results = this.getGetData(server_address + url);
			
   
			Pattern p = Pattern
					.compile("<a href=\"([^\"]+)\"><img id=\"img\" width=\"[^\"]+\" height=\"[^\"]+\" src=\"([^\"]+)\" alt=\"[^\"]+\" name=\"img\" /></a>");
			Matcher m = p.matcher(results);
			if (m.find()) {
				
				
				//download file
				downloadFile(m.group(2), num, foldername);
				
				return true;
			}

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
			int pos = page.indexOf("<select id=\"pageMenu\" name=\"pageMenu\">");
			page = page.substring(pos);

			pos = page.indexOf("</select>");
			page = page.substring(0, pos);
			
			Pattern p = Pattern
					.compile("<option[^>]*value=\"([^\"]+)\"[^>]*>");
			Matcher m = p.matcher(page);
			//int num = 1;
			ArrayList<String> list = new ArrayList<String>();
			while (m.find()) {
				list.add(m.group(1));
			}
			 
			for(String addr: list) {
				String results = this.getGetData(server_address + addr);
				
	   
				Pattern p2 = Pattern
						.compile("<a href=\"([^\"]+)\"><img id=\"img\" width=\"[^\"]+\" height=\"[^\"]+\" src=\"([^\"]+)\" alt=\"[^\"]+\" name=\"img\" /></a>");
				Matcher m2 = p2.matcher(results);
				if (m2.find()) {
					result.add(m2.group(2));
					
					
					//download file
					//downloadFile(m.group(2), num, foldername);
					
					//return true;
				}

				
				//downloadImage(addr, num, foldername);
				//if (this.listener!=null){ 
				//	this.listener.notifyProgress(((float)num)/((float)list.size()));
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
