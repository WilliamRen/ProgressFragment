package com.testing.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class ProgressFragment extends Fragment implements OnClickListener {
		
	// Progress.
	private ProgressArrayList<ProgressButton> mButtons;
	private int mProgress = 0;
	private int mProgressMax = 0;
	
	// Saving.
	private boolean mSaveProgress = false;
	private String mSaveFile = "";
	
	/* Constants */
	// For Arguments.
	private static final String ARGS_ITEMLIST = "itemList";
	private static final String ARGS_SAVE_PROGRESS = "saveProgress";
	private static final String ARGS_SAVE_FILENAME = "saveFilename";
	
	// For Preferences.
	private static final String PREFS_PROGRESS = "progress";
	
	// For Debug.
	private static final String TAG = "ProgressFragment";
	
	/**
	 * Extended New Instance. Provides ability to save/restore
	 * progress from SharedPreferences file.
	 * 
	 * @param items			Array of Strings to use for Buttons.
	 * @param saveProgress	Whether to save the progress or not.
	 * @param savefile		Filename for the SharedPreferences file.
	 * @return ProgressFragment
	 */
	static ProgressFragment newInstance(String[] items, boolean saveProgress, String savefile) {
		
		ProgressFragment f = new ProgressFragment();
		Bundle args = new Bundle();
		
		args.putStringArray(ARGS_ITEMLIST, items);
		args.putBoolean(ARGS_SAVE_PROGRESS, saveProgress);
		args.putString(ARGS_SAVE_FILENAME, savefile);
		
		f.setArguments(args);
		return f;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mButtons = new ProgressArrayList<ProgressButton>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		// Set our items
		String[] items = getArguments().getStringArray(ARGS_ITEMLIST);
		if(items != null) {
			
			LinearLayout llButtons = new LinearLayout(getActivity());
			llButtons.setOrientation(LinearLayout.VERTICAL);
			
			// Params for our children.
			LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT
			);
			
			llParams.weight = 1;
			
			// Loop through the list.
			int count = items.length;	
			mProgressMax = count;
			for(int i = 0; i < count; i++) {
				
				// Create a button with the text.
				Button myButton = new Button(getActivity());
				myButton.setText(items[i]);
				llButtons.addView(myButton, llParams);
				
				// Create a progress Button.
				mButtons.add(new ProgressButton(items[i], myButton, false, i, this));
			}
			
			return llButtons;			
			
		} else {
			Log.e(TAG, "You didn't supply a list of items!");
			return null;
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.v(TAG, "onActivityCreated()");
		
		// Get the progress.
		if( getArguments().getBoolean(ARGS_SAVE_PROGRESS) ) {
			mSaveProgress = true;
			
			String saveFile = getArguments().getString(ARGS_SAVE_FILENAME);
			if( saveFile.length() > 0 ) {
				mSaveFile = saveFile;
			} else {
				mSaveProgress = false;
			}
		}
		
		// Reload the progress from save file.
		if(mSaveProgress) {
			// Get our preferences
			SharedPreferences sf = getActivity().getSharedPreferences(mSaveFile, Activity.MODE_PRIVATE);
			mProgress = sf.getInt(PREFS_PROGRESS, 0);
		}
		
		//mProgress = getArguments().getInt(ARGS_PROGRESS_CURRENT);
		//mProgress = 3;
		updateEnforcement();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		Log.v(TAG, "onPause()");
		
		// Save our current progress.
		SharedPreferences settings = getActivity().getSharedPreferences(mSaveFile, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.putInt(PREFS_PROGRESS, mProgress);
		editor.commit();
	}
	
	@Override
	public void onClick(View v) {
		
		// Get the ID.
		Log.v(TAG, "onClick!");
		int tag = (Integer) v.getTag();
		Log.v(TAG, "Tag: " + tag);
		
		switch(tag) {
			// By Default, we will just enable the next item.
			case 3:
				TestFragment tf = TestFragment.newInstance();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.frameRight, tf);
				ft.commit();
		
			default:
				progressNext(tag);
				break;
		}
	}
	
	public void progressNext(int tag) {
		tag += 1;
	
		if(tag < mButtons.size()) {
			mButtons.setItemEnabled(tag, true);
			mProgress++;
		}
	}
	
	public void  progressPrevious(int tag) {
		if(tag > 0) {
			mButtons.setItemEnabled(tag, false);
			mProgress--;
		}
	}
	
	public void progressSet(int progress) {
		if(progress > 0 && progress < mProgressMax) {
			mProgress = progress;
			updateEnforcement();
		}
	}
	
	private void updateEnforcement() {
		// First progress, make the first button enabled.
		if(mProgress == 0) {
			mButtons.setItemEnabled(0, true);
			
		} else {
			for(int i = 0; i < mProgress; i++) {
				mButtons.setItemEnabled(i, true);
			}
		}
	}
	
	public class ProgressArrayList<E> extends ArrayList<E> {	
		private static final long serialVersionUID = 1L;

		public void setItemOnClickListener(int index, View.OnClickListener listener) {
			ProgressButton pb = (ProgressButton) get(index);
			pb.setOnClickListener(listener);
		}
		
		public void setItemEnabled(int index, boolean enabled) {
			ProgressButton pb = (ProgressButton) get(index);
			pb.setEnabled(enabled);
		}
	}
	
	public class ProgressButton {
		
		@SuppressWarnings("unused")
		private String mText;
		private Button mView;
		private boolean mEnabled;
		
		public ProgressButton(String text, Button view) {
			mText = text;
			mView = view;
			mView.setEnabled(false);
			
			mEnabled = false;
		}
		
		public ProgressButton(String text, Button view, boolean enabled) {
			mText = text;
			mView = view;
			mView.setEnabled(enabled);
			mEnabled = enabled;
		}
		
		public ProgressButton(String text, Button view, boolean enabled, Object tag) {
			mText = text;
			mView = view;
			mView.setEnabled(enabled);
			mView.setTag(tag);
			mEnabled = enabled;
		}
		
		public ProgressButton(String text, Button view, boolean enabled, Object tag, View.OnClickListener listener) {
			mView = view;
			mView.setEnabled(enabled);
			mView.setTag(tag);
			mView.setOnClickListener(listener);
			
			mText = text;
			mEnabled = enabled;
		}		
		
		public boolean isEnabled() {
			return mEnabled;
		}
		
		public void setEnabled(boolean enabled) {
			mView.setEnabled(enabled);
			mEnabled = enabled;
		}
		
		public void setOnClickListener(View.OnClickListener listener) {
			mView.setOnClickListener(listener);			
		}
		
		public Button getView() {
			return mView;
		}
	}	
}
