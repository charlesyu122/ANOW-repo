package com.alslimsibqueryu.anow;

import android.view.View;

public interface Row {
	public View getView(View convertView);
    public int getViewType();
}
