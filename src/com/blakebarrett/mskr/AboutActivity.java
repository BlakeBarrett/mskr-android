package com.blakebarrett.mskr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		final TextView textView = (TextView) findViewById(R.id.about_text_view);
		textView.setText(ABOUT_MSKR + "\n\n" + ABOUT_BLAKE + "\n\n"
				+ ABOUT_TIFFANY);
	}

	private static String ABOUT_MSKR = "mskr lets you shape layer masks over images and save them. Small app making small claims. If you don't like it, use Photoshop. \nmskr was written by Blake Barrett with design support from Tiffany Taylor. \nMore info can be found at http://mskr.co";
	private static String ABOUT_BLAKE = "About Blake: \"I am a software engineer, photographer, traveler, surfer, yogi, motorcyclist. I have been spotted drinking iced tea in various cafes across the world.\"";
	private static String ABOUT_TIFFANY = "About Tiffany: \"I am a designer and illustrator currently living in San Francisco. My passions are concept art, illustration, photography, and design for mobile and web. I have also studied Japanese for several years and am an avid corgi admirer.\"";
}
