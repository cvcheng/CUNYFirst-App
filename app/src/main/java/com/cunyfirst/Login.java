package com.cunyfirst;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
//import com.parse.ParsePushBroadcastReceiver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

//import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import com.google.gson.Gson;

import static com.cunyfirst.R.id.webView;

public class Login extends Activity {
    private ProgressDialog dialog;
    private WebView browser;
    private boolean termSelection;
    private boolean term1Done;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = new Intent(getBaseContext(), Dashboard.class);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
        boolean loggedIn = sharedPrefs.getBoolean("loggedIn", false);
        if (loggedIn) {
            startActivity(intent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //TelephonyManager tManager = (TelephonyManager) Login.this.getSystemService(Context.TELEPHONY_SERVICE);
        //String uid = tManager.getDeviceId();
        //<uses-permission android:name="android.permission.READ_PHONE_STATE" />

        final TextView forgotPwd = (TextView) findViewById(R.id.textView5);
        forgotPwd.setMovementMethod(LinkMovementMethod.getInstance());

        final EditText username = (EditText) findViewById(R.id.editText);
        final EditText password = (EditText) findViewById(R.id.editText2);
        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        final SecurePreferences preferences = new SecurePreferences(Login.this, "my-preferences", "bNT*gKTfF6Kp0Cye{3N6,~1BV7rU8;", true);
        if (preferences.containsKey("rememberMe")) {
            username.setText(preferences.getString("userID"));
            password.setText(preferences.getString("userPass"));
            checkBox.setChecked(true);
        }

        browser = (WebView) findViewById(webView);
        browser.getSettings().setJavaScriptEnabled(true);
        /* Register a new JavaScript interface called HTMLOUT */
        browser.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");

        //disable images for faster loading
        browser.getSettings().setLoadsImagesAutomatically(false);

        browser.setWebViewClient(new WebViewClient() {
            boolean timeout = true;

            @Override
            public void onPageFinished(WebView view, String url) {
                timeout = false;

                Log.e(".",url);

                //login page
                if (url.contains("https://home.cunyfirst.cuny.edu/oam/Portal_Login1.html")) {
                    browser.loadUrl("javascript: var x = document.getElementById('cf-login').value = '" + username.getText() + "';");
                    browser.loadUrl("javascript: var x = document.getElementById('password').value = '" + password.getText() + "';");
                    browser.loadUrl("javascript: document.getElementsByName(\"submit\")[0].click();");
                    //javascript: document.getElementsByName("loginform")[0].submit();
                }
                //failed login
                else if (url.contains("https://home.cunyfirst.cuny.edu/oam/InvalidLogin.html")) {
                    dialog.dismiss();
                    Msg("Invalid Login", "Please recheck your username and password. Remember that they are case sensitive and that your account will be locked if you enter too many incorrect passwords.");
                }
                //successfully logged in
                else if (url.contains("https://home.cunyfirst.cuny.edu/psp/cnyepprd/EMPLOYEE/EMPL/h/?tab=DEFAULT") || url.contains("https://impweb.cuny.edu/oam/PWExpiredNotice.html")) {
                    if (checkBox.isChecked()) {
                        //saves info (all puts are automatically committed)
                        preferences.put("userID", username.getText().toString());
                        preferences.put("userPass", password.getText().toString());
                        preferences.put("rememberMe", "true");
                    }
                    else
                        preferences.clear();
                    dialog.dismiss();
                    dialog = ProgressDialog.show(Login.this, "", "Fetching classes...", true);
                    browser.loadUrl("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL?Page=SSR_SSENRL_CART&f");
                }
                else if (url.contains("errorCode=105"))
                {
                    Msg("Error", url);
                }
                //term select page
                else if (url.contains("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL?Page=SSR_SSENRL_CART&f")) {
                    browser.loadUrl("javascript:window.HTMLOUT.checkTermSelection('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
                //cart page (where classes are)
                else if (url.contains("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL?Page=SSR_SSENRL_CART&Action=A&ACAD_CAREER=UGRD&EMPLID=")) {
                    browser.loadUrl("javascript:window.HTMLOUT.getClasses('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
                //get ID, email, tuition
                else if (url.contains("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL?")) {
                    browser.loadUrl("javascript:window.HTMLOUT.getInfo('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                    dialog.dismiss();
                    dialog = ProgressDialog.show(Login.this, "", "Fetching GPA...", true);
                    browser.loadUrl("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL?&g");
                }
                //gpa page
                else if (url.contains("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSR_SSENRL_GRADE.GBL?&g")) {
                    browser.loadUrl("javascript:window.HTMLOUT.getGPA('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
                //course history page
                else if (url.contains("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL?Page=SSS_MY_CRSEHIST&Action=U")) {
                    dialog.dismiss();
                    browser.loadUrl("javascript:window.HTMLOUT.getCourseHistory('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
                }
                //account locked
                else if (url.contains("Locked")) {
                    dialog.dismiss();
                    Msg("Account Locked", "Your account has been locked for entering too many incorrect passwords. Reset your password by logging into the CUNYFirst website. (Sorry, but I warned you!)");
                }
                //cuny down
                else if (url.contains("http://portaldown.cuny.edu/cunyfirst/")) {
                    dialog.dismiss();
                    Msg("Maintenance", "CUNYFirst is down for maintenance. Please try again later.");
                }
                //page expired
                else if (url.contains("https://home.cunyfirst.cuny.edu/psp/cnyepprd/EMPLOYEE/HRMS/?cmd=expire")) {
                    dialog.dismiss();
                    Msg("Page expired", "You've been logged off.");
                }
                //unknown URL
                else {
                    dialog.dismiss();
                    Msg("Unknown URL encountered", browser.getUrl());
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                dialog.dismiss();
                Msg("Error", description + "\n" + failingUrl);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(15000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (timeout) {
                            dialog.dismiss();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Msg("Timed out", "CUNYFirst took too long to load. It might be down or your connection might be too slow.");
                                }
                            });
                            browser.stopLoading();
                        }
                    }
                }).start();
            }
        });

        BootstrapButton login = (BootstrapButton) findViewById(R.id.button);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ((username.getText().toString().trim().length() == 0) || (password.getText().toString().trim().length() == 0)) {
                    Msg("Error", "Empty username/password field.");
                } else {
                    dialog = ProgressDialog.show(Login.this, "", "Logging in...", true);
                    browser.loadUrl("https://home.cunyfirst.cuny.edu/oam/Portal_Login1.html");
                    //browser.loadUrl("https://www.google.com/");
                }
            }
        });

        final Button showWeb = (Button) findViewById(R.id.button4);
        showWeb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (showWeb.getText().equals("Show")) {
                    showWeb.setText("Hide");
                    browser.setVisibility(View.VISIBLE);
                }
                else {
                    showWeb.setText("Show");
                    browser.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    class MyJavaScriptInterface {

        @JavascriptInterface
        public void checkTermSelection(String html) {
            ArrayList<String> termNames = new ArrayList<>();
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Login.this);;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();

            Document doc = Jsoup.parse(html);
            Elements termName = doc.select("[id^=DERIVED_REGFRM1_SSR_STDNTKEY_DESCR$]");
            //check if single term is found
            if (termName.isEmpty()) {
                Log.e(".", "there is term selection");
                termSelection = true;
                //get term selection names and put them in an ArrayList
                Elements terms = doc.select("[id^=Term_Car$]");
                termNames.add(terms.get(0).text());
                termNames.add(terms.get(1).text());
                //save the term names
                String json = gson.toJson(termNames);
                editor.putString("termNames", json);
                editor.commit();

                if (term1Done == false) {
                    Log.e(".", "clicking term 1");
                    dialog.dismiss();
                    dialog = ProgressDialog.show(Login.this, "", "Fetching first term's classes...", true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //select first term and click submit
                            browser.loadUrl("javascript: var buttons = document.getElementsByName(\"SSR_DUMMY_RECV1$sels$0\"); var x = buttons[0].checked = true;");
                            browser.loadUrl("javascript: submitAction_win0(document.win0,'DERIVED_SSS_SCT_SSR_PB_GO');");
                        }
                    });
                }
                else if (term1Done == true) {
                    Log.e(".", "clicking term 2");
                    dialog.dismiss();
                    dialog = ProgressDialog.show(Login.this, "", "Fetching second term's classes...", true);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //select second term and click submit
                            browser.loadUrl("javascript: var buttons = document.getElementsByName(\"SSR_DUMMY_RECV1$sels$0\"); var x = buttons[1].checked = true;");
                            browser.loadUrl("javascript: submitAction_win0(document.win0,'DERIVED_SSS_SCT_SSR_PB_GO');");
                        }
                    });
                }
            }
            else { //if there is no term selection
                Log.e(".", "no term selection");
                termSelection = false;
                String termString = termName.text();
                if (termString.contains(" |")) {
                    String[] parts = termString.split(Pattern.quote(" |"));
                    termNames.add(parts[0]);
                    String json = gson.toJson(termNames);
                    editor.putString("termNames", json);
                    editor.commit();
                }
                else {
                    termNames.add(termString);
                    String json = gson.toJson(termNames);
                    editor.putString("termNames", json);
                    editor.commit();
                }
            }
        }
        @JavascriptInterface
        public void getClasses(String html) {
            if (term1Done == false) {
                Log.e(".", "term 1 classes page");
                parseClasses(html, "term1Classes");
                //otherwise go back to term select and choose second term
                //if no term selection, start next activity
                if (termSelection == true) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            browser.loadUrl("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSR_SSENRL_CART.GBL?Page=SSR_SSENRL_CART&f");
                        }
                    });
                }
                term1Done = true;
            }
            else {
                Log.e(".", "term 2 classes page");
                parseClasses(html, "term2Classes");
                //cut the rest out
                dialog.dismiss();
                dialog = ProgressDialog.show(Login.this, "", "Fetching ID, email and tuition...", true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_STUDENT_CENTER.GBL?");
                    }
                });
            }
        }
        @JavascriptInterface
        public void getInfo(String html) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Login.this);
            SharedPreferences.Editor editor = sharedPrefs.edit();

            Document doc = Jsoup.parse(html);
            editor.putString("cunyID", doc.getElementById("SCC_PERS_SA_VW_EMPLID").text());
            Log.e(".", doc.getElementById("SCC_PERS_SA_VW_EMPLID").text());
            editor.putString("cunyEmail", doc.getElementById("DERIVED_SSS_SCL_EMAIL_ADDR").text());
            Log.e(".", doc.getElementById("DERIVED_SSS_SCL_EMAIL_ADDR").text());
            if (doc.getElementById("SF_PAYMENT_WRK_DESCRLONG") != null) {
                editor.putString("youOwe", doc.getElementById("SF_PAYMENT_WRK_DESCRLONG").text());
                Log.e(".", doc.getElementById("SF_PAYMENT_WRK_DESCRLONG").text());
            }
            else {
                editor.putString("youOwe", doc.getElementById("SSF_SS_DERIVED_SSF_MESSAGE_TEXT").text());
                Log.e(".", doc.getElementById("SSF_SS_DERIVED_SSF_MESSAGE_TEXT").text());
            }
            editor.commit();
        }
        @JavascriptInterface
        public void getGPA(String html){
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            if (html.contains("Select a term then select Continue."))
            {
                Log.e(".", "gpa term selection page");
                //select the first term listed
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("javascript: var buttons = document.getElementsByName(\"SSR_DUMMY_RECV1$sels$0\"); var x = buttons[0].checked = true;");
                        browser.loadUrl("javascript: submitAction_win0(document.win0,'DERIVED_SSS_SCT_SSR_PB_GO');");
                    }
                });
                editor.putString("GPA", "null");
                editor.putString("credits", "null");
                /*while (true) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Document doc = Jsoup.parse(html);
                            Login.Storage.credits = doc.getElementById("STATS_CUMS$12").text();
                            Log.e(".", Login.Storage.credits);
                            Login.Storage.GPA = doc.getElementById("STATS_CUMS$13").text();
                            Log.e(".", Login.Storage.GPA);
                        }
                    }, 4000);
                }*/
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL?Page=SSS_MY_CRSEHIST&Action=U");
                    }
                });
            }
            else {
                Log.e(".", "no gpa term selection");
                Document doc = Jsoup.parse(html);
                editor.putString("credits", doc.getElementById("STATS_CUMS$12").text());
                Log.e(".", doc.getElementById("STATS_CUMS$12").text());
                editor.putString("GPA", doc.getElementById("STATS_CUMS$13").text());
                Log.e(".", doc.getElementById("STATS_CUMS$13").text());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        browser.loadUrl("https://hrsa.cunyfirst.cuny.edu/psc/cnyhcprd/EMPLOYEE/HRMS/c/SA_LEARNER_SERVICES.SSS_MY_CRSEHIST.GBL?Page=SSS_MY_CRSEHIST&Action=U");
                    }
                });
            }
            editor.commit();
        }
        @JavascriptInterface
        public void getCourseHistory(String html){
            ArrayList<CourseH> courseHistory = new ArrayList<>();

            Document doc = Jsoup.parse(html);
            Elements courseNames = doc.select("[id^=win0divCRSE_LINK$]"); //combined
            Elements courseSections = doc.select("[id^=CRSE_NAME$]"); //combined
            Elements courseTerms = doc.select("[id^=CRSE_TERM$]");
            Elements courseCredits = doc.select("[id^=CRSE_UNITS$]");
            Elements courseGrades = doc.select("[id^=CRSE_GRADE$]");
            Elements courseProgress = doc.select("img[src].SSSIMAGECENTER");
            Elements courseInstitution = doc.select("[id^=INSTITUTION_TBL_DESCR$]");
            for (int i = 0; i < courseNames.size(); i++) {
                String progress = courseProgress.get(i).attr("alt");
                courseHistory.add(new CourseH(courseNames.get(i).text() + " (" + courseSections.get(i).text() + ")", courseTerms.get(i).text(), courseCredits.get(i).text(), courseGrades.get(i).text(), progress, courseInstitution.get(i).text()));
            }
            if (courseNames.isEmpty())
                courseHistory.add(new CourseH("No course history found.", "", "", "", "", ""));

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(Login.this);;
            SharedPreferences.Editor editor = sharedPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(courseHistory);
            editor.putString("courseHistory", json);
            editor.putBoolean("loggedIn", true);
            editor.commit();

            dialog.dismiss();
            startActivity(intent);
            finish();
        }
    }

    public void parseClasses(String html, String term) {
        ArrayList<Course> courses = new ArrayList<>();

        Document doc = Jsoup.parse(html);
        Elements courseNames = doc.select("[id^=E_CLASS_DESCR$]");
        Elements courseSections = doc.select("[id^=win0divE_CLASS_NAME$]");
        Elements courseTimes = doc.select("[id^=DERIVED_REGFRM1_SSR_MTG_SCHED_LONG$160$$]");
        Elements courseRooms = doc.select("[id^=DERIVED_REGFRM1_SSR_MTG_LOC_LONG$161$$]");
        Elements courseProfs = doc.select("[id^=DERIVED_REGFRM1_SSR_INSTR_LONG$162$$]");
        Elements courseCredits = doc.select("[id^=STDNT_ENRL_SSVW_UNT_TAKEN$]");
        int[] rainbow = getApplicationContext().getResources().getIntArray(R.array.rainbow);
        for (int i = 0; i < courseNames.size(); i++)
            courses.add(new Course(courseNames.get(i).text(), courseSections.get(i).text(), courseTimes.get(i).text(), courseRooms.get(i).text(), courseProfs.get(i).text(), courseCredits.get(i).text(), rainbow[i]));
        if (courseNames.isEmpty())
            courses.add(new Course("No classes for this semester found.", "", "", "", "", "", rainbow[0]));

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);;
        SharedPreferences.Editor editor = sharedPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(courses);
        editor.putString(term, json);
        editor.commit();
    }

    public void Msg(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(Login.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", null);
        alert.show();
    }

    /*public static class Receiver extends ParsePushBroadcastReceiver {
        @Override
        public void onPushOpen(Context context, Intent intent) {
            Intent i = new Intent(context, Login.class);
            i.putExtras(intent.getExtras());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }
    }*/
}