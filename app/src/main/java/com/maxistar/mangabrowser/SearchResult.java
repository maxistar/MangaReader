package com.maxistar.mangabrowser;

import java.util.ArrayList;
import java.util.List;

public class SearchResult {
	List <MangaItem> items;
	int total_count = 0; 
	public SearchResult() {
		items = new ArrayList<MangaItem>();
	}
	
	public void addItem(MangaItem item){
		items.add(item);
	}
	
	public List<MangaItem> getItems(){
		return items;
	}
}