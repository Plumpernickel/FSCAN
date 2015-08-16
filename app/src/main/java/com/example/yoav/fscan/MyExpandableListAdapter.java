package com.example.yoav.fscan;

import android.app.Activity;
import android.app.ProgressDialog;
import android.database.sqlite.SQLiteBindOrColumnIndexOutOfRangeException;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Yoav on 8/7/2015.
 * Much of this code, its necessary layout resources, and the code for the Group class is copied from
 * Vogella's tutorial on ListViews: http://www.vogella.com/tutorials/AndroidListView/article.html
 *
 * Filter code attribution is the same as mentioned in the GridAdapter class Filter in the MainActivity
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter implements Filterable {
    Filter myFilter = new Filter() {
        @Override
        synchronized protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            SparseArray<Group> fGroups = new SparseArray<>();
            filterResults.values = fGroups;
            filterResults.count = 0;

            if(constraint != null && !constraint.equals("") && groups != null) {
                int length = groups.size();
                int i = 0;

                while(i < length) {
                    String parent = groups.get(i).string;
                    Group filteredGroup = new Group(parent);

                    String child1 = groups.get(i).children.get(0);
                    String child2 = groups.get(i).children.get(1);
                    String child3 = groups.get(i).children.get(2);

                    if(parent.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredGroup.children.add(child1);
                        filteredGroup.children.add(child2);
                        filteredGroup.children.add(child3);
                    }

                    if(filteredGroup.children.size() > 1) {
                        fGroups.append(i, filteredGroup);
                    }

                    i++;
                }

                filterResults.values = fGroups;
                filterResults.count = fGroups.size();
            }

            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (constraint == null || constraint.length() == 0) {
                notifyDataSetInvalidated();
            }
            else {
                groups.clear();
                groups = (SparseArray<Group>)results.values;
                notifyDataSetChanged();
            }
        }
    };

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private SparseArray<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public MyExpandableListAdapter(Activity act, SparseArray<Group> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    public void sortByOrg(boolean ascending) {
        ArrayList<String> sortedOrgs = new ArrayList<>();
        SparseArray<Group> orderedGroups = new SparseArray<>();

        for(int i=0; i<groups.size(); i++) {
            sortedOrgs.add(groups.get(i).string);
        }

        if(!sortedOrgs.isEmpty()) {
            if(ascending) {
                Collections.sort(sortedOrgs, String.CASE_INSENSITIVE_ORDER);
            }
                  // Reverse sort does not take case insensitivity as an argument,
                  // and therefore outputs odd data.
//                else {
//                    Collections.sort(sortedOrgs, String.CASE_INSENSITIVE_ORDER);
//                    Collections.reverse(sortedOrgs);
//                }

            for(int j=0; j<sortedOrgs.size(); j++) {
                for(int k=0; k<groups.size(); k++) {
                    if(groups.get(k).string.toLowerCase().equals(sortedOrgs.get(j).toLowerCase())
                            && orderedGroups.indexOfValue(groups.get(k)) == -1) {
                        orderedGroups.append(j, groups.get(k));
                    }
                }
            }

            groups.clear();
            groups = orderedGroups;
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText(children);
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, children,
                        Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int finalSize = 0;

        if(groups != null && groups.get(groupPosition).children != null) {
            finalSize = groups.get(groupPosition).children.size();
        }

        return finalSize;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        Group group = (Group) getGroup(groupPosition);

        if (group != null && !group.string.isEmpty()) {
            ((CheckedTextView) convertView).setText(group.string);
            ((CheckedTextView) convertView).setChecked(isExpanded);
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
