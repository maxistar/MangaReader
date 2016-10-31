package com.maxistar.mangabrowser.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;
import com.maxistar.mangabrowser.adapters.AdultManga;
import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;
import com.maxistar.mangabrowser.adapters.MangaEden;

public class MangaEdenTestCase extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSearch(){
		MangaEden m = new MangaEden();
		SearchResult sr = m.search("Ninetails ", 0);	
		MangableTestCase.assertEquals(1, sr.getItems().size());
		
		MangaItem mi = sr.getItems().get(0);
		
		ArrayList <VolumeItem> volumes = m.getVolumes(mi);
		Log.w("xxxx",""+volumes.size());
		Assert.assertEquals(66, volumes.size());
	
		for (VolumeItem v : volumes) {
			assertTrue(v.url.indexOf("http://")==0 || v.url.indexOf("https://")==0);
		}
		
	}
	
	public void testRegEx(){
		String s = "<a href=\"/en-manga/a-thousand-years-ninetails/48/1/\" class=\"chapterLink\">\r\n"+
"<span class=\"chapterDate\">A Thousand Years Ninetails</span>\r\n"+
"<b>48: The Reincarnation (1)</b>\r\n";
	
		Pattern p = Pattern
				.compile(MangaEden.REGEX_CHAPTER_ITEMS);
		Matcher m = p.matcher(s);
		int c = 0;
		while (m.find()) {
			c++;
//			VolumeItem item1 = new VolumeItem(m.group(2)+" : "+m.group(3), m.group(1),
//					TYPE_MANGA_EDEN);
//			result.add(item1);
		}
		assertEquals(1, c);
	}
	
	String getUrl(String url) {
		try {
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(new URI(url));

		HttpResponse response = client.execute(request);
		InputStream is = response.getEntity().getContent();

		StringBuilder data = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),BaseSearchAdapter.BUFFER_SIZE_8);
		String line = reader.readLine();
		while (line != null) {
			data.append(line);
			line = reader.readLine();
		}
		return data.toString();
		}
		catch (Exception e){
			return "";
		}
	}
	
//	void saveToDisk(String s) {
		
//	}
	
	public void testRegExFromWeb(){
		String s = this.getUrl("http://www.mangaeden.com/en-manga/a-thousand-years-ninetails/");
	
		String start = "<th width=\"124\"><span>Date added</span></th>";
		String end = "<a href=\"/en-manga/a-thousand-years-ninetails/57/1/\" class=\"chapterLink\">";
		
		int pos = s.indexOf(start);
		s = s.substring(pos);

		pos = s.indexOf(end);
		s = s.substring(0, pos);
		
		Log.w("oneitem:", s);
		
//		this.saveToDisk(s);
		
		Pattern p = Pattern
				.compile(MangaEden.REGEX_CHAPTER_ITEMS);
		Matcher m = p.matcher(s);
		int c = 0;
		while (m.find()) {
			c++;
		}
		assertTrue(c>1);
	}

	public void testImagesUrls(){
		MangaEden m = new MangaEden();
		String test_url = m.getServerAddress() + "/en-manga/skip-beat/211/1/";
		VolumeItem item = new VolumeItem("Some Volume", test_url, BaseSearchAdapter.TYPE_MANGA_EDEN);
		ArrayList<String> r = m.getImageUrls(item);
		assertEquals(29, r.size());
		
		for (String addr : r) {
			assertTrue(addr.indexOf("http://")==0 || addr.indexOf("https://")==0);
		}

	}

}
