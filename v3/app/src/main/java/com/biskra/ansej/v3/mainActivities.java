package com.biskra.ansej.v3;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.biskra.ansej.v3.kick.Activities;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class mainActivities extends AppCompatActivity {

    public ListView listView;
    TextView tvSecName;
    TextView tvSecCode;
    Context context;
    int sectorcode;
    Bundle b;
    private Toolbar actionBar;
    private ProgressBar progressBar1Act;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        context = this;
        listView = (ListView) findViewById(R.id.lviewAct);

        b = getIntent().getExtras();

        progressBar1Act = (ProgressBar) findViewById(R.id.progressBar1Act);
        sectorcode = b.getInt("sectorcode");
        String sectorname = b.getString("sectorname");
        String uriFile = b.getString("uri");

        actionBar = (Toolbar) findViewById(R.id.toolbarAct);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Locale.getDefault().getLanguage().equals("ar")) {
            actionBar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        if (Locale.getDefault().getLanguage().equals("ar")) {
            getSupportActionBar().setTitle(sectorname);
        } else {
            getSupportActionBar().setTitle(sectorname.toUpperCase());
        }

        new JSONTask1().execute(uriFile);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_down);
    }

    class JSONTask1 extends AsyncTask<String, String, List<Activities>> {

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
                progressBar1Act.setIndeterminate(true);
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

                    if (sectorcode - 1 == i) {

                        for (int j = 0; j < finalObject.getJSONArray("act").length(); j++) {
                            Activities activities = new Activities();
                            activities.setDes_act(finalObject.getJSONArray("act").getJSONObject(j).getString("des"));
                            activities.setDes_act_ar(finalObject.getJSONArray("act").getJSONObject(j).getString("des_ar"));
                            if (finalObject.getJSONArray("act").getJSONObject(j).getString("imgact").equals("/")) {
                                activities.setImgurl("https://raw.githubusercontent.com/hocinemansouri/Wan-__fun/master/default.jpg");
                            } else {
                                activities.setImgurl(finalObject.getJSONArray("act").getJSONObject(j).getString("imgact"));
                            }
                            activities.setSectorCode(finalObject.getJSONArray("act").getJSONObject(j).getInt("code"));
                            activitiesList.add(activities);
                        }
                    }

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
            progressBar1Act.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(List<Activities> s) {
            super.onPostExecute(s);
            progressBar1Act.setVisibility(View.INVISIBLE);
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
                    Intent i = new Intent(context, promoteurs.class);
                    String acts = "";
                    String actsNames = "";

                    String color1 = b.getString("color1");
                    String color2 = b.getString("color2");

                    i.putExtra("acts", activitiesList.get(position).getSectorCode());


                    if (Locale.getDefault().getLanguage().equals("ar")) {
                        i.putExtra("actname", activitiesList.get(position).getDes_act_ar());
                    } else {
                        i.putExtra("actname", activitiesList.get(position).getDes_act());
                    }

                    i.putExtra("actimg", activitiesList.get(position).getImgurl());

                    i.putExtra("color1", color1);
                    i.putExtra("color2", color2);


                    activity.startActivity(i);
                    activity.overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
                }
            });


            return convertView;
        }
    }

}
