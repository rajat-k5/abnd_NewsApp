package com.example.android.newsapp.feature;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context  of the app
     * @param NewsList is the list of Newss, which is the data source of the adapter
     */
    NewsAdapter(Context context, List<News> NewsList) {
        super(context, 0, NewsList);
    }


    /**
     * Returns a list item view that displays information about the News at the given position
     * in the list of NewsList.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView
                    = LayoutInflater.from(getContext()).inflate(R.layout.news_item, parent, false);
        }
        TextView title = convertView.findViewById(R.id.title);
        TextView author = convertView.findViewById(R.id.author);
        TextView date = convertView.findViewById(R.id.date);
        TextView section = convertView.findViewById(R.id.section);

        News currentNews = getItem(position);
        if (currentNews != null) {
            title.setText(currentNews.getTitle());
            author.setText(currentNews.getAuthor());
            date.setText(currentNews.getDate());
            section.setText(currentNews.getSection());
        }
        return convertView;
    }
}