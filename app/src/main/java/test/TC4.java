package test;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.dcu.superword_a_day.MainMenuActivity;
import com.dcu.superword_a_day.OptionsActivity;
import com.dcu.superword_a_day.R;
import com.dcu.superword_a_day.TodaysRevision;
import com.dcu.superword_a_day.WordViewer;
import com.robotium.solo.Solo;

/**
 * Created by Brendan on 28/05/2015.
 */
public class TC4 extends ActivityInstrumentationTestCase2 {
    public TC4() {
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

    //
    public void testRevisionActivity() throws Exception {
        solo.clickOnButton("Options");
        solo.waitForActivity(OptionsActivity.class);
        ((EditText) solo.getView(R.id.revision_edit1)).setText("0");
        ((EditText) solo.getView(R.id.revision_edit2)).setText("0");
        ((EditText) solo.getView(R.id.revision_edit3)).setText("0");
        ((EditText) solo.getView(R.id.revision_edit4)).setText("0");
        solo.sleep(1000);

        /*
        // killing all your Activities manually if it doesn't by itself anyway
        solo.finishOpenedActivities();
        this.launchActivity("com.dcu.superword_a_day", MainMenuActivity.class, null);

        solo.sleep(1000);
        */
        solo.goBack();
        // there can't be any words for revision
        assertTrue(!(((Button) solo.getView(R.id.todaysRevisionButton)).isShown()));


        Log.i("tc4", "revision interval 1 = 1");
        /*
        solo.clickOnButton("Options");
        try {
            this.runTestOnUiThread(new Runnable() {
                @Override
                public void run() {
                    solo.waitForActivity(OptionsActivity.class);
                    ((EditText) solo.getView(R.id.revision_edit1)).setText("1");
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        solo.sleep(1000);
        solo.finishOpenedActivities();
        this.launchActivity("com.dcu.superword_a_day", MainMenuActivity.class, null);
        solo.sleep(1000);
        */
        solo.clickOnButton("Options");
        solo.waitForActivity(OptionsActivity.class);
        ((EditText) solo.getView(R.id.revision_edit1)).setText("1");
        solo.goBack();
        solo.waitForActivity(MainMenuActivity.class);

        if(((Button) solo.getView(R.id.todaysRevisionButton)).isShown()) {
            solo.clickOnButton("Today's Revision");
            solo.waitForActivity(WordViewer.class);
        }

        // repeating for a first revision interval between 2 and 10
        for(int i = 2; i < 8; i++) {
            solo.clickOnButton("Options");
            solo.waitForActivity(OptionsActivity.class);
            ((EditText) solo.getView(R.id.revision_edit1)).setText("" + i);
            solo.goBack();
            solo.waitForActivity(MainMenuActivity.class);

            if(((Button) solo.getView(R.id.todaysRevisionButton)).isShown()) {
                solo.clickOnButton("Today's Revision");
                solo.waitForActivity(WordViewer.class);
                Log.i("tc4", "Words found for revision from " + i + " days ago.");
                solo.goBack();
            }
        }

    }
}
