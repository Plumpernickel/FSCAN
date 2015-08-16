package com.example.yoav.fscan;

/**
 * Created by Yoav on 8/14/2015.
 */
public class CustomAdapterList {
    public MainActivity.GridAdapter gridAdapter;
    public MyExpandableListAdapter myExpandableListAdapter;

    public CustomAdapterList(MainActivity.GridAdapter gridAdapter, MyExpandableListAdapter myExpandableListAdapter) {
        this.gridAdapter = gridAdapter;
        this.myExpandableListAdapter = myExpandableListAdapter;
    }
}
