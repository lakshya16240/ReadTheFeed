package com.example.lakshya.readthefeed;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.lakshya.readthefeed.Adapter.FeedsAdapter;
import com.example.lakshya.readthefeed.POJO.Feed;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rv_feeds;
    private List<Feed> feedList;
    public static final String urlLink = "http://feeds.bbci.co.uk/news/technology/rss.xml";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        rv_feeds = findViewById(R.id.rv_feeds);

        rv_feeds.setLayoutManager(new LinearLayoutManager(this));

        new FetchFeedTask().execute((Void) null);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new FetchFeedTask().execute((Void) null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.action_logout:
                Intent intent = new Intent(FeedActivity.this, LoginActivity.class );
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }


    private class FetchFeedTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {


                URL url = new URL(urlLink);
                InputStream inputStream = url.openConnection().getInputStream();
                feedList = feedParsing(inputStream);
                return true;
            } catch (IOException e) {
                Log.e("IOException ", "Error", e);
            } catch (XmlPullParserException e) {
                Log.e("XmlPullParserException ", "Error", e);
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            swipeRefreshLayout.setRefreshing(false);

            if (success) {
                rv_feeds.setAdapter(new FeedsAdapter(FeedActivity.this,feedList));
            } else {
                Toast.makeText(FeedActivity.this,
                        "Error has occured",
                        Toast.LENGTH_LONG).show();
            }
        }
    }


    public List<Feed> feedParsing(InputStream inputStream) throws XmlPullParserException, IOException {
        String title = null;
        String link = null;
        String description = null;
        String imageUrl = null;
        boolean isItem = false;
        List<Feed> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if(isItem) {
                    if (name.equalsIgnoreCase("title")) {
                        title = result;
                    } else if (name.equalsIgnoreCase("link")) {
                        link = result;
                    } else if (name.equalsIgnoreCase("description")) {
                        description = result;
                    } else if (name.equalsIgnoreCase("media:thumbnail")) {
                        imageUrl = xmlPullParser.getAttributeValue(null, "url");
                    }
                }

                if (title != null && link != null && description != null && imageUrl!=null) {
                    if(isItem) {
                        Feed item = new Feed(title, link, description,imageUrl);
                        items.add(item);
                    }

                    title = null;
                    link = null;
                    description = null;
                    imageUrl = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }
    }
}
