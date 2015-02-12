package com.guil.moura.simplecontentlist.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.guil.moura.simplecontentlist.R;
import com.guil.moura.simplecontentlist.model.Facts;
import com.guil.moura.simplecontentlist.model.Row;
import com.guil.moura.simplecontentlist.network.GsonContentRequest;
import com.guil.moura.simplecontentlist.network.VolleyHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends ActionBarActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "MainActivity";

    private static final String CONTENT_URL = "https://dl.dropboxusercontent.com/u/746330/facts.json";

    @InjectView(R.id.swipe_to_refresh_wrapper)
    SwipeRefreshLayout swipeToRefreshWrapper;

    @InjectView(R.id.facts_list)
    ListView factsList;

    FactsAdapter adapter;

    ArrayList<Row> rowsList = new ArrayList<Row>();

    ActionBar mActionbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        swipeToRefreshWrapper.setOnRefreshListener(this);
        swipeToRefreshWrapper.setRefreshing(true);

        mActionbar = getSupportActionBar();

        adapter = new FactsAdapter(getApplicationContext(), rowsList);

        factsList.setAdapter(adapter);

        loadContent();
    }

    @Override
    public void onRefresh() {
        loadContent();
    }

    private void loadContent() {

        swipeToRefreshWrapper.setRefreshing(true);

        GsonContentRequest<Facts> contentRequest
                = new GsonContentRequest<>(CONTENT_URL, Facts.class, null, new Response.Listener<Facts>() {
            @Override
            public void onResponse(Facts fact) {
                swipeToRefreshWrapper.setRefreshing(false);
                populateFacts(fact);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v(TAG, "Failed to retrieve Facts from network, loading from local raw resource");
                loadContentLocally();
            }
        });

        VolleyHelper.getInstance(getApplicationContext()).getRequestQueue().start();
        VolleyHelper.getInstance(getApplicationContext()).addToRequestQueue(contentRequest);
    }

    private void populateFacts(Facts fact) {
        swipeToRefreshWrapper.setRefreshing(false);
        mActionbar.setTitle(fact.getTitle());
        adapter.addAll(fact.getRows());
        adapter.notifyDataSetChanged();
    }

    private void loadContentLocally() {
        try {
            populateFacts(readRawContentFile());
        } catch (IOException e) {
            swipeToRefreshWrapper.setRefreshing(false);
            Log.v(TAG, "Failed to retrieve Facts!");
        }
    }

    private Facts readRawContentFile() throws IOException {
        InputStream is = getResources().openRawResource(R.raw.facts);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        return new Gson().fromJson(writer.toString(), Facts.class);
    }

}
