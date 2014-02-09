package com.blakebarrett.mskr;

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

		Bitmap maskedBitmap = source.copy(source.getConfig(), true);
		final Canvas canvas = new Canvas(maskedBitmap);
		final Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(mask, 0, 0, paint);

		// Canvas canvas = new Canvas(this.source);
		// Paint paint = new Paint();
		// paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		// // TODO: convert this.mask into paint.
		// canvas.drawBitmap(
		// this.source,
		// null,
		// new Rect(0, 0, this.source.getWidth(), this.source.getHeight()),
		// paint);

		source.recycle();
		mask.recycle();

		return maskedBitmap;
	}
}
