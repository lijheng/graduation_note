package com.example.li.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.li.graduation_note.R;


public class GridFragment extends BaseFragment {

    public GridFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initView(View view, Bundle saveInstanceState, ViewGroup container, Context context) {

    }

    @Override
    protected int setChildView() {
        return R.layout.fragment_grid;
    }


    @Override
    protected boolean getIsList() {
        return false;
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void refresh() {

    }
}
