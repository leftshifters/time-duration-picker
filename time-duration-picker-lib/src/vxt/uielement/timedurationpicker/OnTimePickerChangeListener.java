package vxt.uielement.timedurationpicker;

import android.app.Dialog;

public interface OnTimePickerChangeListener {
	/**
	 * @return hours
	 * @return minutes
	 */
	public abstract void onProgressChanged(int hours, int minutes);
	
	public abstract void onSubmitClicked(int hours, int minutes,Dialog dialog);

}