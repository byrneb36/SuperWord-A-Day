package com.dcu.superword_a_day;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SlidingDrawer;
import android.widget.TextView;
import android.widget.Toast;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.LinkedList;


public class SelfTestFragment extends Fragment {
    private int mNum;
    //private String file_name1;
    private String mSource;
    private View selfTestFragmentView;

    public SelfTestFragment() {
        // Required empty public constructor
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
    }

    static SelfTestFragment newInstance(int num) {
        Log.i("SelfTestFragment", "inside SelfTestFragment newInstance");
        SelfTestFragment f = new SelfTestFragment();
        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        selfTestFragmentView = inflater.inflate(R.layout.fragment_self_test, container, false);
        String wordStr = SelfTestActivity.word_array[mNum];
        String definitionStr = SelfTestActivity.wordsAndDefinitionsMap.get(wordStr);
        View numView = selfTestFragmentView.findViewById(R.id.fragmentNumView);

        final SlidingDrawer s = (SlidingDrawer) selfTestFragmentView.findViewById(R.id.selfTestSlidingDrawer);

        ((TextView)numView).setText("" + mNum);

        TextView wordView = (TextView) selfTestFragmentView.findViewById(R.id.word2);
        wordView.setText(wordStr);

        ((TextView) selfTestFragmentView.findViewById(R.id.sample1).findViewById(R.id.title)).
                setText(getString(R.string.self_test_definition));

        ExpandableTextView expTv1 = (ExpandableTextView) selfTestFragmentView.findViewById(R.id.sample1)
                .findViewById(R.id.expand_text_view);

        expTv1.setText(definitionStr);

        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        System.out.println("SelfTestFragment Gesture 1: " +e1+", 2: " +e2);
                        // determine if the intention is to swipe vertically rather than horizontally
                        if(Math.abs(e1.getY() - e2.getY()) > Math.abs(e1.getX() - e2.getX())) {
                            // determine whether it's an up or down swipe
                            if(e2.getY() < e1.getY()) {
                                s.setVisibility(View.VISIBLE); // set to visible
                                s.animateOpen();
                            }
                            else {
                                s.animateClose();
                            }
                            return true;
                        }
                        else {
                            s.setVisibility(View.INVISIBLE); // set to invisible
                            s.close(); // make sure the sliding drawer is always closed when a fragment is swiped to
                            return false;
                        }
                    }
                });

        selfTestFragmentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        CheckBox checkbox = (CheckBox) selfTestFragmentView.findViewById(R.id.selfTestCheckbox);
        checkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    SelfTestActivity.checkbox_array[mNum] = true;
                }
                else
                    SelfTestActivity.checkbox_array[mNum] = false;

            }
        });

        return selfTestFragmentView;
    }

}
