package com.blakebarrett.mskr;

import java.io.FileDescriptor;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	private ImageButton imageButton;

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

	private void addImageClickListener() {
		imageButton = (ImageButton) findViewById(R.id.previewImage);
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
				intent.setType("image/*");
				startActivityForResult(
						Intent.createChooser(intent, "Select a photo"), 1);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		final Uri uri = data.getData();
		final Bitmap selectedBitmap = getBitmapFromUri(uri);
		imageButton.setImageBitmap(selectedBitmap);
	}

	private Bitmap getBitmapFromUri(final Uri uri) {
		try {
			ParcelFileDescriptor parcelFileDescriptor = getContentResolver()
					.openFileDescriptor(uri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor
					.getFileDescriptor();
			Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
			parcelFileDescriptor.close();
			return image;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
