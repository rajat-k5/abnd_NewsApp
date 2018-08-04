package com.example.android.newsapp.feature;


/**
 * An {@link News} object contains information related to a single earthquake.
 */
public class News {

    /**
     * Title of the news item
     */
    private String mTitle;

    /**
     * Section of the news item
     */
    private String mSection;

    /**
     * Author of the news item
     */
    private String mAuthor;

    /**
     * url of the news item
     */
    private String mUrl;

    /**
     * Title of the news item
     */
    private String mDate;


    /**
     * Constructs a new {@link News} object.
     *
     * @param title   is the title of the news article
     * @param section is the section to which the article belongs
     * @param author  is the author of the News item
     * @param url     is the website URL to find more details about the news
     * @param date    is the publishing date of the news
     */
    News(String title, String section, String author, String url, String date) {
        mTitle = title;
        mSection = section;
        mAuthor = author;
        mUrl = url;
        mDate = date;
    }


    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getDate() {
        return mDate;
    }

    public String getSection() {
        return mSection;
    }


    @Override
    public String toString() {
        return "News{" +
                "title='" + mTitle + '\'' +
                ", author='" + mAuthor + '\'' +
                ", url='" + mUrl + '\'' +
                ", date='" + mDate + '\'' +
                ", section='" + mSection + '\'' +
                '}';
    }
}