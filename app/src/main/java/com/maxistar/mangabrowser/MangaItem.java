package com.maxistar.mangabrowser;

import java.io.Serializable;

public class MangaItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String url;
	public int volumes;
	public int manga_type;
	public String thumnail_url = null;
	
	public MangaItem(String name,String url,int volumes, int type){
		this.name = name;
		this.url = url;
		this.volumes = volumes;
		this.manga_type = type;
	}
	
	/**
	 * should be pretyu unique;
	 * @return
	 */
	public String getFavoritesKey(){
		return MangaUtils.getFolderName(this.manga_type)+MStrings.COLON+this.url;
	}
	
	public String getFolderName(){
		return MangaUtils.safeFolderName(name);
	}

	
}
