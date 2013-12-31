package vxt.uielement.timedurationpicker;

import vxt.abmulani.customtimepicker.R;
import android.R.color;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/** 
 * 
 * */
public class CustomTimePicker {
	private int defaultTextColor;
	private Dialog _DIALOG;
	private OnTimePickerChangeListener listener = null;
	private Context mContext;
	private int radialWidth;
	private Vibrator mVibrator;
	private boolean mVibrate = false;
	private long mLastVibrate = 0;
	private RadialTextsView radialHoursTexts, radialMinutesText;
	private FrameLayout hoursLayout, minutesLayout;
	private int _HOURS = 12, _MINUTES = 0;
	private TextView hoursTextView, minutesTextView;
	private Animation enterAnim, exitAnim;
	private HoursPicker hourPicker;
	private MinutesPicker minutesPicker;
	private Button submitButton;
	private Activity mActivity;

	public CustomTimePicker(Activity mContext) {
		this(mContext, null);
	}

	public CustomTimePicker(Activity mContext,
			OnTimePickerChangeListener listener) {
		setOnTimePickerChangeListener(listener);
		this.mActivity = mContext;
		this.mContext = mContext;
		defaultTextColor = mContext.getResources().getColor(R.color.text_color);
		InitializeDialogView();
	}

	public void setOnTimePickerChangeListener(
			OnTimePickerChangeListener listener) {
		if (listener != null) {
			this.listener = listener;
		}
	}

	private void InitializeDialogView() {
		_DIALOG = new Dialog(mContext);
		_DIALOG.setContentView(R.layout.dialog_layout);
		_DIALOG.getWindow().addFlags(Window.FEATURE_NO_TITLE);
		_DIALOG.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		mVibrator = (Vibrator) mContext
				.getSystemService(Service.VIBRATOR_SERVICE);
		int screenDensityHeight = getWindowHeight();
		int screenDensityWidth = getWindowWidth();
		radialWidth = Math.min(screenDensityHeight, screenDensityWidth);
		radialWidth = (int) (radialWidth * 0.6);
		hoursTextView = (TextView) _DIALOG.findViewById(R.id.date_hours);
		minutesTextView = (TextView) _DIALOG.findViewById(R.id.date_minutes);
		hoursTextView.setOnClickListener(onHoursClicklistener);
		minutesTextView.setOnClickListener(onMinutesClicklistener);

		enterAnim = AnimationUtils.loadAnimation(mContext, R.anim.bottom_up);
		exitAnim = AnimationUtils.loadAnimation(mContext, R.anim.bottom_down);

		InitHoursLayout();
		InitMinutesLayout();

		submitButton = (Button) _DIALOG.findViewById(R.id.submit_button);
		submitButton.setOnClickListener(onSubmitClicklistener);
		switchToHoursLayout();
	}

	public Dialog create() {
		return _DIALOG;
	}

	public void setVibration(boolean doVibrate) {
		mVibrate = doVibrate;
	}

	OnClickListener onSubmitClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener != null)
				listener.onSubmitClicked(get_HOURS(), get_MINUTES(), _DIALOG);
		}
	};

	OnClickListener onMinutesClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switchToMinutesLayout();
		}
	};

	OnClickListener onHoursClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switchToHoursLayout();
		}
	};

	private void switchToMinutesLayout() {
		tryVibrate(15);
		if (minutesLayout.getVisibility() == View.GONE) {
			minutesTextView.setTextColor(Color.GREEN);
			minutesTextView.setSelected(true);
			hoursTextView.setSelected(false);
			minutesLayout.startAnimation(enterAnim);
			minutesLayout.setVisibility(View.VISIBLE);
			hoursTextView.setTextColor(defaultTextColor);
			hoursLayout.startAnimation(exitAnim);
			hoursLayout.setVisibility(View.GONE);
			minutesPicker.setVisible(true);
			hourPicker.setVisible(false);
		}
	}

	private void switchToHoursLayout() {
		mVibrator.vibrate(15);
		if (hoursLayout.getVisibility() == View.GONE) {
			minutesTextView.setTextColor(defaultTextColor);
			minutesTextView.setSelected(false);
			hoursTextView.setSelected(true);
			minutesLayout.startAnimation(exitAnim);
			minutesLayout.setVisibility(View.GONE);
			hoursTextView.setTextColor(Color.RED);
			hoursLayout.startAnimation(enterAnim);
			hoursLayout.setVisibility(View.VISIBLE);
			minutesPicker.setVisible(false);
			hourPicker.setVisible(true);
		}
	}

	private void InitMinutesLayout() {
		minutesLayout = (FrameLayout) _DIALOG.findViewById(R.id.minutes_layout);
		radialMinutesText = new RadialTextsView(mContext);
		radialMinutesText.setLayoutParams(new FrameLayout.LayoutParams(
				radialWidth, radialWidth, Gravity.CENTER));
		Resources res = mContext.getResources();
		int[] minutes = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55 };
		String[] minutesTexts = new String[12];
		for (int i = 0; i < 12; i++) {
			minutesTexts[i] = String.format("%02d", minutes[i]);
		}
		radialMinutesText.initialize(res, minutesTexts, null, false, true);
		radialMinutesText.invalidate();
		minutesPicker = new MinutesPicker(mContext,radialWidth);
		minutesPicker.setLayoutParams(new FrameLayout.LayoutParams(radialWidth,
				radialWidth, Gravity.CENTER));
		minutesPicker.setOnSeekBarChangeListener(oncircleListener);
		minutesPicker.setBackgroundColor(color.holo_blue_bright);
		minutesLayout.addView(minutesPicker);
		minutesLayout.addView(radialMinutesText);
	}

	private void InitHoursLayout() {
		hoursLayout = (FrameLayout) _DIALOG.findViewById(R.id.hours_layout);
		radialHoursTexts = new RadialTextsView(mContext);
		radialHoursTexts.setLayoutParams(new FrameLayout.LayoutParams(
				radialWidth, radialWidth, Gravity.CENTER));

		Resources res = mContext.getResources();
		int[] hours = { 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		String[] hoursTexts = new String[12];
		for (int i = 0; i < 12; i++) {
			hoursTexts[i] = String.format("%d", hours[i]);
		}
		radialHoursTexts.initialize(res, hoursTexts, null, false, true);
		radialHoursTexts.invalidate();
		hourPicker = new HoursPicker(mContext, radialWidth);
		hourPicker.setLayoutParams(new FrameLayout.LayoutParams(radialWidth,
				radialWidth, Gravity.CENTER));
		hourPicker.setOnSeekBarChangeListener(oncircleListener);
		hoursLayout.addView(hourPicker);
		hoursLayout.addView(radialHoursTexts);
	}

	private void tryVibrate() {
		if (mVibrate && mVibrator != null) {
			long now = SystemClock.uptimeMillis();
			// We want to try to vibrate each individual tick discretely.
			if (now - mLastVibrate >= 125) {
				mVibrator.vibrate(5);
				mLastVibrate = now;
			}
		}
	}

	private void tryVibrate(int val) {
		if (mVibrate && mVibrator != null) {
			mVibrator.vibrate(val);
		}
	}

	/** Gets Window width */
	private int getWindowWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		(mActivity).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	OnCircleSeekBarChangeListener oncircleListener = new OnCircleSeekBarChangeListener() {
		@Override
		public void onProgressChanged(Object seekBar, int progress,
				boolean fromHour) {
			tryVibrate();
			if (fromHour) {
				set_HOURS(progress);
			} else {
				set_MINUTES(progress);
			}
			if (listener != null)
				listener.onProgressChanged(get_HOURS(), get_MINUTES());
		}

		@Override
		public void onScrollRelease(HoursPicker seekBar, int progress,
				boolean fromUser) {
			switchToMinutesLayout();
		}
	};

	private int get_HOURS() {
		return _HOURS;
	}

	private void set_HOURS(int _HOURS) {
		if (_HOURS < 0) {
			_HOURS = 0;
		}
		if (_HOURS > 11) {
			_HOURS = 0;
		}
		hoursTextView.setText(getPadding(_HOURS));
		this._HOURS = _HOURS;
	}

	private int get_MINUTES() {
		return _MINUTES;
	}

	private void set_MINUTES(int _MINUTES) {
		if (_MINUTES < 0) {
			_MINUTES = 0;
		}
		if (_MINUTES >= 60) {
			_MINUTES = 59;
		}
		minutesTextView.setText(getPadding(_MINUTES));
		this._MINUTES = _MINUTES;
	}

	private CharSequence getPadding(int _VALUE) {
		if (_VALUE < 10) {
			return "0" + _VALUE;
		}
		return _VALUE + "";
	}

	/** Gets Height width */
	private int getWindowHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		(mActivity).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}
}
