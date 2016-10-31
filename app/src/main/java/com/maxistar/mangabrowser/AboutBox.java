package com.maxistar.mangabrowser;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.AttributeSet;

public class AboutBox extends DialogPreference
{
	// This is the constructor called by the inflater
	public AboutBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
 
	@Override
	protected void onPrepareDialogBuilder(AlertDialog.Builder builder){
	    // Data has changed, notify so UI can be refreshed!
		builder.setTitle("About");
		builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
			}
		});
		
		final SpannableString s = 
              new SpannableString("Simple software written by Max Starikov http://maxistar.ru");
		Linkify.addLinks(s, Linkify.WEB_URLS);
		
		builder.setMessage(s);
		builder.setNegativeButton(null, null);
    }
	
}
