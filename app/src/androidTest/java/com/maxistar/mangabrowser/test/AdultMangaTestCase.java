package com.maxistar.mangabrowser.test;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;
import android.util.Log;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;
import com.maxistar.mangabrowser.adapters.AdultManga;
import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;

public class AdultMangaTestCase extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSearch(){
		AdultManga m = new AdultManga();
		SearchResult sr = m.search("Gantz", 0);
		MangableTestCase.assertEquals(2, sr.getItems().size());
		
		MangaItem mi = sr.getItems().get(0);
		
		ArrayList <VolumeItem> volumes = m.getVolumes(mi);
		Log.w("xxxx",""+volumes.size());
		
		Assert.assertEquals(385, volumes.size());
		
		for (VolumeItem v : volumes) {
			assertTrue(v.url.indexOf("http://")==0 || v.url.indexOf("https://")==0);
		}

		
	}
	
	public void testImagesUrls(){
		AdultManga m = new AdultManga();
		String test_url = m.getServerAddress() + "/the_testament_of_sister_new_devil/vol1/9";
		VolumeItem item = new VolumeItem("Some Volume", test_url, BaseSearchAdapter.TYPE_ADULT_MANGA);
		ArrayList<String> r = m.getImageUrls(item);
		assertEquals(43, r.size());
		
		for (String addr : r) {
			assertTrue(addr.indexOf("http://")==0 || addr.indexOf("https://")==0);
		}
	}
}
