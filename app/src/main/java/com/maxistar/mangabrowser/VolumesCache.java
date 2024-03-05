package com.maxistar.mangabrowser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;

import android.content.Context;

/**
 * info that will be stored in cache
 */
class VolumesCache implements Serializable {
    private static final long serialVersionUID = 1L;
    ArrayList<VolumeItem> items = null;
    String description = "";
    MangaItem info = null;


    static void saveCache(MangaItem item, ArrayList<VolumeItem> mData, Context context){
        VolumesCache cache = new VolumesCache();
        cache.items = mData;
        cache.info = item;

        String filename = MStrings.VOLUME+MStrings.UNDERSTROKE
                +MangaUtils.getFolderName(item.manga_type)
                +MStrings.UNDERSTROKE+item.getFolderName();
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename,Context.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(fos);
            objectOut.writeObject(cache);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static VolumesCache getCachedItems(MangaItem item, Context context){
        String filename = MStrings.VOLUME + MStrings.UNDERSTROKE
                + MangaUtils.getFolderName(item.manga_type) + MStrings.UNDERSTROKE
                + item.getFolderName();
        try {
            FileInputStream fis = context.getApplicationContext().openFileInput(filename);
            ObjectInputStream objectIn = new ObjectInputStream(fis);
            return (VolumesCache) objectIn.readObject();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
