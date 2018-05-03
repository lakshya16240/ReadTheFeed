package com.example.lakshya.readthefeed.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lakshya.readthefeed.POJO.Feed;
import com.example.lakshya.readthefeed.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.FeedViewHolder> {

    private Context context;
    private List<Feed> feedList;
    private String title;
    private String description;
    private String link;
    private String imageUrl;


    public FeedsAdapter(Context context, List<Feed> feedList) {
        this.context = context;
        this.feedList = feedList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = li.inflate(R.layout.list_item_feeds,parent,false);

        return new FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, final int position) {
        Feed rssFeed = feedList.get(position);
        String finalNews;
        title = rssFeed.title;
        description = "\n" + rssFeed.description;
        link = rssFeed.link;
        imageUrl = rssFeed.imageUrl;

        Log.d("NEWSIMAGE imageURL  ", "doInBackground: " + imageUrl);

        SpannableString ss1 = new SpannableString(title);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        ss1.setSpan(boldSpan, 0, 8, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        finalNews = ss1 + description;
        holder.tv_newsText.setText(finalNews);
        holder.cv_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(feedList.get(position).link));
                context.startActivity(browserIntent);
            }
        });

        new ImageLoadTask(imageUrl,holder.iv_newsImage).execute();

    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }



    class FeedViewHolder extends RecyclerView.ViewHolder {
        TextView tv_newsText;
        ImageView iv_newsImage;
        CardView cv_news;
        public FeedViewHolder(View itemView) {
            super(itemView);
            tv_newsText = itemView.findViewById(R.id.tv_newsText);
            iv_newsImage = itemView.findViewById(R.id.iv_newsImage);
            cv_news = itemView.findViewById(R.id.cv_news);
        }


    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                Log.d("NEWSIMAGE", "doInBackground: " + url);
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
