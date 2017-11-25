package com.biskra.ansej.v3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biskra.ansej.v3.kick.ConnectionClass;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class config extends AppCompatActivity {
    private LinearLayout tv, main,apropos;
    private TextView textView, version;
    private Context context;
    private Toolbar toolbar;
    private EditText et;
    private Connection connect;
    final static String TAG = config.class.getName();
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        context = this;
        toolbar = (Toolbar) findViewById(R.id.Ctoolbar);
        textView = (TextView) findViewById(R.id.avis);
        version = (TextView) findViewById(R.id.version);
        toolbar.setTitle(R.string.param);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        tv = (LinearLayout) findViewById(R.id.selectedAct);
        main = (LinearLayout) findViewById(R.id.main_id_config);
        apropos = (LinearLayout) findViewById(R.id.apropos);

        if (Locale.getDefault().getLanguage().equals("ar")) {
            main.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        version.setText(checkVersion());
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToOthers();//shareApplication();
            }
        });

        apropos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View mView = getLayoutInflater().inflate(R.layout.apropos, null);
                final TextView propostext = (TextView) mView.findViewById(R.id.propostext);
                propostext.setText(ReadFile(context));

                final Button manuel = (Button) mView.findViewById(R.id.fermer);
                final ImageButton close = (ImageButton) mView.findViewById(R.id.close);
                builder.setView(mView);
                final AlertDialog al = builder.show();
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        al.dismiss();
                    }
                });

                manuel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            al.dismiss();
                        }
                });
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final View mView = getLayoutInflater().inflate(R.layout.avis, null);
                final Button manuel = (Button) mView.findViewById(R.id.valid);
                final ImageButton close = (ImageButton) mView.findViewById(R.id.close);
                final EditText et = (EditText) mView.findViewById(R.id.avist);
                builder.setView(mView);
                final AlertDialog al = builder.show();
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        al.dismiss();
                    }
                });

                manuel.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onClick(View v) {
                        connect = new ConnectionClass().CONN(getString(R.string.data0), getString(R.string.data1), getString(R.string.data2), getString(R.string.data3));
                        String s = et.getText().toString().replaceAll("'", " ");
                        if (s.isEmpty()) {
                            YoYo.with(Techniques.Shake).duration(500).playOn(al.getWindow().getDecorView());
                            Toast.makeText(context, R.string.avis_vide, Toast.LENGTH_SHORT).show();

                        } else {
                            String query = "INSERT INTO SUGGESTION (Suggestion) VALUES ('" + s + "')";

                            Statement stmt = null;
                            try {
                                stmt = connect.createStatement();
                                stmt.executeQuery(query);
                                //connect.close();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(context, R.string.avis_merci, Toast.LENGTH_SHORT).show();
                            al.dismiss();
                        }

                    }
                });
            }
        });
    }

    public static  String ReadFile(Context context)  {
        String line = null;
        String urigh="";
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        if (Locale.getDefault().getLanguage().equals("ar")) {
            urigh = "https://raw.githubusercontent.com/hocinemansouri/Wan-__fun/master/prpsar.txt";
        }else{
            urigh = "https://raw.githubusercontent.com/hocinemansouri/Wan-__fun/master/prps.txt";
        }

        URL url = null;
        try {
            url = new URL(urigh);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream stream = null;
        try {
            stream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer buffer = new StringBuffer();
        StringBuilder stringBuilder = new StringBuilder();
        line = "";

        try {
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + System.getProperty("line.separator"));
                //buffer.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        line = stringBuilder.toString();
        return line;
    }

    private String checkVersion() {
        SharedPreferences sharedPreferences = getSharedPreferences("version", MODE_APPEND);
        String set = sharedPreferences.getString("version", "1.0.0");
        return set;
    }

    private String getDownloadLink() {
        SharedPreferences sharedPreferences = getSharedPreferences("download", MODE_APPEND);
        String set = sharedPreferences.getString("download", "Lien non disponible..");
        return set;
    }

    private void sendToOthers() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        if (Locale.getDefault().getLanguage().equals("ar")) {
            sendIntent.putExtra(Intent.EXTRA_TEXT, "انني استعمل تطبيق خدمة لتسهيل البحث عن حرفيين دون عناء البحث و تضييع الوقت..\nنزله الان على هاتفك - رابط التحميل في الاسفل - و جربه انت ايضا\n" +
                    getDownloadLink());
        } else {
            sendIntent.putExtra(Intent.EXTRA_TEXT, "J'utilise l'application \"Khidma\" pour faciliter la recherche d'artisans sans problème de recherche et de perte de temps.\nTéléchargez-le maintenant sur votre téléphone - le lien ci-dessous - et essayez-le aussi\n" +
                    getDownloadLink());
        }
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
