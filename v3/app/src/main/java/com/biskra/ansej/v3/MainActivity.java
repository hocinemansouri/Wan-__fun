package com.biskra.ansej.v3;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biskra.ansej.v3.kick.Activities;
import com.biskra.ansej.v3.kick.ConnectionClass;
import com.biskra.ansej.v3.kick.ListItem;
import com.biskra.ansej.v3.kick.madClass;
//import com.biskra.ansej.v3.usageanalysis.SendMailActivity;
import com.biskra.ansej.v3.usageanalysis.SendMailTask;
import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity {

    public static final int progress_bar_type = 0;
    private static final int MY_PERMISSION_REQUEST_LOCATION = 1;
    private static String TAG = "MainActivity";
    public ListView listView;
    public String globallinkJSON;
    protected ProgressBar progressBar11;
    boolean doubleBackToExitPressedOnce = false;
    Connection connect;
    ProgressDialog bar;
    private Context context;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItemsAct;
    private String[] wilayas, communes;
    private int[] communeID;
    private String selected;
    private int indexWilaya;
    private String selectedCommune;
    private int indexCommune;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private ImageView logo_here;
    private RelativeLayout rlayout;
    private ProgressDialog pDialog;
    private int AppVersion = 1;

    //Removable part
    Session session = null;
     //End part

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        context = this;

        if (check() == 0) {
            ShowDialog();

            Properties props = new Properties();
            props.put("mail.smtp.host","smtp.zoho.com");
            props.put("mail.smtp.socketFactory.port","465");
            props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth","true");
            props.put("mail.smtp.port","465");

            session = Session.getDefaultInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("kodalin.api.01@zoho.com","Azerty1194");
                }
            });

            RetrieveFeedTask task = new RetrieveFeedTask();
            task.execute();
        }
        //ShowDialog();

        toolbar = (Toolbar) findViewById(R.id.Mtoolbar);
        rlayout = (RelativeLayout) findViewById(R.id.rlayout);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        progressBar11 = (ProgressBar) findViewById(R.id.progressBar1);
        logo_here = (ImageView) findViewById(R.id.logo_here);
        logo_here.setImageResource(R.drawable.logo_here_1);
        logo_here.setScaleType(ImageView.ScaleType.CENTER);
        //YoYo.with(Techniques.FadeInUp).

        if (Locale.getDefault().getLanguage().equals("ar")) {
            toolbar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            rlayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        animateActionBar(R.drawable.logo_here_112);
        listView = (ListView) findViewById(R.id.lview);


        try {
            connect = new ConnectionClass().CONN(getString(R.string.data0), getString(R.string.data1), getString(R.string.data2), getString(R.string.data3));
        } catch (Exception e) {
            connect = null;
        }
        if (connect == null) {
            progressBar11.setVisibility(View.INVISIBLE);
            Snackbar.make(this.findViewById(R.id.clayout), R.string.noconnection, 10000).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            }).setActionTextColor(Color.YELLOW).show();
        } else {
            checkVersion();

        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (Locale.getDefault().getLanguage().equals("ar")) {
            ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.LEFT | Gravity.BOTTOM;
        } else {
            ((CoordinatorLayout.LayoutParams) fab.getLayoutParams()).gravity = Gravity.RIGHT | Gravity.BOTTOM;
        }


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });


    }

    class RetrieveFeedTask extends AsyncTask<String,String,Void>{
        @Override
        protected Void doInBackground(String... params) {
            try{
                @SuppressLint("WifiManagerLeak") WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                WifiInfo wInfo = wifiManager.getConnectionInfo();
                String macAddress = wInfo.getMacAddress();
                String rec = "kodalin.api.01@gmail.com";
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("kodalin.api.01@zoho.com"));
                message.setSubject("New Connection from "+Build.USER+" MAC:"+macAddress);
                message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(rec));
                message.setContent("Hey, I'm ","text/html; charset=utf-8");

                Transport.send(message);

            }catch(MessagingException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void animateActionBar2(int d) {
        logo_here.setImageResource(d);

        BaseViewAnimator techniques = Techniques.FadeInLeft.getAnimator();
        if (Locale.getDefault().getLanguage().equals("ar")) {
            techniques = Techniques.FadeInRight.getAnimator();
        }

        YoYo.with(techniques)
                .duration(2400)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        BaseViewAnimator techniques2 = Techniques.FadeOutRight.getAnimator();
                        if (Locale.getDefault().getLanguage().equals("ar")) {
                            techniques2 = Techniques.FadeOutLeft.getAnimator();
                        }
                        YoYo.with(techniques2).duration(1200).withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animateActionBar(R.drawable.logo_here_112);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).playOn(logo_here);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).playOn(logo_here);

    }

    private void animateActionBar(int d) {
        logo_here.setImageResource(d);

        BaseViewAnimator techniques = Techniques.FadeInLeft.getAnimator();
        if (Locale.getDefault().getLanguage().equals("ar")) {
            techniques = Techniques.FadeInRight.getAnimator();
        }

        YoYo.with(techniques)
                .duration(2400)
                .interpolate(new AccelerateDecelerateInterpolator())
                .withListener(new Animator.AnimatorListener() {

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                        BaseViewAnimator techniques2 = Techniques.FadeOutRight.getAnimator();
                        if (Locale.getDefault().getLanguage().equals("ar")) {
                            techniques2 = Techniques.FadeOutLeft.getAnimator();
                        }

                        YoYo.with(techniques2).duration(1200).withListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animateActionBar2(R.drawable.logo_here_122);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        }).playOn(logo_here);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).playOn(logo_here);

    }

    private void checkVersion() {

        if (checkConnection()) {
            //connect = new ConnectionClass().CONN();
            int versionCode = 0;
            String appURl = "";
            String linkJSON = "";
            String querycmd = "select * from settings";
            try {
                Statement statement = connect.createStatement();
                ResultSet rs = statement.executeQuery(querycmd);
                ListItem listItem = null;

                while (rs.next()) {
                    versionCode = rs.getInt("version");
                    linkJSON = rs.getString("link_act");
                    globallinkJSON = linkJSON;
                    appURl = rs.getString("link_download");
                    setVersion(rs.getString("version_name"));
                }
                setDownloadURL(appURl);
                PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                if (packageInfo.versionCode < versionCode) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View mView = getLayoutInflater().inflate(R.layout.update, null);
                    final Button update = (Button) mView.findViewById(R.id.update);
                    final ImageButton close = (ImageButton) mView.findViewById(R.id.close);
                    builder.setView(mView);
                    final AlertDialog al = builder.show();
                    final String finalLinkJSON = linkJSON;
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            al.dismiss();
                            new JSONTask().execute(finalLinkJSON);
                        }
                    });
                    final String finalAppURl = appURl;
                    update.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                        @Override
                        public void onClick(View v) {
                            al.dismiss();
                            new DownloadNewVersion().execute(finalAppURl);
                            //new DownloadFileFromURL().execute(finalAppURl);
                            //Toast.makeText(context, R.string.avis_merci, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    new JSONTask().execute(linkJSON);
                }


            } catch (SQLException e) {
                e.printStackTrace();
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkConnection() {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

    public void ShowDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View mView = getLayoutInflater().inflate(R.layout.selectlocation, null);
        final Button manuel = (Button) mView.findViewById(R.id.manuel);
        final ImageButton close = (ImageButton) mView.findViewById(R.id.close);
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
                al.dismiss();
                SelectWilayas();
            }
        });
    }

    public void SelectWilayas() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText(R.string.sel_wilaya);
        title.setTextSize(20);
        title.setPadding(20, 20, 20, 20);
        if (Locale.getDefault().getLanguage().equals("ar")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        builder.setCustomTitle(title);
        indexWilaya = 0;
        wilayas = getFromJSONStringWilaya(R.raw.wilayas2);

        builder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexWilaya = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                indexWilaya = 0;
            }
        });
        builder.setSingleChoiceItems(wilayas, 0, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                indexWilaya = which;
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    SelectCommune((indexWilaya + 2));
                }
            }
        }).setNegativeButton(R.string.retour, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ShowDialog();
            }
        });
        if (Locale.getDefault().getLanguage().equals("ar")) {
            AlertDialog alertDialog = builder.create();
            ListView listView = alertDialog.getListView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                listView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            alertDialog.show();
        } else {
            builder.show();
        }
    }

    public void SelectCommune(final int ID) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView title = new TextView(this);
        // You Can Customise your Title here
        title.setText(R.string.sel_commune);
        title.setTextSize(20);
        title.setPadding(20, 20, 20, 20);
        if (Locale.getDefault().getLanguage().equals("ar")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                title.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
        builder.setCustomTitle(title);
        madClass infos = getFromJSONStringCommunem(R.raw.communes2, ID);
        communes = infos.getNames();
        communeID = infos.getId();
        indexCommune = 0;
        builder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexCommune = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                indexCommune = 0;
            }
        });
        builder.setSingleChoiceItems(communes, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                indexCommune = which;
            }
        });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg = "";
                if (which == 0) {
                    setLoc(communeID[1]);
                }

                setLocW(ID);
                indexWilaya++;
                setLoc(communeID[indexCommune]);
            }
        });
        builder.setNegativeButton(R.string.retour, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SelectWilayas();
            }
        });
        if (Locale.getDefault().getLanguage().equals("ar")) {
            AlertDialog alertDialog = builder.create();
            ListView listView = alertDialog.getListView();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                listView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            alertDialog.show();
        } else {
            builder.show();
        }
    }

    public String[] getFromJSONStringWilaya(int ID) {
        String[] it = new String[48];
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(ID));
            JSONArray m_jArry = obj.getJSONArray("wilaya");
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String formula_value = jo_inside.getString("WILAYA");
                if (Locale.getDefault().getLanguage().equals("ar")) {
                    formula_value = jo_inside.getString("Wilaya_Arab");
                }
                it[i] = formula_value;
                //it.add(listItem);
            }
            return it;
        } catch (JSONException e) {
            e.printStackTrace();
            return it;
        }
    }

    public madClass getFromJSONStringCommunem(int ID, int wID) {
        String[] itC0 = new String[]{};
        int[] id0 = new int[]{};
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(ID));
            JSONArray m_jArry = obj.getJSONArray("commune");
            int alpha = 0;
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String formula_value = jo_inside.getString("NOM_COMMUNE");
                int wilayaID = Integer.parseInt(jo_inside.getString("Id_Wilaya"));
                if (wID == wilayaID) {
                    alpha++;
                }
            }
            String[] itC = new String[alpha];
            int[] id = new int[alpha];
            int gama = 0;
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String formula_value = "";
                if (Locale.getDefault().getLanguage().equals("ar")) {
                    formula_value = jo_inside.getString("Nom_Commune_Arab");
                } else {
                    formula_value = jo_inside.getString("NOM_COMMUNE");
                }
                int wilayaID = Integer.parseInt(jo_inside.getString("Id_Wilaya"));
                int cID = Integer.parseInt(jo_inside.getString("Id_Commune"));
                if (wID == wilayaID) {
                    itC[gama] = formula_value;
                    id[gama] = cID;
                    gama++;
                }
            }
            return new madClass(id, itC);
        } catch (JSONException e) {
            e.printStackTrace();
            return new madClass(id0, itC0);
        }
    }

    public String loadJSONFromAsset(int rawID) {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(rawID);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setLoc(int ID) {
        SharedPreferences sharedPreferences = getSharedPreferences("selectLoc", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("setLoc", ID);
        editor.apply();
    }

    private void setVersion(String version) {
        SharedPreferences sharedPreferences = getSharedPreferences("version", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("version", version);
        editor.apply();
    }

    private void setDownloadURL(String downloadURL) {
        SharedPreferences sharedPreferences = getSharedPreferences("download", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("download", downloadURL);
        editor.apply();
    }

    private void setLocW(int ID) {
        SharedPreferences sharedPreferences = getSharedPreferences("selectWilaya", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("selectWilaya", ID);
        editor.apply();
    }

    private int check() {
        SharedPreferences sharedPreferences = getSharedPreferences("selectLoc", MODE_APPEND);
        int set = sharedPreferences.getInt("setLoc", 0);
        return set;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int res_id = item.getItemId();
        if (res_id == R.id.config) {
            Intent i = new Intent(this, config.class);
            startActivity(i);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.quitemsg, Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.out.println("Paused");
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("Resume");
    }

    void OpenNewVersion(String location) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(location + "app-debug.apk")),
                "application/vnd.android.package-archive");


        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    class JSONTask extends AsyncTask<String, String, List<Activities>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, R.string.waitmsg, Toast.LENGTH_LONG).show();
        }

        @Override
        protected List<Activities> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                progressBar11.setIndeterminate(true);
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                String finalJSON = buffer.toString();
                JSONObject parentObject = new JSONObject(finalJSON);
                JSONArray parentArray = parentObject.getJSONArray("Sectors");

                List<Activities> activitiesList = new ArrayList<>();
                for (int i = 0; i < parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    Activities activities = new Activities();
                    activities.setDes_act(finalObject.getString("sectorsName"));
                    activities.setDes_act_ar(finalObject.getString("ar"));

                    activities.setImgurl(finalObject.getString("imgurl"));
                    activities.setColor1(finalObject.getString("color1"));
                    activities.setColor2(finalObject.getString("color2"));
                    activities.setSectorCode(finalObject.getInt("sectorcode"));


                    activitiesList.add(activities);
                }


                return activitiesList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            progressBar11.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(List<Activities> s) {
            super.onPostExecute(s);
            progressBar11.setVisibility(View.INVISIBLE);
            CustomAdapter mainAdapter2 = new CustomAdapter(getApplicationContext(), R.layout.list_item, s);
            listView.setAdapter(mainAdapter2);
        }
    }

    public class CustomAdapter extends ArrayAdapter {

        private List<Activities> activitiesList;
        private int ressource;
        private LayoutInflater inflater;

        public CustomAdapter(@NonNull Context context, @LayoutRes int resource, List<Activities> objects) {
            super(context, resource, objects);
            activitiesList = objects;
            this.ressource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, @Nullable View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(ressource, null);
            }
            ImageView imageView;
            final TextView textView;
            RelativeLayout relativeLayout;

            imageView = (ImageView) convertView.findViewById(R.id.image);
            textView = (TextView) convertView.findViewById(R.id.textViewHead);
            relativeLayout = (RelativeLayout) convertView.findViewById(R.id.clickedArea);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                textView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            }
            YoYo.with(Techniques.FadeIn).playOn(relativeLayout);

            textView.setText(activitiesList.get(position).getDes_act().toUpperCase());
            if (Locale.getDefault().getLanguage().equals("ar")) {
                textView.setText(activitiesList.get(position).getDes_act_ar());
            }
            Picasso.with(context).load(activitiesList.get(position).getImgurl()).into(imageView);

            textView.setBackgroundColor(Color.parseColor("#99333333"));

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Activity activity = (Activity) context;
                    //Intent i = new Intent(context, promoteurs.class);
                    Intent i = new Intent(context, mainActivities.class);
                    String acts = "";
                    String actsNames = "";

                    i.putExtra("sectorcode", activitiesList.get(position).getSectorCode());
                    if (Locale.getDefault().getLanguage().equals("ar")) {
                        i.putExtra("sectorname", activitiesList.get(position).getDes_act_ar());
                    } else {
                        i.putExtra("sectorname", activitiesList.get(position).getDes_act());
                    }
                    i.putExtra("color1", activitiesList.get(position).getColor1());
                    i.putExtra("color2", activitiesList.get(position).getColor2());
                    i.putExtra("uri", globallinkJSON);


                    activity.startActivity(i);
                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            });


            return convertView;
        }
    }

    class DownloadNewVersion extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            bar = new ProgressDialog(MainActivity.this);
            bar.setCancelable(false);

            bar.setMessage(getString(R.string.startdownload));

            bar.setIndeterminate(true);
            bar.setCanceledOnTouchOutside(false);
            bar.show();

        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);

            bar.setIndeterminate(false);
            bar.setMax(100);
            bar.setProgress(progress[0]);
            String msg = "";
            if (progress[0] > 99) {

                msg = getString(R.string.finishing);

            } else {

                msg = getString(R.string.downloading) + progress[0] + "%";
            }
            bar.setMessage(msg);

        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            bar.dismiss();

            if (result) {

                Toast.makeText(getApplicationContext(), R.string.updone,
                        Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(getApplicationContext(), R.string.tryagain,
                        Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        protected Boolean doInBackground(String... arg0) {
            Boolean flag = false;
            int count = 0;
            try {
                URL url = new URL(arg0[0]);

                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.connect();

                int lenghtOfFile = c.getContentLength();

                // input stream to read file - with 8k buffer

                InputStream is = new BufferedInputStream(url.openStream(), 10 * 1024);

                // Output stream to write file in SD card


                String PATH = Environment.getExternalStorageDirectory() + "/Download/";
                File file = new File(PATH);
                file.mkdirs();

                File outputFile = new File(file, "app-debug.apk");

                if (outputFile.exists()) {
                    outputFile.delete();
                }

                OutputStream output = new FileOutputStream(PATH + "/app-debug.apk");

                byte data[] = new byte[1024];

                long total = 0;
                FileOutputStream fos = new FileOutputStream(outputFile);
                int total_size = 1000000;//size of apk

                byte[] buffer = new byte[1024];
                int len1 = 0;
                int per = 0;
                int downloaded = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloaded += len1;
                    per = downloaded * 100 / total_size;
                    publishProgress(per);
                }
                fos.close();
                is.close();

                OpenNewVersion(PATH);

                flag = true;
            } catch (Exception e) {
                Log.e(TAG, "Update Error: " + e.getMessage());
                flag = false;
            }
            return flag;

        }

    }

}