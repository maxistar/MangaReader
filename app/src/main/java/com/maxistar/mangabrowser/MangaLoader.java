package com.maxistar.mangabrowser;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Environment;
import android.os.Handler;

import com.maxistar.mangabrowser.adapters.BaseSearchAdapter;
import com.maxistar.mangabrowser.adapters.BaseSearchAdapter.OnDownloadProgressListener;

public class MangaLoader {

	static final int MANGA_LOADED = 1;
	static final int MANGA_LOADING = 2;
	static final int MANGA_NOT_LOADED = 3;

	final int stub_id = R.drawable.download;
	static private MangaLoader instance = null;
	// OnProgressUpdateListener progress_listener = null;

	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread
	Set<String> loading_tasks = new HashSet<String>();

	HashSet<OnProgressUpdateListener> progress_listeners = new HashSet<OnProgressUpdateListener>();

	private MangaLoader() {
		executorService = Executors.newFixedThreadPool(5);
	}

	static private MangaLoader singleton() {
		if (MangaLoader.instance == null) {
			MangaLoader.instance = new MangaLoader();
		}
		return MangaLoader.instance;
	}

	private int getVolumeStatusInt(MangaItem manga, VolumeItem volume) {
		if (loading_tasks.contains(volume.getUniqueKey())) {
			return MangaLoader.MANGA_LOADING;			
		} else if (MangaUtils.isVolumeDownloaded(manga, volume)) {
			return MangaLoader.MANGA_LOADED;
		} else {
			return MangaLoader.MANGA_NOT_LOADED;
		}
	}

	private void downloadMangaInt(MangaItem manga, VolumeItem volume) {
		if (this.loading_tasks.contains(volume.getUniqueKey()))
			return; // already downloading!
		
		this.loading_tasks.add(volume.getUniqueKey());

		executorService.submit(new VolumeLoader(manga, volume));
	}

	private void setProgressListenerInt(OnProgressUpdateListener listener) {
		if (listener != null)

			this.progress_listeners.add(listener);
	}

	private void removeProgressListenerInt(OnProgressUpdateListener listener) {
		if (listener == null) return;
		if (!this.progress_listeners.contains(listener)) return;
		this.progress_listeners.remove(listener);
	}

	static public void downloadManga(MangaItem manga, VolumeItem volume) {
		MangaLoader.singleton().downloadMangaInt(manga, volume);
	}

	// static public void displayImage(Context context, MangaItem manga,
	// VolumeItem volume, ImageView imageView) {
	// MangaLoader.singleton().displayImageInt(manga, volume, imageView);
	// }

	static public int getVolumeStatus(MangaItem manga, VolumeItem volume) {
		return MangaLoader.singleton().getVolumeStatusInt(manga, volume);
	}

	static public void setProgressListener(OnProgressUpdateListener listener) {
		MangaLoader.singleton().setProgressListenerInt(listener);
	}

	static public void removeProgressListener(OnProgressUpdateListener listener) {
		MangaLoader.singleton().removeProgressListenerInt(listener);
	}

	class VolumeLoader implements Runnable, OnDownloadProgressListener {
		MangaItem manga;
		VolumeItem volume;

		VolumeLoader(MangaItem manga, VolumeItem volume) {
			this.manga = manga;
			this.volume = volume;
		}

		@Override
		public void run() {
			BaseSearchAdapter adapter;
			try {
				
				StartProgressDisplayer bd0 = new StartProgressDisplayer(volume);
				handler.post(bd0);
				
				String foldername = Environment.getExternalStorageDirectory()
						+ MStrings.SLASH+MStrings.MANGABROWSER+MStrings.SLASH+MStrings.DOWNLOAD+MStrings.SLASH
						+ MangaUtils.getFolderName(manga.manga_type) + MStrings.SLASH
						+ manga.getFolderName() + MStrings.SLASH + volume.getFolderName();

				File direct = new File(foldername);
				if (!direct.exists()) {
					direct.mkdirs();
				}

				adapter = BaseSearchAdapter.getSearchAdapter(volume.manga_type);
				adapter.setOnDownloadProgressListner(this);
				adapter.downloadImages(volume, foldername);

				CompleteProgressDisplayer bd = new CompleteProgressDisplayer(
						manga, volume);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}

		@Override
		public void notifyProgress(float progress) {
			UpdateProgressDisplayer bd = new UpdateProgressDisplayer(volume,
					progress);
			handler.post(bd);
		}
	}

	class UpdateProgressDisplayer implements Runnable {
		VolumeItem item;
		float progress;

		UpdateProgressDisplayer(VolumeItem item, float progress) {
			this.item = item;
			this.progress = progress;
		}

		public void run() {
			Iterator<OnProgressUpdateListener> it = progress_listeners
					.iterator();
			while (it.hasNext()) {
				OnProgressUpdateListener l = it.next();
				l.onProgressUpdate(item, progress);
			}
		}
	}

	class StartProgressDisplayer implements Runnable {
		VolumeItem item;

		StartProgressDisplayer(VolumeItem item) {
			this.item = item;
		}

		public void run() {
			Iterator<OnProgressUpdateListener> it = progress_listeners
					.iterator();
			while (it.hasNext()) {
				OnProgressUpdateListener l = it.next();
				l.onDownloadStarted(item);
			}
		}
	}
	
	
	class CompleteProgressDisplayer implements Runnable {
		VolumeItem item;
		MangaItem manga;

		CompleteProgressDisplayer(MangaItem manga, VolumeItem item) {
			this.item = item;
			this.manga = manga;
		}

		public void run() {
			File current = new File(Environment.getExternalStorageDirectory()
					+ MStrings.SLASH+MStrings.MANGABROWSER+MStrings.SLASH+MStrings.DOWNLOAD+MStrings.SLASH
					+ MangaUtils.getFolderName(manga.manga_type) + MStrings.SLASH
					+ manga.getFolderName() + MStrings.SLASH + item.getFolderName());

			File newpath = new File(Environment.getExternalStorageDirectory()
					+ MStrings.SLASH+MStrings.MANGABROWSER+MStrings.SLASH
					+ MangaUtils.getFolderName(manga.manga_type) + MStrings.SLASH
					+ manga.getFolderName() + MStrings.SLASH + item.getFolderName());
			
			//current.renameTo(newpath);

			
			if (newpath.exists()) {
				//newpath.delete(); //remove old folder
				MangaUtils.deleteFolder(newpath);
			}
			if (!newpath.getParentFile().exists()) {
				newpath.getParentFile().mkdirs();
			}
			if (!current.renameTo(newpath)) {

			}

			loading_tasks.remove(item.getUniqueKey());

			Iterator<OnProgressUpdateListener> it = progress_listeners
					.iterator();
			while (it.hasNext()) {
				OnProgressUpdateListener l = it.next();
				l.onDownloadComplete(item);
			}
		}
	}

	public interface OnProgressUpdateListener {
		void onProgressUpdate(VolumeItem item, float progress);

		void onDownloadComplete(VolumeItem item);
		
		void onDownloadStarted(VolumeItem item);
		
	}

}
