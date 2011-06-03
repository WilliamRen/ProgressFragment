package com.testing.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

/**
 * Test Harness for ProgressFragment.
 */
public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load our Layout, however you could easily instantiate
        // the Fragments in XML.
        setContentView(R.layout.main);
        
        // Use a list of items from our resources.
        String[] items = getResources().getStringArray(R.array.progressItems);
        
        // Replace one of our frames with the ProgressFragment.
        ProgressFragment pFrag = ProgressFragment.newInstance(items, true, "mySaveFile");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        
        ft.replace(R.id.frameLeft, pFrag);
        ft.commit();       
    }
}