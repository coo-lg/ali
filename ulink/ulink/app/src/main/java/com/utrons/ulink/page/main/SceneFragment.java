package com.utrons.ulink.page.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.utrons.ulink.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SceneFragment extends Fragment {


    public SceneFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab_scene, container, false);
    }

}
