package com.biskra.ansej.v3.kick;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.biskra.ansej.v3.R;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

/**
 * Created by ACER User on 13/06/2017.
 */

public class promoteurAdapter extends RecyclerView.Adapter<promoteurAdapter.ViewHolder> {

    public String id = "2040014";
    private List<ListItem> listItems;
    private Context context;
    private int var;
    private Connection connect;

    public promoteurAdapter(List<ListItem> listItems, int a, Context context, Connection conn) {
        this.listItems = listItems;
        this.context = context;
        this.var = a;
        this.connect = conn;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (Locale.getDefault().getLanguage().equals("ar")) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promoteur_inf_ar, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promoteur_inf, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ListItem listItem = listItems.get(position);
        TypedValue typedValue = new TypedValue();

        Picasso.with(context).load(listItem.getBgp()).into(holder.bgp);
        int[] colors = new int[]{
                Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN,
                Color.DKGRAY, Color.MAGENTA, Color.CYAN
        };
        String[] colors2 = new String[]{
                "#bb4d00", "#f57c00"
        };
        holder.mainp.setBackgroundColor(Color.parseColor("#cc333333"));
        double a = Math.random() * 7;
        int d = ((int) a);
        holder.colorLeft.setBackgroundColor(Color.parseColor(listItem.getThird().split("_")[position % 2]));
        holder.pname.setText(listItem.getFirst());
        holder.pname.setTextSize(20);
        holder.pact.setTextColor(Color.WHITE);
        holder.pact.setText(listItem.getSuppName());
        holder.ptel.setText(listItem.getSecond());
        holder.callbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tel = "tel:" + listItem.getSecond();
                Intent callI = new Intent(Intent.ACTION_CALL);
                callI.setData(Uri.parse(tel));
                int checkPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{Manifest.permission.CALL_PHONE},
                            1);
                } else {
                    context.startActivity(callI);
                }
            }
        });

        holder.warningbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog(listItem);
            }
        });
    }

    public void ShowDialog(final ListItem listItem) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView;
        if (!Locale.getDefault().getLanguage().equals("ar")) {
            mView = li.inflate(R.layout.warning, null);
        } else {
            mView = li.inflate(R.layout.warning_ar, null);
        }
        final Button valid = (Button) mView.findViewById(R.id.valid);
        final ImageButton close = (ImageButton) mView.findViewById(R.id.close);
        final Spinner dropdown = (Spinner) mView.findViewById(R.id.spinner);
        String[] items = new String[]{context.getResources().getString(R.string.choix1), context.getResources().getString(R.string.choix2)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);
        builder.setView(mView);
        final AlertDialog al = builder.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                al.dismiss();
            }
        });
        connect = null;
        /*try {
            connect = new ConnectionClass().CONN(getString(R.string.data0),getString(R.string.data1),getString(R.string.data2),getString(R.string.data3));
        } catch (Exception e) {
            connect = null;
        }*/

        valid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int alpha = 0;
                int beta = 0;
                if (dropdown.getSelectedItemPosition() == 1) {
                    alpha = 1;
                } else {
                    beta = 1;
                }
                String query = "IF NOT EXISTS(SELECT * FROM FEEDBACK WHERE Id_Dossier = " + listItem.idDossier() + ")" +
                        "    BEGIN" +
                        "        INSERT INTO FEEDBACK(ME_Existe,TEL_Erroné,Id_Dossier)" +
                        "        VALUES(" + alpha + "," + beta + "," + listItem.idDossier() + ")" +
                        "    END" +
                        "    ELSE" +
                        "    BEGIN" +
                        "        UPDATE FEEDBACK" +
                        "        SET ME_Existe = (ME_Existe+" + alpha + ")," +
                        "        TEL_Erroné = (TEL_Erroné+" + beta + ")" +
                        "        WHERE Id_Dossier=" + listItem.idDossier() +
                        "    END";

                Statement stmt = null;
                try {
                    stmt = connect.createStatement();
                    String sql = "UPDATE DETAIL_DOSSIER " +
                            "SET tel_errone = tel_errone+1 WHERE Id_Dossier = " + listItem.idDossier() + "'";
                    stmt.executeQuery(query);

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (alpha == 1) {
                    Toast.makeText(context, R.string.choix2, Toast.LENGTH_SHORT).show();
                }
                if (beta == 1) {
                    Toast.makeText(context, R.string.choix1, Toast.LENGTH_SHORT).show();
                }
                al.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;
        public TextView pname, pact;
        public TextView ptel;
        public FloatingActionButton callbtn, warningbtn;
        public ImageView bgp;
        public LinearLayout mainp, colorLeft;

        public ViewHolder(View itemView) {
            super(itemView);
            pname = (TextView) itemView.findViewById(R.id.pname);
            pact = (TextView) itemView.findViewById(R.id.activite);
            ptel = (TextView) itemView.findViewById(R.id.ptel);
            bgp = (ImageView) itemView.findViewById(R.id.bgp);
            mainp = (LinearLayout) itemView.findViewById(R.id.mainp);
            colorLeft = (LinearLayout) itemView.findViewById(R.id.colorLeft);
            cardView = (CardView) itemView.findViewById(R.id.cviewpro);
            callbtn = (FloatingActionButton) itemView.findViewById(R.id.callbtn);
            warningbtn = (FloatingActionButton) itemView.findViewById(R.id.warningbtn);
        }
    }
}
