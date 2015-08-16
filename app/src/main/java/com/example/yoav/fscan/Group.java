package com.example.yoav.fscan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yoav on 8/7/2015.
 */
public class Group {
    public String string;
    public ArrayList<String> children = new ArrayList<>();

    public Group(String string) {
        this.string = string;
    }
}
