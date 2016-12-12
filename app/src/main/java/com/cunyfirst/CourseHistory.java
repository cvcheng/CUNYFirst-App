package com.cunyfirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CourseHistory extends Activity {
    private RecyclerView myRecyclerView1;
    private ArrayList<CourseH> myDataset = new ArrayList<>();
    private RecyclerView.Adapter myAdapter;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_history);

        myRecyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);
        myRecyclerView1.setHasFixedSize(true); //use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView1.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        /* get saved preferences */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(CourseHistory.this);
        Gson gson = new Gson();
        String json;
        Type type = new TypeToken<ArrayList<CourseH>>() {}.getType();
        json = sharedPrefs.getString("courseHistory", null);
        final ArrayList<CourseH> courseHistory = gson.fromJson(json, type);

        myDataset = courseHistory;
        myAdapter = new DataAdapter(myDataset);
        myRecyclerView1.setAdapter(myAdapter);
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        public ArrayList<CourseH> myDataset = new ArrayList<>();

        public DataAdapter(ArrayList<CourseH> myDataset) {
            this.myDataset = myDataset;
        }
        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_history_row, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.textView11.setText(myDataset.get(position).getName());
            viewHolder.textView12.setText(myDataset.get(position).getTerm());
            viewHolder.textView13.setText(myDataset.get(position).getCredit());
            viewHolder.textView14.setText(myDataset.get(position).getProgress());
            viewHolder.textView15.setText(myDataset.get(position).getGrade());
        }
        @Override
        public int getItemCount() {
            return myDataset.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView11;
            public TextView textView12;
            public TextView textView13;
            public TextView textView14;
            public TextView textView15;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                textView11 = (TextView) itemView.findViewById(R.id.textView11);
                textView12 = (TextView) itemView.findViewById(R.id.textView12);
                textView13 = (TextView) itemView.findViewById(R.id.textView13);
                textView14 = (TextView) itemView.findViewById(R.id.textView14);
                textView15 = (TextView) itemView.findViewById(R.id.textView15);
            }
        }
    }
}
