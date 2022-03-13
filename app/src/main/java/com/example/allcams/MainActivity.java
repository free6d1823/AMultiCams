package com.example.allcams;

import android.Manifest;
import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.PermissionChecker;

import android.app.DialogFragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    public static final String FRAGMENT_DIALOG = "dialog";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (requestPermissions()) {
            start();
        }/*
         if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }*/
    }
    private void start() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow();
    }
    private boolean requestPermissions() {
        if(PermissionChecker.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) == PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
        )  {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            ConfirmationDialog.newInstance("This app needs the following permissions to run:").show( getFragmentManager(),
                    FRAGMENT_DIALOG);
        } else {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length >=4) {
            if ( grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //showToast("App exits because no using camera permission.");
                ErrorDialog.newInstance("App exits because no using camera permission.")
                        .show(getFragmentManager(), FRAGMENT_DIALOG);
            }
            else if(grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                //showToast("App exits because no audio recording permission.");
                ErrorDialog.newInstance("App exits because  no audio recording permission.")
                        .show(getFragmentManager(), FRAGMENT_DIALOG);
            }
            else if(grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                //showToast("App exits because no read external storage permission.\n" +
                //        "If you had rejected the permissions before, please enable it manually in Settings.");
                ErrorDialog.newInstance("App exits because no read external storage permission." )
                        .show(getFragmentManager(), FRAGMENT_DIALOG);
            }
            else if(grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                //showToast("App exits because no write external storage permission.\n" +
                //        "If you had rejected the permissions before, please enable it manually in Settings.");
                ErrorDialog.newInstance("App exits because no write external storage permission." )
                        .show(getFragmentManager(), FRAGMENT_DIALOG);
            }
            start();
        }
    }
    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
            }
        });
    }
    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {
        private static final String ARG_MESSAGE = "message";
        public static ConfirmationDialog newInstance(String message) {
            ConfirmationDialog dialog = new ConfirmationDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            dialog.setCancelable(false);
            return dialog;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().requestPermissions(new String[]{android.Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO,
                                            Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    0);
                            dismiss();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = getActivity();
                                    dismiss();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            dialog.setCancelable(false);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                            activity.finish();
                        }
                    })
                    .create();
        }

    }
}