package com.maxistar.mangabrowser.test;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;
import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;
import com.maxistar.mangabrowser.adapters.MangaReader;

public class MangaReaderTestCase extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSearch(){
		MangaReader m = new MangaReader();
		SearchResult sr = m.search("Ninetails", 0);	
		MangableTestCase.assertEquals(1, sr.getItems().size());
		
		MangaItem mi = sr.getItems().get(0);
		
		ArrayList <VolumeItem> volumes = m.getVolumes(mi);
		Assert.assertEquals(108, volumes.size());
		
		for (VolumeItem v : volumes) {
			assertTrue(v.url.indexOf("http://")==0 || v.url.indexOf("https://")==0);
		}

	}
	
	public void testImagesUrls(){
		MangaReader m = new MangaReader();
		String test_url = m.getServerAddress() + "/freezing/160";
		VolumeItem item = new VolumeItem("Some Volume", test_url, BaseSearchAdapter.TYPE_ADULT_MANGA);
		ArrayList<String> r = m.getImageUrls(item);
		assertEquals(26, r.size());
		
		for (String addr : r) {
			assertTrue(addr.indexOf("http://")==0 || addr.indexOf("https://")==0);
		}

	}
}
