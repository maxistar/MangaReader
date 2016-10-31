package com.maxistar.mangabrowser.test;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;
import android.util.Log;

import com.maxistar.mangabrowser.MangaItem;
import com.maxistar.mangabrowser.SearchResult;
import com.maxistar.mangabrowser.VolumeItem;
import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;
import com.maxistar.mangabrowser.adapters.MangaHere;

public class MangaHereTestCase extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSearch(){
		MangaHere m = new MangaHere();
		SearchResult sr = m.search("Ninetails", 0);	
		assertEquals(1, sr.getItems().size());
		
		MangaItem mi = sr.getItems().get(0);
		
		ArrayList <VolumeItem> volumes = m.getVolumes(mi);
		Log.w("xxxx",""+volumes.size());
		Assert.assertEquals(63, volumes.size());
		
		for (VolumeItem v : volumes) {
			assertTrue(v.url.indexOf("http://")==0 || v.url.indexOf("https://")==0);
		}

	}
	
	public void testImagesUrls(){
		MangaHere m = new MangaHere();
		String test_url = m.getServerAddress() + "/manga/wagatsuma_san_wa_ore_no_yome/c082/";
		VolumeItem item = new VolumeItem("Some Volume", test_url, BaseSearchAdapter.TYPE_ADULT_MANGA);
		ArrayList<String> r = m.getImageUrls(item);
		assertEquals(16, r.size());
		
		for (String addr : r) {
			assertTrue(addr.indexOf("http://")==0 || addr.indexOf("https://")==0);
		}

	}


}
