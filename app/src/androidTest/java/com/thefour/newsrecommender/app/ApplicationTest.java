package com.thefour.newsrecommender.app;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void updateListNewsTaskTest(){
        String url = "http://localhost:8084/RankedListNews/toprankedlistnews?offset=10";
        UpdateListNewsTask updateListNewsTask =new UpdateListNewsTask(getContext());
        updateListNewsTask.doInBackground(url);

    }
}