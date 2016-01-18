package com.dcu.superword_a_day;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class OptionsActivity extends Activity implements OnItemSelectedListener {
    private int numOfWordsPerDay = -1;
    private int testType = -1;
    // default revision intervals:
    private int firstRevisionInterval = 1;
    private int secondRevisionInterval = 7;
    private int thirdRevisionInterval = 28;
    private int fourthRevisionInterval = 168;

    private boolean revisionSkipCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        int temp;

        // Restore preferences
        SharedPreferences settings = getSharedPreferences(Constants.OPTIONS_FILE, MODE_PRIVATE);
        numOfWordsPerDay = settings.getInt("numOfWordsPerDay", -1);
        testType = settings.getInt("testType", -1);
        revisionSkipCheckbox = settings.getBoolean("revisionSkipCheckbox", false);

        CheckBox checkbox_revision_skip = (CheckBox) findViewById(R.id.options_revision_skip_checkbox);
        if(revisionSkipCheckbox)
            checkbox_revision_skip.setChecked(true);


        temp = settings.getInt("firstRevisionInterval", -1);
        if(temp != -1) {
            firstRevisionInterval = temp;
        }
        temp = settings.getInt("secondRevisionInterval", -1);
        if(temp != -1) {
            secondRevisionInterval = temp;
        }
        temp = settings.getInt("thirdRevisionInterval", -1);
        if(temp != -1) {
            thirdRevisionInterval = temp;
        }
        temp = settings.getInt("fourthRevisionInterval", -1);
        if(temp != -1) {
            fourthRevisionInterval = temp;
        }

        Log.i("numOfWordsPerDay", "" + numOfWordsPerDay);
        Log.i("testType", "" + testType);
        Log.i("revision interval 1", "" + firstRevisionInterval);
        Log.i("revision interval 2", "" + secondRevisionInterval);
        Log.i("revision interval 3", "" + thirdRevisionInterval);
        Log.i("revision interval 4", "" + fourthRevisionInterval);

        //applySaved();

        Spinner spinner = (Spinner) findViewById(R.id.numToLearnSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> numToLearnAdapter = ArrayAdapter.createFromResource(this,
                R.array.numOfWords, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        numToLearnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(numToLearnAdapter);
        spinner.setOnItemSelectedListener(this);
        if(numOfWordsPerDay != -1) {
            switch(numOfWordsPerDay) {
                case 1: {
                    spinner.setSelection (0, false);
                    break;
                }
                case 2: {
                    spinner.setSelection (1, false);
                    break;
                }
                case 3: {
                    spinner.setSelection (2, false);
                    break;
                }
                case 4: {
                    spinner.setSelection (3, false);
                    break;
                }
                case 5: {
                    spinner.setSelection (4, false);
                    break;
                }
                case 6: {
                    spinner.setSelection (5, false);
                    break;
                }
                case 7: {
                    spinner.setSelection (6, false);
                    break;
                }
                case 8: {
                    spinner.setSelection (7, false);
                    break;
                }
                case 9: {
                    spinner.setSelection (8, false);
                    break;
                }
                case 10: {
                    spinner.setSelection (9, false);
                    break;
                }
                case 11: {
                    spinner.setSelection (10, false);
                    break;
                }
                case 12: {
                    spinner.setSelection (11, false);
                    break;
                }
                case 13: {
                    spinner.setSelection (12, false);
                    break;
                }
                case 14: {
                    spinner.setSelection (13, false);
                    break;
                }
                case 15: {
                    spinner.setSelection (14, false);
                    break;
                }
                case 16: {
                    spinner.setSelection (15, false);
                    break;
                }
                case 17: {
                    spinner.setSelection (16, false);
                    break;
                }
                case 18: {
                    spinner.setSelection (17, false);
                    break;
                }
                case 19: {
                    spinner.setSelection (18, false);
                    break;
                }
                case 20: {
                    spinner.setSelection (19, false);
                    break;
                }
            }
        }
        EditText editText1 = (EditText) findViewById(R.id.revision_edit1);
        editText1.setText(firstRevisionInterval + "");
        EditText editText2 = (EditText) findViewById(R.id.revision_edit2);
        editText2.setText(secondRevisionInterval + "");
        EditText editText3 = (EditText) findViewById(R.id.revision_edit3);
        editText3.setText(thirdRevisionInterval + "");
        EditText editText4 = (EditText) findViewById(R.id.revision_edit4);
        editText4.setText(fourthRevisionInterval + "");

        Spinner testTypeSpinner = (Spinner) findViewById(R.id.testTypeSpinner);
        ArrayAdapter<CharSequence> testTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.testTypes, android.R.layout.simple_spinner_item);
        testTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        testTypeSpinner.setAdapter(testTypeAdapter);
        testTypeSpinner.setOnItemSelectedListener(this);
        if(testType != -1) {
            Log.i("Setting", "setting selection");
            switch (testType) {
                case 0: {
                    testTypeSpinner.setSelection(0, false);
                    break;
                }
                case 1: {
                    testTypeSpinner.setSelection(1, false);
                    break;
                }
                case 2: {
                    testTypeSpinner.setSelection(2, false);
                    break;
                }
            }
        }

        checkbox_revision_skip.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    revisionSkipCheckbox = true;
                }
                else
                    revisionSkipCheckbox = false;

            }
        });
    }

    public void applySaved() {

    }

    @Override
    protected void onPause(){
        super.onPause();

        SharedPreferences settings = getSharedPreferences(Constants.OPTIONS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("numOfWordsPerDay", numOfWordsPerDay);
        editor.putInt("testType", testType);

        EditText editText1 = (EditText) findViewById(R.id.revision_edit1);
        String text1 = editText1.getText().toString();
        int first_interval = Integer.parseInt(text1);
        editor.putInt("firstRevisionInterval", first_interval);

        EditText editText2 = (EditText) findViewById(R.id.revision_edit2);
        String text2 = editText2.getText().toString();
        int second_interval = Integer.parseInt(text2);
        editor.putInt("secondRevisionInterval", second_interval);

        EditText editText3 = (EditText) findViewById(R.id.revision_edit3);
        String text3 = editText3.getText().toString();
        int third_interval = Integer.parseInt(text3);
        editor.putInt("thirdRevisionInterval", third_interval);

        EditText editText4 = (EditText) findViewById(R.id.revision_edit4);
        String text4 = editText4.getText().toString();
        int fourth_interval = Integer.parseInt(text4);
        editor.putInt("fourthRevisionInterval", fourth_interval);

        Log.i("revision intervals", "" + first_interval + " " + second_interval + " " + third_interval +
                " " + fourth_interval);

        editor.putBoolean("revisionSkipCheckbox", revisionSkipCheckbox);
        editor.commit();

        Context context = getApplicationContext();
        CharSequence text = "Changes Saved";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onItemSelected(AdapterView<?> parentView, View v, int position, long id) {
        String viewIDName = parentView.getResources().getResourceEntryName(parentView.getId());
        String thisItem = parentView.getItemAtPosition(position).toString();
        Log.i("OptionsActivity", "onItemSelected position: " + position + " thisItem: " + thisItem);
        Log.i("View ID name", viewIDName);
        if("testTypeSpinner".equals(viewIDName)) {
            Log.i("onItemSelected", "inside testTypeSpinner IF statement");
            testType = position;
        } else if("numToLearnSpinner".equals(viewIDName)) {
            numOfWordsPerDay = Integer.parseInt(thisItem);
        }
    }

    public void onNothingSelected(AdapterView<?> parentView){
        // do nothing
    }
}
