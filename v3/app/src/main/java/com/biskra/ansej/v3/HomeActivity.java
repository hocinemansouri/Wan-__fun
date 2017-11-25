package com.biskra.ansej.v3;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.biskra.ansej.v3.kick.IntroActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout l;
    private ImageView logo, k, h, i, d, m, a;
    private TextView tv, tv2;
    private Context context;
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, R.string.msg_paysage, Toast.LENGTH_LONG).show();
        }
        context = this;
        l = (LinearLayout) findViewById(R.id.joke);
        logo = (ImageView) findViewById(R.id.logoansej);
        tv = (TextView) findViewById(R.id.welcome);
        tv2 = (TextView) findViewById(R.id.welcome2);
        videoView = (VideoView) findViewById(R.id.videoView);


        Uri path = Uri.parse("android.resource://com.biskra.ansej.v3/" + R.raw.intro);
        videoView.setZOrderOnTop(true);
        videoView.setVideoURI(path);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                tv2.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn).playOn(tv2);
            }
        });
        YoYo.with(Techniques.FadeIn).delay(50).duration(50).repeat(1).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                logo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(l);
        YoYo.with(Techniques.FadeInUp).delay(100).duration(1000).playOn(logo);
        YoYo.with(Techniques.FadeInUp).delay(100).duration(1000).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tv.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeInUp).duration(150).withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //k.setVisibility(View.VISIBLE);
                        videoView.setVisibility(View.VISIBLE);
                        YoYo.with(Techniques.FadeInUp).duration(500).playOn(videoView);
                        videoView.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).playOn(tv);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).playOn(logo);

        Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(8000);
                    Intent i;
                    if (checkVisited() == 0) {
                        setVisited();
                        i = new Intent(getApplicationContext(), IntroActivity.class);
                    } else {
                        i = new Intent(getApplicationContext(), MainActivity.class);
                    }
                    startActivity(i);
                    Activity activity = (Activity) context;
                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                    finish();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        };
        myThread.start();
    }

    private int checkVisited() {
        SharedPreferences sharedPreferences = getSharedPreferences("introManager", MODE_PRIVATE);
        int visited = sharedPreferences.getInt("visited", 0);
        return visited;
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume");
    }

    private void setVisited() {
        SharedPreferences sharedPreferences = getSharedPreferences("introManager", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("visited", 1);
        editor.apply();
    }
}
