package com.blakebarrett.mskr;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.blakebarrett.mskr.MaskedBitmap.SquareMode;

public class BitmapLoadingUtils {

	public static Bitmap getBitmapFromUri(final Context ctx, final Uri uri) {
		try {
			final ParcelFileDescriptor parcelFileDescriptor = ctx
					.getContentResolver().openFileDescriptor(uri, "r");
			final FileDescriptor fileDescriptor = parcelFileDescriptor
					.getFileDescriptor();
			final Bitmap image = BitmapFactory
					.decodeFileDescriptor(fileDescriptor);
			parcelFileDescriptor.close();

			return MaskedBitmap.makeItSquare(MaskedBitmap.MAXIMUM_IMAGE_SIZE,
					image, SquareMode.CROP);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public static Bitmap getDownsampledBitmap(Context ctx, Uri uri,
			int targetWidth, int targetHeight) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options outDimens = getBitmapDimensions(ctx, uri);

			int sampleSize = calculateSampleSize(outDimens.outWidth,
					outDimens.outHeight, targetWidth, targetHeight);

			bitmap = downsampleBitmap(ctx, uri, sampleSize);

		} catch (Exception e) {
			// handle the exception(s)
		}

		return bitmap;
	}

	private static BitmapFactory.Options getBitmapDimensions(Context ctx,
			Uri uri) throws FileNotFoundException, IOException {
		BitmapFactory.Options outDimens = new BitmapFactory.Options();
		outDimens.inJustDecodeBounds = true; // the decoder will return null (no
												// bitmap)

		InputStream is = ctx.getContentResolver().openInputStream(uri);
		// if Options requested only the size will be returned
		BitmapFactory.decodeStream(is, null, outDimens);
		is.close();

		return outDimens;
	}

	private static int calculateSampleSize(int width, int height,
			int targetWidth, int targetHeight) {
		float bitmapWidth = width;
		float bitmapHeight = height;

		int bitmapResolution = (int) (bitmapWidth * bitmapHeight);
		int targetResolution = targetWidth * targetHeight;

		int sampleSize = 1;

		if (targetResolution == 0) {
			return sampleSize;
		}

		for (int i = 1; (bitmapResolution / i) > targetResolution; i *= 2) {
			sampleSize = i;
		}

		return sampleSize;
	}

	private static Bitmap downsampleBitmap(Context ctx, Uri uri, int sampleSize)
			throws FileNotFoundException, IOException {
		Bitmap resizedBitmap;
		BitmapFactory.Options outBitmap = new BitmapFactory.Options();
		outBitmap.inJustDecodeBounds = false; // the decoder will return a
												// bitmap
		outBitmap.inSampleSize = sampleSize;

		InputStream is = ctx.getContentResolver().openInputStream(uri);
		resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
		is.close();

		return resizedBitmap;
	}
}
