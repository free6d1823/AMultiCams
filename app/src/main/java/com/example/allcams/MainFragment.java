package com.example.allcams;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import android.app.Fragment;

import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;


public class MainFragment extends Fragment {
    static String TAG = "MainFragment";
    public MainFragment() {
        // Required empty public constructor
    }
    public static Fragment newInstance() {
        MainFragment fragment;
        fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    Point calDivisionsByNumbers(int n)
    {
        Point p = new Point();
        if (n<3) {
            p.y = 1;
            p.x = (n<2)?1:2;
        }else if (n<7) {
            p.y = 2; //column
            p.x = (n<5)?2:3; //row
        } else if (n<13) {
            p.y = 3;
            p.x = (n<10)?3:4;
        } else if (n<17){
            p.y = 4;
            p.x = 4;
        }
        else
            return null; /* not support more than 16 cameras */
        return p;
    }
    int mTotalCams = 0;
    Vector<CameraView> mCamList;
    GridLayout mWorkspace;
    String[] updateCameraList() {
        String[] list = null;
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            list = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_main, container, false);
        mWorkspace  = (GridLayout)view.findViewById(R.id.grid_layout);
        final String[] listId = updateCameraList();
        if (listId != null)
            mTotalCams = listId.length;
        Point p = calDivisionsByNumbers(mTotalCams);
        mWorkspace.setColumnCount(p.x);
        mWorkspace.setRowCount(p.y);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //Remove it here unless you want to get this callback for EVERY
                //layout pass, which can get you into infinite loops if you ever
                //modify the layout from within this method.
                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                //Now you can get the width and height from content

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = view.getHeight();//.getHeight();//displayMetrics.heightPixels;
                int width = view.getWidth();//displayMetrics.widthPixels;
                mCamList = new Vector<CameraView>();
                Log.d(TAG, "gridLayout="+mTotalCams+ " "+ width + "x" +height);
                mTotalCams = 1; //tt
                for (int i=0; i< mTotalCams; i++) {
                    FrameLayout layout = new FrameLayout(getContext());
                    FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
                    layout.setLayoutParams(flp);
                    //layout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
                    CameraView camView = CameraView.newInstance(layout, getContext(), i);

                    GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
                    glp.width = width / mWorkspace.getColumnCount();
                    glp.height = height / mWorkspace.getRowCount();
                    Log.d(TAG, "addView " + i + " " + glp.width + "x" + glp.height);
                    //gridLayout.setLayoutParams(new ViewGroup.LayoutParams(width, height));
                    mWorkspace.addView(layout, i, glp);
                    mCamList.add(camView);
                    if (listId!= null)
                        camView.setCamId(listId[i]);
                }
            }
        });
        return view;
    }

}