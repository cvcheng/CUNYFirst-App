package com.cunyfirst;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Classes extends Activity {
    private RecyclerView mRecyclerView3;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);

        mRecyclerView3 = (RecyclerView) findViewById(R.id.recyclerView3);
        mRecyclerView3.setHasFixedSize(true); //use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView3.setLayoutManager(mLayoutManager);

        /* get saved preferences */
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json;
        //fetch saved term names
        Type stringType = new TypeToken<ArrayList<String>>() {}.getType();
        json = sharedPrefs.getString("termNames", null);
        final ArrayList<String> termNames = gson.fromJson(json, stringType);
        //fetch saved term 1 classes
        Type arrayListType = new TypeToken<ArrayList<Course>>() {}.getType();
        json = sharedPrefs.getString("term1Classes", null);
        final ArrayList<Course> term1Classes = gson.fromJson(json, arrayListType);
        //fetch saved term 2 classes
        json = sharedPrefs.getString("term2Classes", null);
        final ArrayList<Course> term2Classes = gson.fromJson(json, arrayListType);

        //set spinner items
        //error at this line
        ArrayAdapter<String> arr = new ArrayAdapter<>(Classes.this, android.R.layout.simple_spinner_item, termNames);
        arr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(arr);

        //set classes in listview
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0)
                    mAdapter = new CardViewDataAdapter(term1Classes);
                if (pos == 1)
                    mAdapter = new CardViewDataAdapter(term2Classes);
                mRecyclerView3.setAdapter(mAdapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) { }
        });
    }

    public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder> {
        public ArrayList<Course> mDataset;

        public CardViewDataAdapter(ArrayList<Course> myDataset) {
            mDataset = myDataset;
        }
        @Override
        public CardViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.classes_cardview_row, null);
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.courseName.setText(mDataset.get(position).getTitle());
            viewHolder.courseSection.setText(mDataset.get(position).getSection());
            viewHolder.courseTime.setText(mDataset.get(position).getTime());
            viewHolder.courseRoom.setText(mDataset.get(position).getRoom());
            viewHolder.courseProf.setText(mDataset.get(position).getProf());
            viewHolder.courseCredits.setText(mDataset.get(position).getCredits());
            viewHolder.v.setBackgroundColor(mDataset.get(position).getColor());
        }
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView courseName;
            public TextView courseSection;
            public TextView courseTime;
            public TextView courseRoom;
            public TextView courseProf;
            public TextView courseCredits;
            public View v;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                courseName = (TextView) itemView.findViewById(R.id.textView);
                courseSection = (TextView) itemView.findViewById(R.id.textView5);
                courseTime = (TextView) itemView.findViewById(R.id.textView2);
                courseRoom = (TextView) itemView.findViewById(R.id.textView3);
                courseProf = (TextView) itemView.findViewById(R.id.textView4);
                courseCredits = (TextView) itemView.findViewById(R.id.textView6);
                v = itemView.findViewById(R.id.colored_bar);
            }
        }
    }

    /*private class listAdapter extends ArrayAdapter<Login.Course> {
        private ArrayList<Login.Course> termSelected;

        public listAdapter(ArrayList<Login.Course> term) {
            super(Classes.this, R.layout.classes_cardview_row, term);
            termSelected = term;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.classes_cardview_row, parent, false);
            TextView courseName = (TextView) itemView.findViewById(R.id.textView);
            TextView courseTime = (TextView) itemView.findViewById(R.id.textView2);
            TextView courseRoom = (TextView) itemView.findViewById(R.id.textView3);
            TextView courseProf = (TextView) itemView.findViewById(R.id.textView4);
            TextView courseCredits = (TextView) itemView.findViewById(R.id.textView6);
            View v = itemView.findViewById(R.id.colored_bar);
            Login.Course currentCourse = termSelected.get(position);
            courseName.setText(currentCourse.getTitle());
            courseTime.setText(currentCourse.getTime());
            courseRoom.setText(currentCourse.getRoom());
            courseProf.setText(currentCourse.getProf());
            courseCredits.setText(currentCourse.getCredits());
            v.setBackgroundColor(currentCourse.getColor());
            return itemView;
        }
    }*/
}