package com.example.yoav.fscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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
    JSONObject currentData;
    JSONArray org, stratOut, name, spending;
    SparseArray<Group> groups = new SparseArray<>();
    ExpandableListView portList;

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

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int index = arg0.getSelectedItemPosition();

                switch (index) {
                    case 1:
                        try {
                            currentData = new JSONObject(loadJSONFromAssets("ogsp2012.json"));
                            if(inFrench) {
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

                            for (int i = 0; i < org.length(); i++) {
                                Group group = new Group(org.getString(i));
                                for (int j = 0; j < 3; j++) {
                                    if(j == 0) {
                                        if(inFrench){
                                            group.children.add(getString(R.string.col_name_fr) + ": " + name.getString(i));
                                        }
                                        else {
                                            group.children.add("Name: " + name.getString(i));
                                        }
                                    }
                                    else if(j == 1) {
                                        if(inFrench) {
                                            group.children.add(getString(R.string.col_strat_fr) + ": " + stratOut.getString(i));
                                        }
                                        else {
                                            group.children.add("Strategic Outcome: " + stratOut.getString(i));
                                        }
                                    }
                                    else {
                                        if(inFrench) {
                                            group.children.add(getString(R.string.col_spend_fr) + ": $" + spending.getString(i));
                                        }
                                        else {
                                            group.children.add("Actual Spending: $" + spending.getString(i));
                                        }
                                    }
                                }
                                groups.append(i, group);
                            }

                            if(findViewById(R.id.expandableListView) != null){
                                portList = (ExpandableListView)findViewById(R.id.expandableListView);

                                MyExpandableListAdapter elvAdapter = new MyExpandableListAdapter(MainActivity.this, groups);
                                portList.setAdapter(elvAdapter);
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        break;
                    case 2:
                        try {
                            currentData = new JSONObject(loadJSONFromAssets("ogsp2013.json"));
                            if(inFrench) {
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

                            for (int i = 0; i < org.length(); i++) {
                                Group group = new Group(org.getString(i));
                                for (int j = 0; j < 3; j++) {
                                    if(j == 0) {
                                        if(inFrench){
                                            group.children.add(getString(R.string.col_name_fr) + ": " + name.getString(i));
                                        }
                                        else {
                                            group.children.add("Name: " + name.getString(i));
                                        }
                                    }
                                    else if(j == 1) {
                                        if(inFrench) {
                                            group.children.add(getString(R.string.col_strat_fr) + ": " + stratOut.getString(i));
                                        }
                                        else {
                                            group.children.add("Strategic Outcome: " + stratOut.getString(i));
                                        }
                                    }
                                    else {
                                        if(inFrench) {
                                            group.children.add(getString(R.string.col_spend_fr) + ": $" + spending.getString(i));
                                        }
                                        else {
                                            group.children.add("Actual Spending: $" + spending.getString(i));
                                        }
                                    }
                                }
                                groups.append(i, group);
                            }

                            if(findViewById(R.id.expandableListView) != null){
                                portList = (ExpandableListView)findViewById(R.id.expandableListView);

                                MyExpandableListAdapter elvAdapter = new MyExpandableListAdapter(MainActivity.this, groups);
                                portList.setAdapter(elvAdapter);
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                        break;
                    default:
                        try {
                            currentData = new JSONObject(loadJSONFromAssets("ogsp2011.json"));
                            if(inFrench) {
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

                            for (int i = 0; i < org.length(); i++) {
                                Group group = new Group(org.getString(i));
                                for (int j = 0; j < 3; j++) {
                                    if(j == 0) {
                                        if(inFrench){
                                            group.children.add(getString(R.string.col_name_fr) + ": " + name.getString(i));
                                        }
                                        else {
                                            group.children.add("Name: " + name.getString(i));
                                        }
                                    }
                                    else if(j == 1) {
                                        if(inFrench) {
                                            group.children.add(getString(R.string.col_strat_fr) + ": " + stratOut.getString(i));
                                        }
                                        else {
                                            group.children.add("Strategic Outcome: " + stratOut.getString(i));
                                        }
                                    }
                                    else {
                                        if(inFrench) {
                                            group.children.add(getString(R.string.col_spend_fr) + ": $" + spending.getString(i));
                                        }
                                        else {
                                            group.children.add("Actual Spending: $" + spending.getString(i));
                                        }
                                    }
                                }
                                groups.append(i, group);
                            }

                            if(findViewById(R.id.expandableListView) != null){
                                portList = (ExpandableListView)findViewById(R.id.expandableListView);

                                MyExpandableListAdapter elvAdapter = new MyExpandableListAdapter(MainActivity.this, groups);
                                portList.setAdapter(elvAdapter);
                            }

                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //TODO: Set AutocompleteTextView to ExpandableListAdapter somehow...
        final AutoCompleteTextView acv = (AutoCompleteTextView)findViewById(R.id.autoComplete1);
        final TextView orgLabel = (TextView)findViewById(R.id.orgLabel);
        final TextView stratLabel = (TextView)findViewById(R.id.stratOutLabel);
        final TextView nameLabel = (TextView)findViewById(R.id.nameLabel);
        final TextView spendLabel = (TextView)findViewById(R.id.spendingLabel);

        Switch lang = (Switch)findViewById(R.id.switch2);

        lang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    inFrench = true;
                    dropdown.getSelectedView().performClick(); // Doesn't affect standard spinner class...
                    aboutTitle = R.string.action_bar_about_fr;
                    helpTitle = R.string.action_bar_help_fr;
                    aboutText = getApplicationContext().getString(R.string.about_text_fr);
                    helpText = getApplicationContext().getString(R.string.instructions_fr);
                    portHint = getApplicationContext().getString(R.string.hint_fr);
                    acv.setHint(portHint);
                    if(findViewById(R.id.orgLabel) != null) {
                        orgLabel.setText(getApplicationContext().getString(R.string.col_org_fr));
                        stratLabel.setText(getApplicationContext().getString(R.string.col_strat_fr));
                        nameLabel.setText(getApplicationContext().getString(R.string.col_name_fr));
                        spendLabel.setText(getApplicationContext().getString(R.string.col_spend_fr));
                    }
                    invalidateOptionsMenu();
                } else {
                    inFrench = false;
                    dropdown.getSelectedView().performClick();
                    aboutTitle = R.string.action_bar_about_en;
                    helpTitle = R.string.action_bar_help_en;
                    aboutText = getApplicationContext().getString(R.string.about_text);
                    helpText = getApplicationContext().getString(R.string.instructions_en);
                    portHint = getApplicationContext().getString(R.string.hint_en);
                    acv.setHint(portHint);
                    if(findViewById(R.id.orgLabel) != null) {
                        orgLabel.setText(getApplicationContext().getString(R.string.col_org_en));
                        stratLabel.setText(getApplicationContext().getString(R.string.col_strat_en));
                        nameLabel.setText(getApplicationContext().getString(R.string.col_name_en));
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
}
