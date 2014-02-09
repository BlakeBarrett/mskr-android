package com.blakebarrett.mskr;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ImageButton imageButton;
	private Bitmap maskedBitmap;
	private Bitmap sourceBitmap;
	private int selectedMask = R.drawable.crclmsk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addImageClickListener();
		addMaskChangeListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.action_new:
			createNew();
			break;
		case R.id.action_add_layer:
			addLayer();
			break;
		case R.id.action_save:
			save(maskedBitmap);
			break;
		case R.id.action_about:
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	private void createNew() {
		imageButton = (ImageButton) findViewById(R.id.previewImage);
		imageButton.setMaxHeight(imageButton.getWidth());
		imageButton.setImageResource(R.drawable.mskr_add);
		imageButton.setClickable(true);

		final Spinner spinner = (Spinner) findViewById(R.id.select_mask);
		spinner.setSelection(0);

		if (this.maskedBitmap != null) {
			maskedBitmap.recycle();
			maskedBitmap = null;
		}
		if (this.sourceBitmap != null) {
			sourceBitmap.recycle();
			sourceBitmap = null;
		}
	}

	private void addLayer() {
		if (this.maskedBitmap == null) {
			return;
		}

		this.sourceBitmap.recycle();
		this.sourceBitmap = null;

		this.sourceBitmap = this.maskedBitmap;
		applyMaskToImage(sourceBitmap, selectedMask);
	}

	private void addImageClickListener() {
		createNew();
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(Intent.createChooser(intent,
						getString(R.string.select_an_image)), 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((resultCode != RESULT_OK) || (data == null)) {
			return;
		}
		imageButton.setClickable(false);
		final Uri uri = data.getData();
		sourceBitmap = getBitmapFromUri(uri);

		applyMaskToImage(sourceBitmap, selectedMask);
	}

	private void addMaskChangeListener() {
		final Spinner spinner = (Spinner) findViewById(R.id.select_mask);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parentView,
					View selectedItemView, int position, long id) {

				final String selectedItemName = ((TextView) selectedItemView)
						.getText().toString();
				selectedMask = findMaskByName(selectedItemName);

				applyMaskToImage(sourceBitmap, selectedMask);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parentView) {

			}

		});
	}

	private int findMaskByName(final String name) {
		// Anyone who knows me will know how much I hate this.
		// Why can't you switch on Strings in Java 1.6!?
		if ("sqr".equals(name)) {
			return R.drawable.sqrmsk;
		} else if ("crcl".equals(name)) {
			return R.drawable.crclmsk;
		} else if ("trngl".equals(name)) {
			return R.drawable.trnglmsk;
		} else if ("POW".equals(name)) {
			return R.drawable.powmsk;
		} else if ("plrd".equals(name)) {
			return R.drawable.plrdmsk;
		} else if ("x".equals(name)) {
			return R.drawable.xmsk;
		} else if ("eqlty".equals(name)) {
			return R.drawable.eqltymsk;
		} else if ("hrt".equals(name)) {
			return R.drawable.hrtmsk;
		} else if ("dmnd".equals(name)) {
			return R.drawable.dmndmsk;
		} else {
			return R.drawable.crclmsk;
		}
	}

	private void applyMaskToImage(final Bitmap bitmap, final int maskId) {
		if (bitmap == null) {
			return;
		}
		maskedBitmap = MaskedBitmap.draw(bitmap, getMask(maskId));
		imageButton.setImageBitmap(maskedBitmap);
	}

	private void save(final Bitmap bitmap) {
		final String filename = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getAbsolutePath()
				+ "/" + System.currentTimeMillis() + "_mskr.jpg";
		MaskedBitmap.save(filename, bitmap);
		dispatchMediaScanIntent(filename);
	}

	private void dispatchMediaScanIntent(final String filename) {
		final Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		final File file = new File(filename);
		final Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		getApplicationContext().sendBroadcast(mediaScanIntent);
	}

	private Bitmap getMask(final int resId) {
		final Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
		return temp.copy(temp.getConfig(), true);
	}

	private Bitmap getBitmapFromUri(final Uri uri) {
		try {
			final ParcelFileDescriptor parcelFileDescriptor = getContentResolver()
					.openFileDescriptor(uri, "r");
			final FileDescriptor fileDescriptor = parcelFileDescriptor
					.getFileDescriptor();
			final Bitmap image = BitmapFactory
					.decodeFileDescriptor(fileDescriptor);
			parcelFileDescriptor.close();
			return image;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
