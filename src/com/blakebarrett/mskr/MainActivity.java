package com.blakebarrett.mskr;

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
import android.widget.ImageButton;

public class MainActivity extends Activity {

	private ImageButton imageButton;
	private Bitmap maskedBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addImageClickListener();
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
			break;
		case R.id.action_save:
			save();
			return super.onOptionsItemSelected(item);
		case R.id.action_settings:
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	private void createNew() {
		imageButton = (ImageButton) findViewById(R.id.previewImage);
		imageButton.setImageResource(R.drawable.mskr_add);
		if (this.maskedBitmap != null) {
			maskedBitmap.recycle();
		}
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
		final Uri uri = data.getData();

		maskedBitmap = MaskedBitmap.draw(getBitmapFromUri(uri),
				getMask(R.drawable.crclmsk));

		imageButton.setImageBitmap(maskedBitmap);
	}

	private void save() {
		final String filename = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getAbsolutePath()
				+ "/" + System.currentTimeMillis() + "_mskr.png";
		MaskedBitmap.save(filename, maskedBitmap);
	}

	private Bitmap getMask(final int resId) {
		final Bitmap temp = BitmapFactory.decodeResource(getResources(), resId);
		return temp.copy(temp.getConfig(), true);
	}

	private Bitmap getBitmapFromUri(final Uri uri) {
		try {
			ParcelFileDescriptor parcelFileDescriptor = getContentResolver()
					.openFileDescriptor(uri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor
					.getFileDescriptor();
			Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			parcelFileDescriptor.close();
			return image.copy(image.getConfig(), true);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
