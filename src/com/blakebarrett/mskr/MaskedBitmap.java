package com.blakebarrett.mskr;

import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

public class MaskedBitmap {
	public static int MAXIMUM_IMAGE_SIZE = 1024;

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

	enum SquareMode {
		CROP, LETTERBOX;
	}

	static Bitmap makeItSquare(final int size, final Bitmap source,
			final SquareMode mode) {

		float originalWidth = source.getWidth();
		float originalHeight = source.getHeight();
		float scale = 1.0f;

		switch (mode) {
		case CROP:
			scale = size / Math.min(originalWidth, originalHeight);
			break;
		case LETTERBOX:
			scale = size / Math.max(originalWidth, originalHeight);
			break;
		}

		final float xTranslation = (size - originalWidth * scale) / 2.0f;
		final float yTranslation = (size - originalHeight * scale) / 2.0f;

		final Matrix transformation = new Matrix();
		transformation.postTranslate(xTranslation, yTranslation);
		transformation.preScale(scale, scale);

		final Paint paint = new Paint();
		paint.setFilterBitmap(true);

		final Bitmap background = Bitmap.createBitmap(size, size,
				Config.ARGB_8888);

		final Canvas canvas = new Canvas(background);
		canvas.drawBitmap(source, transformation, paint);

		source.recycle();

		return background;
	}

	public static Bitmap draw(final Bitmap source, final Bitmap mask) {
		// final Bitmap scaledMask = makeItSquare(MAXIMUM_IMAGE_SIZE, mask,
		// SquareMode.LETTERBOX);

		// final Bitmap croppedSource = makeItSquare(MAXIMUM_IMAGE_SIZE, source,
		// SquareMode.CROP);

		return mergeAndDestroy(createBackgroundLayer(source),
				applyMaskToBitmap(source, mask));
	}

	private static Bitmap mergeAndDestroy(final Bitmap... bitmaps) {
		final Bitmap merged = Bitmap.createBitmap(bitmaps[0].getWidth(),
				bitmaps[0].getHeight(), bitmaps[0].getConfig());
		final Canvas canvas = new Canvas(merged);
		for (int i = 0; i < bitmaps.length; i++) {
			canvas.drawBitmap(bitmaps[i], new Matrix(), null);
			// bitmaps[i].recycle();
			// bitmaps[i] = null;
		}
		return merged;
	}

	private static Bitmap createBackgroundLayer(final Bitmap source) {
		final Bitmap temp = Bitmap.createBitmap(source.getWidth(),
				source.getHeight(), source.getConfig());

		final Canvas canvas = new Canvas();
		canvas.setBitmap(temp);
		canvas.drawColor(Color.WHITE);

		final Paint paint = new Paint(Paint.DITHER_FLAG);
		paint.setAlpha(128);
		paint.setStyle(Paint.Style.FILL);

		canvas.drawBitmap(source, new Matrix(), paint);

		return temp;
	}

	private static Bitmap applyMaskToBitmap(final Bitmap source,
			final Bitmap mask) {

		final Canvas canvas = new Canvas(source);

		final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		canvas.drawBitmap(mask, 0, 0, paint);
		paint.setXfermode(null);

		mask.recycle();

		return source;
	}

	public static void save(final String filename, final Bitmap image) {
		try {
			final FileOutputStream out = new FileOutputStream(filename);
			image.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
