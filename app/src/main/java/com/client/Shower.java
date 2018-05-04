package com.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Shower {

    private Context context;

    Shower(Context context) {
        this.context = context;
    }

    void show(String networkResult, Bitmap image) {
        Toast toast = Toast.makeText(context,
                "How the network recognized this and how it sees it\n" +
                        "It number = " + networkResult, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        LinearLayout toastContainer = (LinearLayout) toast.getView();
        ImageView catImageView = new ImageView(context);
        catImageView.setImageBitmap(Bitmap.createScaledBitmap(
                image,
                400,
                400,
                false));
        toastContainer.addView(catImageView, 0);
        toast.show();
    }
}
