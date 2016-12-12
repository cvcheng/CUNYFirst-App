package com.cunyfirst;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyInfo extends Activity {
    private RecyclerView myRecyclerView1, myRecyclerView2, myRecyclerView3;
    private ArrayList<textHolder> myDataset = new ArrayList<>();
    private ArrayList<textHolder> myDataset1 = new ArrayList<>();
    private ArrayList<textHolder> myDataset2 = new ArrayList<>();
    private RecyclerView.Adapter myAdapter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);

        myRecyclerView1 = (RecyclerView) findViewById(R.id.recyclerview1);
        myRecyclerView1.setHasFixedSize(true);
        myRecyclerView1.setLayoutManager(new LinearLayoutManager(this)); //use a linear layout manager
        myRecyclerView1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        myRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerview2);
        myRecyclerView2.setHasFixedSize(true);
        myRecyclerView2.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView2.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        myRecyclerView3 = (RecyclerView) findViewById(R.id.recyclerview3);
        myRecyclerView3.setHasFixedSize(true);
        myRecyclerView3.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView3.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MyInfo.this);

        myDataset.add(new textHolder("ID: " + sharedPrefs.getString("cunyID", "null"), ""));
        myDataset.add(new textHolder("Email: " + sharedPrefs.getString("cunyEmail", "null"), ""));
        myAdapter = new DataAdapter(myDataset);
        myRecyclerView1.setAdapter(myAdapter);

        myDataset1.add(new textHolder(sharedPrefs.getString("youOwe", "null"), ""));
        myAdapter = new DataAdapter(myDataset1);
        myRecyclerView2.setAdapter(myAdapter);

        myDataset2.add(new textHolder("Credits: " + sharedPrefs.getString("credits", "null"), ""));
        myDataset2.add(new textHolder("GPA: " + sharedPrefs.getString("GPA", "null"), ""));
        myAdapter = new DataAdapter(myDataset2);
        myRecyclerView3.setAdapter(myAdapter);
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        public ArrayList<textHolder> myDataset = new ArrayList<>();

        public DataAdapter(ArrayList<textHolder> myDataset) {
            this.myDataset = myDataset;
        }

        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_info_row, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.textView9.setText(myDataset.get(position).getText1());
            viewHolder.textView10.setText(myDataset.get(position).getText2());
        }

        @Override
        public int getItemCount() {
            return myDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView9;
            public TextView textView10;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                textView9 = (TextView) itemView.findViewById(R.id.textView9);
                textView10 = (TextView) itemView.findViewById(R.id.textView10);
            }
        }
    }

    public class textHolder {
        private String text1;
        private String text2;

        private textHolder(String text1, String text2) {
            this.text1 = text1;
            this.text2 = text2;
        }
        public String getText1() {
            return text1;
        }
        public String getText2() {
            return text2;
        }
    }
}
