package com.example.yoav.fscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class MainActivity extends Activity {
    String aboutText, helpText, portHint;
    boolean inFrench = false;
    int aboutTitle = R.string.action_bar_about_en;
    int helpTitle = R.string.action_bar_help_en;
    String currentFile = "ogsp2011.json";
    JSONObject currentData;
    JSONArray org, stratOut, name, spending;
    SparseArray<Group> groups = new SparseArray<>();
    protected ArrayList<String> columnVals;
    ExpandableListView portList;
    GridView landList;
    GridAdapter gridAdapter;
    MyExpandableListAdapter elvAdapter;
    CustomAdapterList myCurrentAdapter;
    AutoCompleteTextView acv = null;
    //EditText et1;
    Button b1;
    ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        aboutText = this.getString(R.string.about_text);
        helpText = this.getString(R.string.instructions_en);
        portHint = this.getString(R.string.hint_en);

        final Spinner dropdown = (Spinner)findViewById(R.id.myspinner);
        String[] items = new String[]{"2011-2012", "2012-2013", "2013-2014"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
        acv = (AutoCompleteTextView)findViewById(R.id.autoComplete1);
        //et1 = (EditText)findViewById(R.id.edit1);
        b1 = (Button)findViewById(R.id.button);
        loader = (ProgressBar)findViewById(R.id.progressBar);
        if(loader != null) {
            loader.setVisibility(View.GONE);
        }

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();

                switch (index) {
                    case 1:
                        currentFile = "ogsp2012.json";
                        myCurrentAdapter = populateData(currentFile, inFrench);
                        if(myCurrentAdapter.gridAdapter != null) {
                            gridAdapter = myCurrentAdapter.gridAdapter;
                            acv.setAdapter(gridAdapter);
                            acv.setText("");
                        }
                        break;
                    case 2:
                        currentFile = "ogsp2013.json";
                        myCurrentAdapter = populateData(currentFile, inFrench);
                        if(myCurrentAdapter.gridAdapter != null) {
                            gridAdapter = myCurrentAdapter.gridAdapter;
                            acv.setAdapter(gridAdapter);
                            acv.setText("");
                        }
                        break;
                    default:
                        currentFile = "ogsp2011.json";
                        myCurrentAdapter = populateData(currentFile, inFrench);
                        if(myCurrentAdapter.gridAdapter != null) {
                            gridAdapter = myCurrentAdapter.gridAdapter;
                            acv.setAdapter(gridAdapter);
                            acv.setText("");
                        }
                        else {
                            elvAdapter = myCurrentAdapter.myExpandableListAdapter;
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if(acv != null) {
            acv.setThreshold(1);
            acv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if(findViewById(R.id.gridView) != null) {
                        myCurrentAdapter = populateData(currentFile, inFrench);
                        gridAdapter = myCurrentAdapter.gridAdapter;
                        gridAdapter.getFilter().filter(s.toString().toLowerCase());
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        if(b1 != null) {
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sortTask task = new sortTask();
                    task.execute();
                }
            });
        }

        // Currently does nothing, and is here for viewing purposes only.
        // Refer to the implementation report for more information.
//        if(et1 != null) {
//            et1.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                    if(findViewById(R.id.expandableListView) != null) {
//                        myCurrentAdapter = populateData(currentFile, inFrench);
//                        elvAdapter = myCurrentAdapter.myExpandableListAdapter;
//                        elvAdapter.getFilter().filter(s.toString().toLowerCase());
//                    }
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//
//                }
//            });
//        }

        final TextView orgLabel = (TextView)findViewById(R.id.orgLabel);
        final TextView stratLabel = (TextView)findViewById(R.id.stratOutLabel);
        final TextView nameLabel = (TextView)findViewById(R.id.nameLabel);
        final TextView spendLabel = (TextView)findViewById(R.id.spendingLabel);

        Switch lang = (Switch)findViewById(R.id.switch2);

        lang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inFrench = true;
                    myCurrentAdapter = populateData(currentFile, inFrench);
                    aboutTitle = R.string.action_bar_about_fr;
                    helpTitle = R.string.action_bar_help_fr;
                    aboutText = getApplicationContext().getString(R.string.about_text_fr);
                    helpText = getApplicationContext().getString(R.string.instructions_fr);
                    portHint = getApplicationContext().getString(R.string.hint_fr);
                    if(b1 != null) {
                        b1.setText(getApplicationContext().getString(R.string.sort_asc_fr));
                    }
                    if(acv != null) {
                        acv.setText("");
                        acv.setHint(portHint);
                    }
//                    else {
//                        et1.setText("");
//                        et1.setHint("Recherche désactivée");
//                    }
                    if(findViewById(R.id.orgLabel) != null) {
                        orgLabel.setText(getApplicationContext().getString(R.string.col_org_fr));
                        stratLabel.setText(getApplicationContext().getString(R.string.col_name_fr));
                        nameLabel.setText(getApplicationContext().getString(R.string.col_strat_fr));
                        spendLabel.setText(getApplicationContext().getString(R.string.col_spend_fr));
                    }
                    invalidateOptionsMenu();
                } else {
                    inFrench = false;
                    myCurrentAdapter = populateData(currentFile, inFrench);
                    aboutTitle = R.string.action_bar_about_en;
                    helpTitle = R.string.action_bar_help_en;
                    aboutText = getApplicationContext().getString(R.string.about_text);
                    helpText = getApplicationContext().getString(R.string.instructions_en);
                    portHint = getApplicationContext().getString(R.string.hint_en);
                    if(b1 != null) {
                        b1.setText(getApplicationContext().getString(R.string.sort_asc_en));
                    }
                    if(acv != null) {
                        acv.setText("");
                        acv.setHint(portHint);
                    }
//                    else {
//                        et1.setText("");
//                        et1.setHint("Search disabled");
//                    }
                    if(findViewById(R.id.orgLabel) != null) {
                        orgLabel.setText(getApplicationContext().getString(R.string.col_org_en));
                        stratLabel.setText(getApplicationContext().getString(R.string.col_name_en));
                        nameLabel.setText(getApplicationContext().getString(R.string.col_strat_en));
                        spendLabel.setText(getApplicationContext().getString(R.string.col_spend_en));
                    }
                    invalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem helpItem = menu.findItem(R.id.help);
        MenuItem aboutItem = menu.findItem(R.id.about);

        helpItem.setTitle(helpTitle);
        aboutItem.setTitle(aboutTitle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Code inspired by: http://stackoverflow.com/a/4850534/1438611
        if (id == R.id.about) {

            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(aboutTitle);
            alertDialog.setMessage(aboutText);

            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.hide();
                }
            });

            alertDialog.show();
            return true;
        }
        if (id == R.id.help) {
            final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(helpTitle);
            alertDialog.setMessage(helpText);

            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    alertDialog.hide();
                }
            });

            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putStringArrayList("savedGrid", columnVals);

        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(columnVals == null && savedInstanceState.getStringArrayList("savedGrid") != null) {
            columnVals = savedInstanceState.getStringArrayList("savedGrid");
        }
    }

    // Code for descending order cannot work with current datatypes.
    // It is currently commented out and kept for viewing purposes only
    public class sortTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            if(inFrench) {
                if(b1.getText() == getString(R.string.sort_asc_fr)) {
                    elvAdapter.sortByOrg(true);
                    // b1.setText(getString(R.string.sort_des_en));
                }
//            else {
//                elvAdapter.sortByOrg(false);
//                b1.setText(getString(R.string.sort_asc_fr));
//            }
            }
            else {
                if(b1.getText() == getString(R.string.sort_asc_en)) {
                    elvAdapter.sortByOrg(true);
                    //b1.setText(getString(R.string.sort_des_en));
                }
//            else {
//                elvAdapter.sortByOrg(false);
//                b1.setText(getString(R.string.sort_asc_en));
//            }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            myCurrentAdapter.myExpandableListAdapter.notifyDataSetChanged();
            loader.setVisibility(View.GONE);
        }
    }

    // Code copied from: http://stackoverflow.com/a/13814551/1438611
    public String loadJSONFromAssets(String filename) {
        String json;
        try {
            InputStream is = getAssets().open(filename);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);

            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return json;
    }

    public CustomAdapterList populateData(String filename, boolean versionFr) {
        try {
            currentData = new JSONObject(loadJSONFromAssets(filename));
            if(versionFr) {
                org = currentData.getJSONArray("OrganizationFr");
                name = currentData.getJSONArray("NameFr");
                stratOut = currentData.getJSONArray("StrategicOutcomeFr");
            }
            else {
                org = currentData.getJSONArray("OrganizationEn");
                name = currentData.getJSONArray("NameEn");
                stratOut = currentData.getJSONArray("StrategicOutcomeEn");
            }
            spending = currentData.getJSONArray("ActualSpending");

            if(groups.size() > 0)  {
                groups.clear();
            }

            if(findViewById(R.id.gridView) != null) {
                landList = (GridView)findViewById(R.id.gridView);
            }

            columnVals = new ArrayList<>();

            for (int i = 0; i < org.length(); i++) {
                Group group = new Group(org.getString(i));
                columnVals.add(org.getString(i) + "=" + name.getString(i) + "=" +
                        stratOut.getString(i) + "=" + "\t\t\t\t$" + spending.getString(i));

                for (int j = 0; j < 3; j++) {
                    if(j == 0) {
                        if(versionFr){
                            group.children.add(getString(R.string.col_name_fr) + ": " + name.getString(i));
                        }
                        else {
                            group.children.add("Name: " + name.getString(i));
                        }
                    }
                    else if(j == 1) {
                        if(versionFr) {
                            group.children.add(getString(R.string.col_strat_fr) + ": " + stratOut.getString(i));
                        }
                        else {
                            group.children.add("Strategic Outcome: " + stratOut.getString(i));
                        }
                    }
                    else {
                        if(versionFr) {
                            group.children.add(getString(R.string.col_spend_fr) + ": $" + spending.getString(i));
                        }
                        else {
                            group.children.add("Actual Spending: $" + spending.getString(i));
                        }
                    }
                }
                groups.append(i, group);
            }

            if(landList != null) {
                gridAdapter = new GridAdapter(columnVals);
                landList.setAdapter(gridAdapter);
            }

            if(findViewById(R.id.expandableListView) != null){
                portList = (ExpandableListView)findViewById(R.id.expandableListView);

                elvAdapter = new MyExpandableListAdapter(MainActivity.this, groups);
                portList.setAdapter(elvAdapter);
            }

            if(landList != null) {
                acv.setEnabled(true);
            }

//            if(portList != null) {
//                et1.setEnabled(false);
//            }

        } catch (JSONException je) {
            je.printStackTrace();
        }

        CustomAdapterList customAdapterList = new CustomAdapterList(gridAdapter, elvAdapter);

        return customAdapterList;
    }

    // Code copied and modified from: http://stackoverflow.com/a/17417615/1438611
    public final class GridAdapter extends BaseAdapter implements Filterable {
        // Code copied and modified from: http://stackoverflow.com/a/19301216/1438611
        Filter myFilter = new Filter() {
            @Override
            synchronized protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                ArrayList<String> tempList = new ArrayList<>();
                filterResults.values = tempList;
                filterResults.count = 0;
                //constraint is the result from text you want to filter against.
                //objects is your data set you will filter from
                if(constraint != null && !constraint.equals("") && mItems != null) {
                    int length = mItems.size();
                    int i = 0;

                    while(i < length) {
                        // The entire row of data (4 column values)
                        String item = mItems.get(i);
                        String item2 = mItems.get(i + 1);
                        String item3 = mItems.get(i + 2);
                        String item4 = mItems.get(i + 3);

                        //do whatever you wanna do here
                        //adding result set output array
                        if(item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            tempList.add(item);
                            tempList.add(item2);
                            tempList.add(item3);
                            tempList.add(item4);
                        }

                        i += 4;
                    }
                    //following two lines is very important
                    //as publish result can only take FilterResults objects
                    filterResults.values = tempList;
                    filterResults.count = tempList.size();
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
                    mItems = (ArrayList<String>)results.values;
                    notifyDataSetChanged();
                }
            }
        };

        ArrayList<String> mItems;

        /**
         * Default constructor
         * @param items to fill data to
         */
        private GridAdapter(final ArrayList<String> items) {
            mItems = new ArrayList<>();

            // for small size of items it's ok to do it here, sync way
            for (String item : items) {
                // get separate string parts, divided by =
                final String[] parts = item.split("=");

                for (String part : parts) {
                    mItems.add(part);
                }
            }
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public String getItem(final int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                view.setPadding(5, 5, 5, 5);
            }

            final TextView text = (TextView) view.findViewById(android.R.id.text1);

            if(mItems != null && mItems.size() > 0) {
                if(position >= mItems.size()) {
                    text.setText(mItems.get(mItems.size() - 1));
                }
                else {
                    text.setText(mItems.get(position));
                }
                text.setHeight(300);
                text.setTextSize(9);
            }

            return view;
        }

        @Override
        public Filter getFilter() {
            return myFilter;
        }
    }
}
