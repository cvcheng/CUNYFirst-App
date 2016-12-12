package com.cunyfirst;

import android.os.Build;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /*
        Parse.initialize(this, "vWc5jFvIoICDL2hzlOdqTVsdcglHQoeUJZ8cymRS", "w3YSw2wI4szk0cqjJHa8EahBr9tMwh0UCiMbPviW");
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("deviceName", getDeviceName());
        installation.saveInBackground();
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + "-" + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }*/
    }
}