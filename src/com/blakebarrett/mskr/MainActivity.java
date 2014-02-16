package com.blakebarrett.mskr;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.blakebarrett.mskr.MaskedBitmap.SquareMode;

public class MainActivity extends Activity {

	private ImageButton imageButton;
	private Bitmap maskedBitmap;
	private Uri sourceBitmapUri;
	private int selectedMask = R.drawable.crclmsk;
	private File saved;
	private String filename;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addImageClickListener();
		addMaskChangeListener();
		addToolbarItemsClickListeners();
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
		case R.id.action_delete:
			createNew();
			break;
		case R.id.action_add_layer:
			addLayer();
			break;
		case R.id.action_save:
			saved = save(maskedBitmap);
			break;
		case R.id.action_about:
			launchAboutActivity();
			break;
		case R.id.action_share:
			if (saved == null) {
				saved = save(applyMaskToImage(maskedBitmap, selectedMask));
			}
			share(saved);
			break;
		default:
			return super.onOptionsItemSelected(item);
		}
		return super.onOptionsItemSelected(item);
	}

	private void addToolbarItemsClickListeners() {
		findViewById(R.id.addLayerButton).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (maskedBitmap != null) {
							addLayer();
						}
					}
				});
		findViewById(R.id.saveImageButton).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (maskedBitmap != null) {
							saved = save(applyMaskToImage(maskedBitmap,
									selectedMask));
						}
					}
				});
		findViewById(R.id.deleteImageButton).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (maskedBitmap != null) {
							createNew();
						}
					}
				});
	}

	private void createNew() {
		// create filename for this composition
		filename = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES).getAbsolutePath()
				+ "/" + System.currentTimeMillis() + "_mskr.jpg";

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
	}

	private void addLayer() {
		if (this.maskedBitmap == null) {
			return;
		}
		maskedBitmap = applyMaskToImage(maskedBitmap, selectedMask);
		saved = null;
	}

	private void launchAboutActivity() {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
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

		findViewById(R.id.controls_layout).setClickable(true);

		sourceBitmapUri = data.getData();

		if (maskedBitmap != null) {
			maskedBitmap.recycle();
			maskedBitmap = null;
		}

		maskedBitmap = getBitmapFromUri(sourceBitmapUri);
		applyMaskToImage(maskedBitmap, selectedMask);
		saved = null;
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
				applyMaskToImage(maskedBitmap, selectedMask);
				saved = null;
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

	private Bitmap applyMaskToImage(final Bitmap bitmap, final int maskId) {
		if (bitmap == null) {
			return null;
		}
		Bitmap temp = MaskedBitmap.draw(bitmap.copy(Config.ARGB_8888, true),
				getMask(maskId)).copy(Config.ARGB_8888, true);
		imageButton.setImageBitmap(temp);
		return temp;
	}

	private File save(final Bitmap bitmap) {
		File file = createFile(filename);
		MaskedBitmap.save(filename, bitmap);
		dispatchMediaScanIntent(file);
		return file;
	}

	private File createFile(final String filename) {
		File temp = new File(filename);
		try {
			// overwrite anything with the same filename.
			if (temp.exists()) {
				temp.delete();
			}
			temp.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

	private void dispatchMediaScanIntent(final File file) {
		final Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		final Uri contentUri = Uri.fromFile(file);
		mediaScanIntent.setData(contentUri);
		getApplicationContext().sendBroadcast(mediaScanIntent);
	}

	private void share(final File file) {
		Intent shareIntent = new Intent();
		shareIntent.setAction(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
		shareIntent.setType("image/jpeg");
		startActivity(Intent.createChooser(shareIntent,
				getResources().getText(R.string.action_share)));
	}

	private Bitmap getMask(final int resId) {
		Bitmap mask = BitmapFactory.decodeResource(getResources(), resId);
		return MaskedBitmap.makeItSquare(MaskedBitmap.MAXIMUM_IMAGE_SIZE, mask,
				SquareMode.LETTERBOX);
	}

	private Bitmap getBitmapFromUri(final Uri uri) {
		return BitmapLoadingUtils.getBitmapFromUri(this, uri);
	}
}
