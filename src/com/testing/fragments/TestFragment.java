package com.testing.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Doesn't do anything interesting other than show a test layout. *
 */
public class TestFragment extends Fragment {

	static TestFragment newInstance() {
		return new TestFragment(); 		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.testlayout, container, false);
	}	
}
