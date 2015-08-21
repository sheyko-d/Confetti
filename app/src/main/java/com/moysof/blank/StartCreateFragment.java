package com.moysof.blank;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moysof.blank.util.ProgressHttpEntityWrapper;
import com.moysof.blank.util.Util;
import com.moysof.blank.view.DrawingView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;

public class StartCreateFragment extends Fragment {

    private static int sCardNum;
    private static String sGameId;
    private static String sPlayerId;
    private static int sNumberCards;
    private ImageLoader mImageLoader;
    private View mRootView;
    private DrawingView mDrawingView;
    private static boolean sIsHost;
    private static String sName;
    private static String sUsername;
    private static String sAvatar;
    private static int sPendingCount;
    public static String sTeams;

    public static StartCreateFragment newInstance(int cardNum, String gameId, String playerId,
                                                  Boolean isHost, String name, String username,
                                                  String avatar, int pendingCount,
                                                  int numberCards) {
        sCardNum = cardNum;
        sGameId = gameId;
        sPlayerId = playerId;
        sIsHost = isHost;
        sName = name;
        sUsername = username;
        sAvatar = avatar;
        sPendingCount = pendingCount;
        sNumberCards = numberCards;

        return new StartCreateFragment();
    }

    public StartCreateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_start_create, container, false);

        View acceptBtn = mRootView.findViewById(R.id.start_create_accept_btn);
        View clearBtn = mRootView.findViewById(R.id.start_create_clear_btn);
        mDrawingView = (DrawingView) mRootView.findViewById(R.id.start_create_drawing_view);
        TextView pageNumTxt = (TextView) mRootView.findViewById(R.id.start_create_page_num);

        ((TextView) mRootView.findViewById(R.id.start_create_name_txt))
                .setText(sName);
        ((TextView) mRootView.findViewById(R.id.start_create_username_txt))
                .setText(sUsername);
        initAvatar();

        pageNumTxt.setText("Card " + sCardNum + " of " + sNumberCards);

        acceptBtn.setOnClickListener(mCardClickListener);
        clearBtn.setOnClickListener(mCardClickListener);

        StartGameActivity.sViewPager.setSwipeEnabled(false);

        return mRootView;
    }

    View.OnClickListener mCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.start_create_accept_btn) {
                accept();
            } else {
                clear();
            }
        }
    };

    private void accept() {
        if (mDrawingView.isEmpty()) {
            Toast.makeText(getActivity(), "Card can't be empty", Toast.LENGTH_LONG).show();
        } else if (saveCard()) {
            if (StartGameActivity.sViewPager.getCurrentItem() == sNumberCards) {
                uploadCardsToServer();
            } else {
                openNextPage();
            }
        } else {
            Toast.makeText(getActivity(), "Can't save card", Toast.LENGTH_LONG).show();
        }
    }

    private void openNextPage() {
        StartGameActivity.sViewPager
                .setCurrentItem(StartGameActivity.sViewPager.getCurrentItem() + 1);
    }

    private void uploadCardsToServer() {
        new UploadImagesTask().execute();
    }

    private class UploadImagesTask extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog mDialog;
        private String mResult;

        @Override
        protected void onPreExecute() {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setMessage("Uploading cards");
            mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mDialog.show();
        }

        @Override
        protected Boolean doInBackground(final Void... params) {

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            int cardsCount = sNumberCards;
            for (int i = 0; i < cardsCount; i++) {
                builder.addPart("card" + i, new FileBody(new File(Environment
                        .getExternalStorageDirectory() + "/.WhatTheBlank/card" + i + ".png")));
            }
            builder.addPart("game_id", new StringBody(sGameId, ContentType.TEXT_PLAIN));
            builder.addPart("number_cards", new StringBody(sNumberCards + "",
                    ContentType.TEXT_PLAIN));

            String playerId;
            try {
                playerId = StartGameActivity.sPendingPlayers
                        .getJSONObject(StartGameActivity.sCurrentPlayerNum).getString("id");
            } catch (Exception e) {
                playerId = "";
            }
            builder.addPart("player_id", new StringBody(playerId,
                    ContentType.TEXT_PLAIN));

            ProgressHttpEntityWrapper.ProgressCallback progressCallback
                    = new ProgressHttpEntityWrapper.ProgressCallback() {

                @Override
                public void progress(float progress) {
                    mDialog.setProgress((int) progress);
                }

            };

            try {
                HttpClient client = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(Util.URL_UPLOAD_CARDS);

                request.setEntity(new ProgressHttpEntityWrapper(builder.build(), progressCallback));

                HttpResponse response = client.execute(request);
                HttpEntity httpEntity = response.getEntity();

                mResult = EntityUtils.toString(httpEntity, "UTF-8");
                Util.Log("result = " + mResult);
                return true;
            } catch (Exception e) {
                Util.Log("Server error = " + e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mDialog.setProgress(100);
            mDialog.cancel();
            if (!result) {
                Toast.makeText(getActivity(), "Can't upload cards", Toast.LENGTH_LONG).show();
            } else {
                if (sIsHost && sPendingCount != (StartGameActivity.sCurrentPlayerNum + 1)) {
                    StartGameActivity.sCurrentPlayerNum++;
                    StartGameActivity.sViewPager.setAdapter(StartGameActivity.sAdapter);
                    StartGameActivity.sViewPager
                            .setCurrentItem(1);
                } else {
                    JSONArray teams;
                    try {
                        sTeams = new JSONObject(mResult).getString("teams");
                        teams = new JSONObject(mResult).getJSONArray("teams");
                        for (int i = 0; i < teams.length(); i++) {
                            StartWaitingFragment.addTeamCircle(Color.parseColor("#" + teams
                                    .getJSONObject(i).getString("color")));
                            if (i < teams.length() - 1) {
                                StartWaitingFragment.addTeamDivider();
                            }
                        }
                    } catch (Exception e) {
                        Util.Log("Can't get teams = " + e);
                    }

                    openNextPage();
                }
            }
        }
    }

    private Boolean saveCard() {
        File myDir = new File(Environment.getExternalStorageDirectory() + "/.WhatTheBlank/");
        if (!myDir.exists()) {
            if (!myDir.mkdirs()) {
                Util.Log("Can't create folder");
            }
        }
        String fileName = "card" + (StartGameActivity.sViewPager.getCurrentItem() - 1) + ".png";
        File file = new File(myDir, fileName);

        if (file.exists()) file.delete();

        try {
            FileOutputStream out = new FileOutputStream(file);
            getBitmapFromView(mDrawingView).compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();

            return true;
        } catch (Exception e) {
            Util.Log("Saving card exception = " + e);
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void clear() {
        mDrawingView.clear();
    }

    private void initAvatar() {
        ImageView avatarImg = (ImageView) mRootView.findViewById
                (R.id.start_create_avatar_img);

        if (!TextUtils.isEmpty(sAvatar)) {
            DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder().cacheInMemory(true)
                    .cacheOnDisk(true).showImageOnFail(R.drawable.avatar_placeholder)
                    .displayer(new FadeInBitmapDisplayer(250, true, false, false))
                    .showImageOnLoading(android.R.color.white)
                    .build();
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getActivity()).defaultDisplayImageOptions(defaultOptions).build();
            ImageLoader.getInstance().init(config);
            mImageLoader = ImageLoader.getInstance();
            mImageLoader.displayImage(sAvatar, avatarImg, defaultOptions);
        } else {
            avatarImg.setImageResource(R.drawable.avatar_placeholder);
        }
    }

}