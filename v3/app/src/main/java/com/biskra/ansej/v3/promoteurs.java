package com.biskra.ansej.v3;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.biskra.ansej.v3.kick.ConnectionClass;
import com.biskra.ansej.v3.kick.ListItem;
import com.biskra.ansej.v3.kick.promoteurAdapter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class promoteurs extends AppCompatActivity {

    List<String> NAMES = new ArrayList();
    Connection connect;
    ResultSet rs;
    List<ListItem> data;
    int act = 0;
    int a = 0;
    private Toolbar actionBar;
    private int color, colorExtras, colorAlpha;
    private RelativeLayout relativeLayout;
    private ImageView imageView, icoView;
    private RecyclerView recyclerView;
    private List<ListItem> listItemsAct;
    private RecyclerView.Adapter adapter;
    private Connection con;
    private ImageView btndisable_location;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promoteurs);
        recyclerView = (RecyclerView) findViewById(R.id.promoteurView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // ToolBar Design
        actionBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Locale.getDefault().getLanguage().equals("ar")) {
            actionBar.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        relativeLayout = (RelativeLayout) findViewById(R.id.list_layout);
        imageView = (ImageView) findViewById(R.id.imgpromoteur);
        icoView = (ImageView) findViewById(R.id.icopromoteur);
        btndisable_location = (ImageView) findViewById(R.id.wilaya);
        final Bundle b = getIntent().getExtras();
        a = b.getInt("type");
        String Act = b.getString("actname");
        String actArab = b.getString("arName");
        final String actimg = b.getString("actimg");
        if (Locale.getDefault().getLanguage().equals("ar")) {
            getSupportActionBar().setTitle(Act);
        } else {
            getSupportActionBar().setTitle(Act.toUpperCase());
        }
        final int acts = b.getInt("acts");
        final String actsNames = b.getString("actname");
        final String color1 = b.getString("color1");
        final String color2 = b.getString("color2");

        data = new ArrayList<>();
        connect = null;
        try {
            connect = new ConnectionClass().CONN(getString(R.string.data0), getString(R.string.data1), getString(R.string.data2), getString(R.string.data3));
        } catch (Exception e) {
            connect = null;
        }
        if (connect == null) {
            data.clear();
            Snackbar.make(this.findViewById(R.id.list_layout), R.string.noconnection, Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } else {
            String querycmd = "select * from DETAIL_DOSSIER where Id_Commune=" + (check()) + " AND Id_Activite=" + acts;
            try {
                Statement statement = connect.createStatement();
                rs = statement.executeQuery(querycmd);
                ListItem listItem = null;

                while (rs.next()) {
                    if (Locale.getDefault().getLanguage().equals("ar")) {
                        listItem = new ListItem(rs.getString("NOMRS_E_Arab"),
                                rs.getString("TEL_P"), color1 + "_" + color2, actsNames, rs.getInt("Id_Dossier"), actimg);
                    } else {
                        listItem = new ListItem(rs.getString("NOMRS_E"),
                                rs.getString("TEL_P"), color1 + "_" + color2, actsNames, rs.getInt("Id_Dossier"), actimg);
                    }
                    data.add(listItem);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (!data.isEmpty()) {
                adapter = new promoteurAdapter(data, a, this, connect);
                recyclerView.setAdapter(adapter);
            } else {
                Snackbar.make(this.findViewById(R.id.list_layout), R.string.nodata, Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
        final Context context = this;
        btndisable_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.clear();
                String querycmd = "SELECT * from DETAIL_DOSSIER where Id_Commune IN ( SELECT Id_Commune" +
                        " from COMMUNE where Id_Wilaya IN ( SELECT Id_Wilaya from COMMUNE where " +
                        "Id_Commune=" + check() + ")) AND Id_Activite=" + acts + " ORDER BY Id_Commune ASC";
                try {
                    Statement statement = connect.createStatement();
                    rs = statement.executeQuery(querycmd);
                    ListItem listItem = null;

                    while (rs.next()) {
                        if (Locale.getDefault().getLanguage().equals("ar")) {
                            listItem = new ListItem(rs.getString("NOMRS_E_Arab"),
                                    rs.getString("TEL_P"), color1 + "_" + color2, actsNames, rs.getInt("Id_Dossier"), actimg);
                        } else {
                            listItem = new ListItem(rs.getString("NOMRS_E"),
                                    rs.getString("TEL_P"), color1 + "_" + color2, actsNames, rs.getInt("Id_Dossier"), actimg);
                        }
                        data.add(listItem);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (!data.isEmpty()) {
                    adapter = new promoteurAdapter(data, a, context, connect);
                    recyclerView.setAdapter(adapter);
                } else {
                }
                Toast.makeText(context, R.string.btndisable_wilaya, Toast.LENGTH_SHORT).show();
            }
        });
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

    private List<ListItem> getData() {
        ArrayList<ListItem> listItems = new ArrayList<>();

        return listItems;
    }

    private int check() {
        SharedPreferences sharedPreferences = getSharedPreferences("selectLoc", MODE_APPEND);
        int set = sharedPreferences.getInt("setLoc", 0);
        return set;
    }

    private int checkw() {
        SharedPreferences sharedPreferences = getSharedPreferences("selectWilaya", MODE_APPEND);
        int set = sharedPreferences.getInt("selectWilaya", 0);
        return set;
    }
}