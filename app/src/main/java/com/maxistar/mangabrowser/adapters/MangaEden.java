/**
 * http://www.mangaeden.com/
 * http://www.mangaeden.com/en-directory/?title=San
 * 
 * ganres: 
 * Action Adult Adventure Comedy Doujinshi Drama Ecchi Fantasy Gender Bender Harem Historical Horror Josei Martial Arts Mature Mecha Mystery One Shot Psychological Romance School Life Sci-fi Seinen Shoujo Shounen Slice of Life Smut Sports Supernatural Tragedy Webtoons Yaoi Yuri
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

public class MangaEden extends BaseSearchAdapter {
	
	void init() {
		server_address = "http://www.mangaeden.com";
		settings_key = "source_mangaeden";
		name = "MangaEden";
		language = "en";
	}
	
	public final static String REGEX_CHAPTER_ITEMS = "<a\\s+href=\"([^\"]+)\"\\s+class=\"chapterLink\">\\s*<span\\s+class=\"chapterDate\">([^<]+)</span>\\s*<b>([^<]+)</b>";
	
	/**
	 * 
	 */
	public SearchResult search(String word, int page) {
		//http://www.mangaeden.com/en-directory/?title=Bleach&author=&artist=&releasedType=0&released=
		SearchResult result = new SearchResult();
		try {
			
			String results = this.getGetData("http://www.mangaeden.com/en-directory/?title="+URLEncoder.encode(word, "ISO-8859-1"));
			
			int pos = results.indexOf("<table id=\"mangaList\">");
			results = results.substring(pos);

			pos = results.indexOf("</table>");
			results = results.substring(0, pos);
			

			Pattern p = Pattern
			.compile("<a\\s*href=\"([^\"]+)\"\\s*class=\"(openManga|closedManga)\">([^<]+)");

			Matcher m = p.matcher(results);

			while (m.find()) {
				MangaItem item = new MangaItem(m.group(3),
						m.group(1), 0, TYPE_MANGA_EDEN);
				item.thumnail_url = "";
				result.addItem(item);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Returns get of volumes
	 */
	public ArrayList<VolumeItem> getVolumes(MangaItem item) {
		ArrayList<VolumeItem> result = new ArrayList<VolumeItem>();
		try {
			String results = this.getGetData(server_address + item.url);
			

			Pattern p = Pattern
					.compile(MangaEden.REGEX_CHAPTER_ITEMS);
			Matcher m = p.matcher(results);


			while (m.find()) {
				VolumeItem item1 = new VolumeItem(m.group(2)+" : "+m.group(3), server_address + m.group(1),
						TYPE_MANGA_EDEN);
				result.add(item1);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Download images of item and saves them
	 *
	public void downloadImages(VolumeItem item, String foldername){
		try {
			String page;
			String pagination;
			page = this.getGetData(server_address+item.url);
			int pos = page.indexOf("<div class=\"pagination \">");
			pagination = page.substring(pos);

			pos = pagination.indexOf("<div id=\"flashMsg\">");
			pagination = pagination.substring(0, pos);

			Pattern p = Pattern.compile("<a class=\"ui-state-default\" href=\"([^\"]+)\">[^<]+</a>");
			Matcher m = p.matcher(pagination);
			int num = 1;
			ArrayList<String> list = new ArrayList<String>();
			list.add(server_address + item.url);

			while (m.find()) {
				list.add(server_address + m.group(1) + "/");
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
	
	public ArrayList <String> getImageUrls(VolumeItem item) {
		ArrayList<String> result = new ArrayList<String>();

		try {
			String page;
			String pagination;
			page = this.getGetData(item.url);
			int pos = page.indexOf("<div class=\"pagination \">");
			pagination = page.substring(pos);

			pos = pagination.indexOf("<div id=\"flashMsg\">");
			pagination = pagination.substring(0, pos);

			Pattern p = Pattern.compile("<a class=\"ui-state-default\" href=\"([^\"]+)\">[^<]+</a>");
			Matcher m = p.matcher(pagination);
			ArrayList<String> list = new ArrayList<String>();
			list.add(item.url);

			while (m.find()) {
				list.add(server_address + m.group(1));
			}

			for (String addr : list) {
				String results = this.getGetData(addr);
				Pattern p1 = Pattern
						.compile("<img[^<]+id=\"mainImg\" src=\"([^\"]+)\"[^<]+/>");
				Matcher m1 = p1.matcher(results);
				if (m1.find()) {
					result.add("http:"+m1.group(1));
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
	/*
	boolean downloadImage(String url, int num, String foldername) {
		try {
			String results = this.getGetData(url);
			Pattern p = Pattern
					.compile("<img[^<]+id=\"mainImg\" src=\"([^\"]+)\"[^<]+/>");
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
}
