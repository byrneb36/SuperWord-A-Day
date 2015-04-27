package com.dcu.superword_a_day;

import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.MotionEvent;
import android.widget.TextView;

public class LongClickLinkMovementMethod extends LinkMovementMethod {

	private Long lastClickTime = 0l;
	private int lastX = 0;
	private int lastY = 0;
	private Timer timer;
	
	@Override
	public boolean onTouchEvent(final TextView widget, Spannable buffer, MotionEvent event) {
	    int action = event.getAction();
	
	    if (action == MotionEvent.ACTION_UP ||
	            action == MotionEvent.ACTION_DOWN) {
	        int x = (int) event.getX();
	        int y = (int) event.getY();
	        lastX = x;
	        lastY = y;
	        int deltaX = Math.abs(x-lastX);
	        int deltaY = Math.abs(y-lastY);
	
	        x -= widget.getTotalPaddingLeft();
	        y -= widget.getTotalPaddingTop();
	
	        x += widget.getScrollX();
	        y += widget.getScrollY();
	
	        Layout layout = widget.getLayout();
	        int line = layout.getLineForVertical(y);
	        int off = layout.getOffsetForHorizontal(line, x);
	
	        final LongClickableSpan[] link = buffer.getSpans(off, off, LongClickableSpan.class);
	
	        if (link.length != 0) {
	            if (action == MotionEvent.ACTION_UP) {
	            	timer.cancel();
	                if (System.currentTimeMillis() - lastClickTime < 750)
	                    link[0].onClick(widget);
	                /*
	                else if (deltaX < 10 && deltaY < 10)
	                    link[0].onLongClick(widget);
	                */
	            } else if (action == MotionEvent.ACTION_DOWN) {
	                Selection.setSelection(buffer,
	                        buffer.getSpanStart(link[0]),
	                        buffer.getSpanEnd(link[0]));
	                final Handler handler = new Handler();
	                timer = new Timer();	                
	                TimerTask timerTask = new TimerTask() {
	                	public void run() {
	                		handler.post(new Runnable() {
	                			public void run() {
	                				link[0].onLongClick(widget);
	                			}
	                		});
	                	}
	                };
	                // wait 999 milliseconds before running the task
	                timer.schedule(timerTask, 750);
	                lastClickTime = System.currentTimeMillis();

	            }
	        return true;
	        }
	    }
	
	    return super.onTouchEvent(widget, buffer, event);
	}
	
	public static MovementMethod getInstance() {
	    if (sInstance == null)
	        sInstance = new LongClickLinkMovementMethod();
	
	    return sInstance;
	}
	private static LongClickLinkMovementMethod sInstance;
}
