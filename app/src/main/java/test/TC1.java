package test;

import android.content.Context;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.dcu.superword_a_day.MainMenuActivity;
import com.dcu.superword_a_day.R;
import com.dcu.superword_a_day.TestActivity;
import com.dcu.superword_a_day.WordViewer;
import com.robotium.solo.Solo;

/**
 * Created by Brendan on 23/05/2015.
 */
public class TC1 extends ActivityInstrumentationTestCase2 {
    public TC1() {
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

    public void testTodaysWords() throws Exception {
        solo.clickOnButton("Today's Words");
        solo.waitForActivity(WordViewer.class);
        solo.waitForView(R.id.definition1);
        swipe("left");
        swipe("left");
        swipe("left");
        View s = solo.getView(R.id.content);
        assertTrue("sliding drawer not visible", !(s.isShown()));
        swipe("up");
        solo.waitForView(R.id.slidingDrawer1);
        solo.clickOnButton(R.id.content);
        solo.waitForActivity(TestActivity.class);

    }

    private void swipe(String direction) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager)getActivity().getApplication().getSystemService(Context.WINDOW_SERVICE))
                .getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        int screenHeight = displaymetrics.heightPixels;
        int toX = -1, fromX = -1, fromY = -1, toY = -1;
        if("left".equals(direction)) {
            toX = (screenWidth / 2) - (screenWidth / 3);
            fromX = (screenWidth / 2) + (screenWidth / 3);
            fromY = screenHeight/2;
            toY = screenHeight/2;
        }
        else if("right".equals(direction)) {
            fromX = (screenWidth / 2) - (screenWidth / 3);
            toX = (screenWidth / 2) + (screenWidth / 3);
            fromY = screenHeight/2;
            toY = screenHeight/2;
        }
        else if("up".equals(direction)) {
            fromX = (screenWidth / 2);
            toX = (screenWidth / 2);
            fromY = screenHeight/2 + (screenHeight / 3);
            toY = screenHeight/2 - (screenHeight / 3);
        }
        else {
            Log.i("error", "invalid swipe direction");
        }

        solo.drag(fromX, toX, fromY, toY, 1);
        Log.i("robotium",direction + " swipe");
    }
}
