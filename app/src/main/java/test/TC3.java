package test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;

import com.dcu.superword_a_day.MainMenuActivity;
import com.dcu.superword_a_day.OptionsActivity;
import com.dcu.superword_a_day.R;
import com.robotium.solo.Solo;

/**
 * Created by Brendan on 27/05/2015.
 */
public class TC3 extends ActivityInstrumentationTestCase2 {
    public TC3() {
        super(MainMenuActivity.class);
    }

    private Solo solo;

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        //solo.finishOpenedActivities();
    }

    public void testOptionsActivity() throws Exception {
        solo.clickOnButton("Options");
        solo.waitForActivity(OptionsActivity.class);


        // setting the number of words to learn per day to 1
        Spinner v = (Spinner) solo.getView(R.id.numToLearnSpinner);
        String currentlySelected = v.getSelectedItem().toString();
        int currentlySelectedInt = Integer.parseInt(currentlySelected);
        Log.i("tc3", "currently selected spinner item 1: " + currentlySelected);
        solo.pressSpinnerItem(0, -currentlySelectedInt);
        currentlySelected = v.getSelectedItem().toString();
        Log.i("tc3", "currently selected spinner item 2: " + currentlySelected);
        //Log.i("tc3", "spinner item: " + v.getItemAtPosition(4).toString());
        solo.goBack();

        // checking that the option is still set to 1
        solo.clickOnButton("Options");
        solo.waitForActivity(OptionsActivity.class);
        // setting the number of words to learn per day to 1
        v = (Spinner) solo.getView(R.id.numToLearnSpinner);
        currentlySelected = v.getSelectedItem().toString();
        Log.i("tc3", "currently selected spinner item 3: " + currentlySelected);
        currentlySelectedInt = Integer.parseInt(currentlySelected);
        assertTrue(currentlySelectedInt == 1);

        // setting the option to 20 (the maximum allowed)
        solo.pressSpinnerItem(0, 20);
        assertTrue((Integer.parseInt(v.getSelectedItem().toString()) == 20));

        // checking that the option is still set to 20
        solo.goBack();
        solo.clickOnButton("Options");
        solo.waitForActivity(OptionsActivity.class);
        assertTrue((Integer.parseInt(v.getSelectedItem().toString()) == 20));

        // resetting to 3
        solo.pressSpinnerItem(0, -17);
        assertTrue((Integer.parseInt(v.getSelectedItem().toString()) == 3));

        // setting all of the revision intervals to 0
        ((EditText) solo.getView(R.id.revision_edit1)).setText("0");
        ((EditText) solo.getView(R.id.revision_edit2)).setText("0");
        ((EditText) solo.getView(R.id.revision_edit3)).setText("0");
        ((EditText) solo.getView(R.id.revision_edit4)).setText("0");

        // ensuring the revision intervals have been saved
        solo.goBack();
        solo.waitForActivity(MainMenuActivity.class);
        solo.clickOnButton("Options");
        solo.sleep(2000);
        solo.waitForActivity(OptionsActivity.class);
        assertTrue(((EditText) solo.getView(R.id.revision_edit1)).getText().toString().equals("0"));
        assertTrue(((EditText) solo.getView(R.id.revision_edit2)).getText().toString().equals("0"));
        assertTrue(((EditText) solo.getView(R.id.revision_edit3)).getText().toString().equals("0"));
        assertTrue(((EditText) solo.getView(R.id.revision_edit4)).getText().toString().equals("0"));
    }
}
