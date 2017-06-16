package com.ps.util;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.ps.hcentercoupon.R;

public class ImageLoader {

	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	Handler handler = new Handler();// handler to display images in UI thread
	Context ct;
	Animation animation;
	int REQUIRED_SIZE;
	

	public ImageLoader(Context context) {
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		ct = context;
		REQUIRED_SIZE = 460;
	}

	final int stub_id = R.drawable.loading;

	public void DisplayImage(String imgName, ImageView imageView) {
		// imgName =imgName;
		imageViews.put(imageView, imgName);
		Bitmap bitmap = memoryCache.get(imgName);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
		} else {
			queuePhoto(imgName, imageView,true);
			imageView.setImageResource(stub_id);
		}
	}

	
	public void DisplayImage(String imgName, ImageView imageView, boolean withCache) {
		// imgName =imgName;
		imageViews.put(imageView, imgName);
		
	
		if(withCache)
		{
			Bitmap bitmap = memoryCache.get(imgName);
			if (bitmap != null) {
				imageView.setImageBitmap(bitmap);
			} else {
				queuePhoto(imgName, imageView,true);
				//imageView.setImageResource(stub_id);
			}
		}else
		{
			queuePhoto(imgName, imageView,false);
			//imageView.setImageResource(stub_id);
		}
	}

	private void queuePhoto(String url, ImageView imageView,boolean cacheMode) {
		
		PhotoToLoad p = new PhotoToLoad(url, imageView,cacheMode);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url) {
		File f = fileCache.getFile(url);

		// from SD cache
		Bitmap b = decodeFile(f);
		if (b != null)
			return b;

		// from web
		try {
			
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			conn.disconnect();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}
	
	public Bitmap getBitmap(String url,boolean cache) {
		File f = fileCache.getFile(url);

		if (cache)
		{
			// from SD cache
			Bitmap b = decodeFile(f);
			if (b != null)
				return b;
		}

		// from web
		try {
			
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			conn.disconnect();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	public void setRequiredSize(int size)
	{
		REQUIRED_SIZE = size;
	}
	// decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream = new FileInputStream(f);
			BitmapFactory.decodeStream(stream, null, o);
			stream.close();

			// Find the correct scale value. It should be the power of 2.
			
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public boolean cacheMode;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
			cacheMode = true;
		}
		public PhotoToLoad(String u, ImageView i,boolean mode) {
			url = u;
			imageView = i;
			cacheMode = mode;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			try {
				if (imageViewReused(photoToLoad))
					return;
				
				
				Bitmap bmp = getBitmap(photoToLoad.url,photoToLoad.cacheMode);
				if(photoToLoad.cacheMode)
					memoryCache.put(photoToLoad.url, bmp);
				
				if (imageViewReused(photoToLoad))
					return;
				BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
				handler.post(bd);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)

				photoToLoad.imageView.setImageBitmap(bitmap);
			//else
				//photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
