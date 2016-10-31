package com.maxistar.mangabrowser;

import java.io.Serializable;

public class VolumeItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String name;
	public String url;
	public int manga_type;
	public int page_num = 0;
	boolean read_flag = false;
	
	public VolumeItem(String name,String url,int type){
		this.name = name;
		this.url = url;
		this.manga_type = type;
	}
	
	/**
	 * 
	 */
	public String getUniqueKey(){
		return this.name + this.url + this.manga_type;
	}
	
	/**
	 * @return
	 */
	public String getFolderName(){
		return MangaUtils.safeFolderName(name);
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean equals(VolumeItem item){
		if (this.manga_type!=item.manga_type) return false;
		if (!this.url.equals(item.url)) return false;
		return true;
	}

}
