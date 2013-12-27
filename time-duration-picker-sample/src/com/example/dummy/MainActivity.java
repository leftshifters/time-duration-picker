package com.example.dummy;

import vxt.uielement.timedurationpicker.CustomTimePicker;
import vxt.uielement.timedurationpicker.OnTimePickerChangeListener;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		CustomTimePicker mypicker = new CustomTimePicker(this);

		mypicker.setVibration(false);
		
		mypicker.setOnTimePickerChangeListener(new OnTimePickerChangeListener() {

			@Override
			public void onProgressChanged(int hours, int minutes) {
				// hours = updated hours value
				// minutes = updated minutes value
			}

			@Override
			public void onSubmitClicked(int hours, int minutes, Dialog dialog) {
				// hours = SELECTED hours value
				// minutes = SELECTED minutes value
				dialog.dismiss();
			}
		});

		
		Dialog mydialog = mypicker.create();

		mydialog.show();
	}

}
