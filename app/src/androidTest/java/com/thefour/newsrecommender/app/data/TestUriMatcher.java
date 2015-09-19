package com.thefour.newsrecommender.app.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by Quang Quang on 8/14/2015.
 */
public class TestUriMatcher extends AndroidTestCase{
    private static final Uri TEST_NEWS_DIR = NewsContract.NewsEntry.CONTENT_URI;
    private static final Uri TEST_NEWS_WITH_CATEGORY_ID = NewsContract.NewsEntry.buildNewsCategoryId(2);
    private static final Uri TEST_NEWS_WITH_CATEGORY_NAME = NewsContract.NewsEntry.buildNewsCategoryName("Kinh Doanh");
    private static final Uri TEST_CATEGORY_DIR = NewsContract.CategoryEntry.CONTENT_URI;
    private static final Uri TEST_SOURCE_DIR = NewsContract.NewsSourceEntry.CONTENT_URI;

    public void testUriMatcher(){
        UriMatcher matcher = NewsProvider.buildUriMatcher();
        assertEquals("The NEWS URI was matched incorrectly",NewsProvider.NEWS,matcher.match(TEST_NEWS_DIR));
        assertEquals("The NEWS WITH CATEGORY ID was matched incorrectly",NewsProvider.NEWS_WITH_CATEGORY,matcher.match(TEST_NEWS_WITH_CATEGORY_ID));
        assertEquals("The NEWS WITH CATEGORY NAME was matched incorrectly",NewsProvider.NEWS_WITH_CATEGORY,matcher.match(TEST_NEWS_WITH_CATEGORY_NAME));
        assertEquals("The CATEGORY URI was matched incorrectly", NewsProvider.CATEGORY,matcher.match(TEST_CATEGORY_DIR));
        assertEquals("The SOURCE URI was matched incorrectly", NewsProvider.SOURCE,matcher.match(TEST_SOURCE_DIR));
    }
}
