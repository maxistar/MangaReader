package com.maxistar.mangabrowser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;

import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;

public class MangaUtils {

	static private TreeMap<String, MangaItem> favorites = null;
	//static public TreeMap<ImageView, String> previewloading = new TreeMap<ImageView, String>();
	/**
	 * replaces all non alphanumeric values with understrokes
	 * 
	 * @param name
	 * @return
	 */
	static String safeFolderName(String name) {
		Pattern p = Pattern.compile(MStrings.REGEXP_ALPHANUMS, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(name);
		String foldername = m.replaceAll(MStrings.UNDERSTROKE);

		p = Pattern.compile(MStrings.REGEXP_UNDERSTOKES, Pattern.CASE_INSENSITIVE);
		m = p.matcher(foldername);
		foldername = m.replaceAll(MStrings.UNDERSTROKE);
		return foldername;
	}

	static TreeMap<String, MangaItem> getFavorites(Context context) {
		if (MangaUtils.favorites != null)
			return MangaUtils.favorites;
		try {
			FileInputStream fis = context.openFileInput(MStrings.FAVORITES);
			ObjectInputStream objectIn = new ObjectInputStream(fis);
			MangaUtils.favorites = (TreeMap<String, MangaItem>) objectIn
					.readObject();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StreamCorruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (MangaUtils.favorites == null)
			MangaUtils.favorites = new TreeMap<String, MangaItem>();
		return MangaUtils.favorites;
	}

	static void saveFavorites(Context context) {
		TreeMap <String, MangaItem> favorites = MangaUtils
				.getFavorites(context);
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(MStrings.FAVORITES, Context.MODE_PRIVATE);
			ObjectOutputStream objectOut = new ObjectOutputStream(fos);
			objectOut.writeObject(favorites);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	static List<MangaItem> getFavoritesList(Context context) {
		ArrayList <MangaItem> list = new ArrayList<MangaItem>();
		TreeMap <String, MangaItem> favorites = MangaUtils
				.getFavorites(context);

		for (Map.Entry <String, MangaItem> entry : favorites.entrySet()) {
			MangaItem item = entry.getValue();
			list.add(item);
		}
		return list;
	}

	static String getFolderName(int type) {
		return BaseSearchAdapter.getFolderName(type);
	}

	static boolean isItemFavorited(Context context, MangaItem item) {
		String key = item.getFavoritesKey();
		TreeMap <String, MangaItem> favorites = MangaUtils
				.getFavorites(context);
		return favorites.containsKey(key);
	}

	static boolean isVolumeDownloaded(MangaItem manga,
			VolumeItem item) {
		String foldername = Environment.getExternalStorageDirectory()
				+ MStrings.SLASH+MStrings.MANGABROWSER + MStrings.SLASH
				+ MangaUtils.getFolderName(manga.manga_type)
				+ MStrings.SLASH + manga.getFolderName() + MStrings.SLASH + item.getFolderName();

		// common name
		File direct = new File(foldername);
		if (!direct.exists()) {
			return false;
		}
		return true;
	}

	static void addFavorite(Context context, MangaItem item) {
		String key = item.getFavoritesKey();
		TreeMap <String, MangaItem> favorites = MangaUtils
				.getFavorites(context);
		favorites.put(key, item);
		MangaUtils.saveFavorites(context);
	}

	static void removeFavorite(Context context, MangaItem item) {
		String key = item.getFavoritesKey();
		TreeMap <String, MangaItem> favorites = MangaUtils
				.getFavorites(context);
		favorites.remove(key);
		MangaUtils.saveFavorites(context);
	}

	static String getMd5Sign(String value) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance(MStrings.MD5);
		byte[] bytesOfMessage;
		byte[] thedigest;

		bytesOfMessage = value.getBytes(MStrings.UTF_8); 
		thedigest = md.digest(bytesOfMessage);

		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < thedigest.length; i++) {
			hexString.append(Integer.toHexString(0xFF & thedigest[i]));
		}

		return hexString.toString();
	}

	
	
    public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for(;;) {
              int count = is.read(bytes, 0, buffer_size);
              if(count == -1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex) {}
    }
    
    static void deleteFolder(File folder) {
    	//
    	for (File child : folder.listFiles()) {
			child.delete();
    	}
    	folder.delete();
    }
}
