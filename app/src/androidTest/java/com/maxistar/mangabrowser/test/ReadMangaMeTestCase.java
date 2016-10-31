package com.maxistar.mangabrowser.test;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;
import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;
import com.maxistar.mangabrowser.adapters.ReadMangaMe;

public class ReadMangaMeTestCase extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSearch(){
		ReadMangaMe m = new ReadMangaMe();
		SearchResult sr = m.search("One Piece", 0);
		
		assertEquals(59, sr.getItems().size());
		MangaItem mi = sr.getItems().get(0);
		ArrayList <VolumeItem> volumes = m.getVolumes(mi);
		Assert.assertEquals(1, volumes.size());
		
		for (VolumeItem v : volumes) {
			assertTrue(v.url.indexOf("http://")==0 || v.url.indexOf("https://")==0);
		}
	}
	
	public void testImagesUrls(){
		ReadMangaMe m = new ReadMangaMe();
		String test_url = m.getServerAddress() + "/fairy_tail/vol1/1";
		VolumeItem item = new VolumeItem("Some Volume", test_url, BaseSearchAdapter.TYPE_ADULT_MANGA);
		ArrayList<String> r = m.getImageUrls(item);
		assertEquals(77, r.size());
		
		for (String addr : r) {
			assertTrue(addr.indexOf("http://")==0 || addr.indexOf("https://")==0);
		}

	}


}
