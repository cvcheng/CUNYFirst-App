package com.cunyfirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Dashboard extends Activity {
    private RecyclerView myRecyclerView1;
    private ArrayList<event> myDataset = new ArrayList<>();
    private RecyclerView.Adapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BootstrapButton viewClasses = (BootstrapButton) findViewById(R.id.button1);
        viewClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, Classes.class);
                startActivity(intent);
            }
        });

        BootstrapButton viewInfo = (BootstrapButton) findViewById(R.id.button2);
        viewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, MyInfo.class);
                startActivity(intent);
            }
        });

        BootstrapButton viewCourseHistory = (BootstrapButton) findViewById(R.id.button3);
        viewCourseHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, CourseHistory.class);
                intent.putExtra("courseHistory", getIntent().getStringExtra("courseHistory"));
                startActivity(intent);
            }
        });

        BootstrapButton logOut = (BootstrapButton) findViewById(R.id.button4);
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Dashboard.this);;
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("loggedIn", false);
                editor.commit();

                Intent intent = new Intent(Dashboard.this, Login.class);
                startActivity(intent);
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //swipeRefreshLayout.setRefreshing(false);
            }
        });

        myRecyclerView1 = (RecyclerView) findViewById(R.id.recyclerView1);
        myRecyclerView1.setHasFixedSize(true); //use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        myRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
        myRecyclerView1.setAdapter(null);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document doc = Jsoup.connect("http://www.cuny.edu/academics/calendars.html").timeout(10*1000).get(); //sets timeout to 10 seconds

                    final String source = doc.toString();

                    //parse today's date in cuny's date format
                    final DateFormat df = new SimpleDateFormat("MMMM d"); //September 3
                    String todayString = df.format(Calendar.getInstance(Locale.ENGLISH).getTime());
                    final Date today = df.parse(todayString);

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Matcher dateMatches = Pattern.compile("<td><strong>(.*\\d)").matcher(source); //matches the dates
                            Matcher dayMatches = Pattern.compile("<\\/strong><\\/td>\\s*<td>(.*)<\\/td>").matcher(source); //matches the days
                            Matcher descMatches = Pattern.compile("<td>(.*)<\\/td>\\s*<\\/tr>").matcher(source); //matches the descriptions
                            int totalUsed = 0;
                            //there are more dayMatches and descMatches than dateMatches so the and statements cause it to stop when dateMatches finish
                            while (dateMatches.find() && dayMatches.find() && descMatches.find() && totalUsed < 4) {
                                Log.e(".",dateMatches.group(1) + ", " + dayMatches.group(1)+", "+ descMatches.group(1));
                                String[] removedComma = dateMatches.group(1).split(Pattern.quote(",")); //remove comma
                                String[] removedDashAndComma = removedComma[0].split(Pattern.quote("-")); //remove dashes
                                try {
                                    Date cunyDate = df.parse(removedDashAndComma[0]); //parse the polished date from cuny
                                    if (cunyDate.equals(today) || cunyDate.after(today) || removedComma.length == 2) { //if it's today or later or contains a year
                                        myDataset.add(new event(dateMatches.group(1) + ", " + dayMatches.group(1), descMatches.group(1)));
                                        totalUsed++;
                                    }
                                }
                                catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (myDataset.isEmpty())
                                myDataset.add(new event("No announcements found.", ""));
                            myAdapter = new CardViewDataAdapter(myDataset);
                            myRecyclerView1.setAdapter(myAdapter);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public class CardViewDataAdapter extends RecyclerView.Adapter<CardViewDataAdapter.ViewHolder> {
        public ArrayList<event> myDataset = new ArrayList<>();

        public CardViewDataAdapter(ArrayList<event> myDataset) {
            this.myDataset = myDataset;
        }
        // Create new views (invoked by the layout manager)
        @Override
        public CardViewDataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dates_cardview_row, null);
            // create ViewHolder
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }
        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            viewHolder.textView2.setText(myDataset.get(position).getTitle());
            viewHolder.textView8.setText(myDataset.get(position).getDescription());
        }
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return myDataset.size();
        }
        // inner class to hold a reference to each item of RecyclerView
        // should be static
        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView2;
            public TextView textView8;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                textView2 = (TextView) itemLayoutView.findViewById(R.id.textView2);
                textView8 = (TextView) itemLayoutView.findViewById(R.id.textView8);
            }
        }
    }

    private class event {
        private String title;
        private String description;

        private event(String title, String description) {
            this.title = title;
            this.description = description;
        }
        private String getTitle() {
            return title;
        }
        private String getDescription() {
            return description;
        }
    }
}

    /*private class listAdapter1 extends ArrayAdapter<announcement> {
        public listAdapter1() {
            super(Dashboard.this, R.layout.dates_cardview_row, announcements);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null)
                itemView = getLayoutInflater().inflate(R.layout.dates_cardview_row, parent, false);
            TextView date = (TextView) itemView.findViewById(R.id.textView2);
            TextView event = (TextView) itemView.findViewById(R.id.textView8);
            announcement currentAnnounce = announcements.get(position);
            date.setText(currentAnnounce.getDayDate());
            event.setText(currentAnnounce.getEvent());
            return itemView;
        }
    }*/
