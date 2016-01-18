package test;

import android.app.LauncherActivity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.dcu.superword_a_day.MainMenuActivity;
import com.dcu.superword_a_day.R;
import com.dcu.superword_a_day.WordArchiveActivity;
import com.dcu.superword_a_day.WordViewer;
import com.robotium.solo.Solo;

import org.apache.commons.lang3.ObjectUtils;

/**
 * Created by Brendan on 27/05/2015.
 */
    public class TC2 extends ActivityInstrumentationTestCase2 {

    public TC2() {
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


    public void testWordArchiveActivity() throws Exception {
        solo.clickOnButton("Word Archive");
        solo.clickInList(1);
        ListView l = (ListView)solo.getView(android.R.id.list);

        int j = 0;
        do {
            Log.i("tc2", "j: " + j);
            TextView t = (TextView) l.getChildAt(j).findViewById(R.id.textView1);
            ColorDrawable drawable = (ColorDrawable) t.getBackground();
            try {
                //Log.i("tc2", "textview colour: " + drawable.getColor());
                if (drawable.getColor() == -8947849) {
                    Log.i("tc2", "colour == GRAY");
                }
                else {
                    Log.i("tc2", "colour == RED");
                }
            }
            catch(NullPointerException e) {
                Log.i("tc2", "no color available");
                //if(solo.getCurrentActivity().equals(WordArchiveActivity.class)) {
                    Log.i("tc2", "clicking on: " + t.getText());
                    solo.clickOnText("" + t.getText());
                    solo.waitForActivity(WordViewer.class);
                //}
                Log.i("tc2", "word: " + solo.getText(1).getText());
                solo.goBack();
                Log.i("tc2", "after goBack");
                solo.waitForActivity(WordArchiveActivity.class);
                Log.i("tc2", "after waitForActivity");
                //e.printStackTrace();
            }
            j++;
        } while (l.getChildAt(j) != null);



    }
}
