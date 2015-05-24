package test;

import android.test.ActivityInstrumentationTestCase2;

import com.dcu.superword_a_day.MainMenuActivity;
import com.robotium.solo.Solo;

/**
 * Created by Brendan on 23/05/2015.
 */
public class ExampleTest extends ActivityInstrumentationTestCase2 {
    public ExampleTest() {
        super(MainMenuActivity.class);
    }
    private Solo solo;

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }
    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    public void testButtons() throws Exception {
        solo.clickOnButton("Options");
    }
}
