package com.seriousmonkey.uptimerobot;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.seriousmonkey.uptimerobot.JsonClasses.Log;
import com.seriousmonkey.uptimerobot.JsonClasses.Monitor;
import com.seriousmonkey.uptimerobot.JsonClasses.Monitors;
import com.seriousmonkey.uptimerobot.adapters.DetailItemAdapter;
import com.seriousmonkey.uptimerobot.assets.DetailItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    public String timeZone;
    private Locale appLocale;

    private GridView mDetailsGridView;
    private DetailItemAdapter mDetailItemAdapter;

    private List<DetailItem> mDetailItems;

    public MainActivity() {
        mDetailItems = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mDetailsGridView = (GridView) findViewById(R.id.DetailsItemGrid);

        mDetailItemAdapter = new DetailItemAdapter(this.getBaseContext(), mDetailItems);
        mDetailsGridView.setAdapter(mDetailItemAdapter);

        appLocale = new Locale.Builder().setLanguage("en").setRegion("US").build();

        initRefreshButton();
        initActionBar();
        readApiKey();
        readTimeZone();

        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void initActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.menu_icon);
        actionBar.setDisplayOptions(1);
    }

    public void callUptimeRobot()
    {
        URL url;
        HttpURLConnection urlConnection;

        try{
            url = new URL(buildRequestUrl("getMonitors"));

            try {
                urlConnection = (HttpURLConnection) url.openConnection();

                //urlConnection.addRequestProperty("content-type", "application/x-www-form-urlencoded");
                //urlConnection.addRequestProperty("cache-control", "no-cache");

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                wr.writeBytes(getRequestParameters());
                wr.flush();
                wr.close();

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                readStream(in);

                urlConnection.disconnect();

                outputTimerText(String.format("Last updated on %s", Monitor.getFormattedDate(Monitor.getCurrentDate(), timeZone)));
            }
            catch (IOException e) {
                //Error: could not connect
                outputString("Could not connect! " + e.getMessage());
             }
        }
        catch(MalformedURLException e) {
            //Error: url is not in the correct format
            outputString("Url is not in the correct format!");
        }
    }

    public void readStream(InputStream in) {
        ByteArrayOutputStream resultStream = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int length;

        try {
            while ((length = in.read(buffer)) != -1) {
                resultStream.write(buffer, 0, length);
            }

            String resultString = resultStream.toString("UTF-8");

            try{
                JSONObject obj = new JSONObject(resultString);
                System.out.print(obj.getString("monitors"));

                clearOutput();

                Monitors monitors = Monitors.getMonitorsFromJson(resultString); //obj.getString("monitors"));

                outputUpMonitorCount(monitors.getUpMonitorCount());
                outputDownMonitorCount(monitors.getDownMonitorCount());
                outputPausedMonitorCount(monitors.getPausedMonitorCount());

                mDetailItems.clear();

                if (monitors != null && monitors.monitors != null) {
                    //outputString("");
                    for (int i = 0; i < monitors.monitors.length; i++) {
                        Monitor monitor = monitors.monitors[i];

                        //outputString(monitor.friendly_name + ": " + monitor.getStatus() + "\n");

                        String lastDownTime = "";
                        int highestEntry = 0;

                        for (int l = 0; l < monitor.logs.length; l++) {
                            Log log = monitor.logs[l];

                            if(log.type.getValue() == 1 && log.datetime.getValue() > highestEntry) {
                                highestEntry = log.datetime.getValue();

                                lastDownTime = "Last Down for " + log.getFormattedDuration(false) + " on " + log.getFormattedDateTime(false);
                            }
                        }

                        //outputString(lastDownTime);
                        //outputString("\n");

                        double upTimePercentage = 100 - monitor.getDownTimePercentage(Monitor.TimeDuration.DAY);
                        String downtime = String.format(appLocale, "%1$.1f%%", upTimePercentage);

                        mDetailItems.add(new DetailItem(lastDownTime, monitor.friendly_name, downtime, "#ffffffff", monitor.getStatusColor()));
                    }

                    loadDetailListItems();
                }

                //outputString("\n\n\n");
                //outputString(resultString);

            }
            catch(JSONException je) {

            }
        }
        catch(IOException e) {
            //Error: The response could not be read
            outputString("The response could not be read!");
        }
    }

    private void outputString(String output) {
        TextView detailsView = (TextView)findViewById(R.id.detailsView);

        detailsView.setText(detailsView.getText() + output);
        detailsView.setVisibility(View.GONE);
    }

    private void outputTimerText(String output) {
        TextView timerTextView = (TextView)findViewById(R.id.timerTextView);

        timerTextView.setText(output);
    }

    private void outputUpMonitorCount(int output) {
        TextView upMonitorsText = (TextView)findViewById(R.id.upMonitorsText);

        upMonitorsText.setText(Integer.toString(output));
    }

    private void outputDownMonitorCount(int output) {
        TextView downMonitorsText = (TextView)findViewById(R.id.downMonitorsText);

        downMonitorsText.setText(Integer.toString(output));
    }

    private void outputPausedMonitorCount(int output) {
        TextView pausedMonitorsText = (TextView)findViewById(R.id.pausedMonitorsText);

        pausedMonitorsText.setText(Integer.toString(output));
    }

    private void clearOutput() {
        TextView detailsView = (TextView)findViewById(R.id.detailsView);

        detailsView.setText("");
    }

    //API Key: u101288-f19f18a00d3569cc653cab62
    private String apiKey = "u101288-f19f18a00d3569cc653cab62";
    private final String responseFormat = "json";

    private String buildRequestUrl(String method) {
        StringBuilder urlString = new StringBuilder();

        urlString.append("https://api.uptimerobot.com/v2/");
        urlString.append(method);
        urlString.append("?api_key=" + apiKey);
        urlString.append("&format=" + responseFormat);

        return urlString.toString();
    }

    private String getRequestParameters() {
        StringBuilder reqParamStr = new StringBuilder();

        reqParamStr.append("api_key=");
        reqParamStr.append(apiKey);
        reqParamStr.append("&format=");
        reqParamStr.append(responseFormat);
        reqParamStr.append("&logs=1");

        return reqParamStr.toString();
    }

    public void loadDetailListItems() {
        mDetailItemAdapter.notifyDataSetChanged();
    }

    public void initRefreshButton() {
        Button goButton = (Button)findViewById(R.id.goButton);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/FontAwesome.otf");
        goButton.setTypeface(custom_font);
        goButton.setTextSize(28);
        goButton.setText("\uF021");

        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                callUptimeRobot();
            }
        });
    }

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            callUptimeRobot();
            timerHandler.postDelayed(this, 60000);
        }
    };

    private void writeAPiKey(String key) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString(getString(R.string.uptime_apiKey), key);

        editor.apply();
    }

    private void readApiKey() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        apiKey = getResources().getString(R.string.uptime_apiKey);
    }

    private void readTimeZone() {
        timeZone = getResources().getString(R.string.time_zone);
    }

}
