package com.blakebarrett.mskr;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class MaskedBitmap {

	/**
	 * Here are some links!
	 * 
	 * http://stackoverflow.com/questions/1540272/android-how
	 * -to-overlay-a-bitmap-draw-over-a-bitmap
	 * 
	 * 
	 * http://stackoverflow.com/questions/10268724/android-change-canvas-
	 * background-color-without-losing-any-drawings-from-it/10370828#10370828
	 * 
	 * https://coderwall.com/p/hmzf4w <-- this guy looks legit
	 * 
	 * 
	 * http://stackoverflow.com/questions/8630365/android-is-it-possible-to-
	 * declare-an-alpha-mask-directly-within-layer-list-xml
	 * 
	 */
	public MaskedBitmap(final Bitmap source, final Bitmap mask) {
		super();
		draw(source, mask);
	}

	public static Bitmap draw(final Bitmap source, final Bitmap mask) {

		Bitmap scaledMask = Bitmap.createScaledBitmap(mask, source.getWidth(),
				source.getHeight(), true);
		final Canvas canvas = new Canvas(source);
		final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(scaledMask, 0, 0, paint);

		mask.recycle();
		scaledMask.recycle();

		return source;
	}

	public static void save(final String filename, final Bitmap image) {
		try {
			final FileOutputStream out = new FileOutputStream(filename);
			image.compress(Bitmap.CompressFormat.PNG, 100, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
