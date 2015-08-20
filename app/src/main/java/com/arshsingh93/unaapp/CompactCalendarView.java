package com.arshsingh93.unaapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.OverScroller;

import java.util.Date;
import java.util.Locale;

/**
 * calender front end for user to interact with
 * Created by Todd on 8/19/2015.
 */
public class CompactCalendarView extends View {

    private CompactCalendarController compactCalendarController;
    private GestureDetectorCompat gestureDetector;
    private CompactCalendarViewListener listener;

    public interface CompactCalendarViewListener {
        public void onDayClick(Date dateClicked);
        public void onMonthScroll(Date firstDayOfNewMonth);
    }

    private final GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Date onDateClicked = compactCalendarController.onSingleTapConfirmed(e);
            invalidate();
            if(listener != null){
                listener.onDayClick(onDateClicked);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return compactCalendarController.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            compactCalendarController.onFling(e1, e2, velocityX, velocityY);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            compactCalendarController.onScroll(e1, e2, distanceX, distanceY);
            invalidate();
            return true;
        }
    };

    public CompactCalendarView(Context context) {
        this(context, null);
    }

    public CompactCalendarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompactCalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        compactCalendarController = new CompactCalendarController(new Paint(), new OverScroller(getContext()),
                new Rect(), attrs, getContext(),  Color.argb(255, 233, 84, 81), Color.argb(255, 64, 64, 64), Color.argb(255, 219, 219, 219));
        gestureDetector = new GestureDetectorCompat(getContext(), gestureListener);
    }

    /**
     * Use a custom locale.
     */
    public void setLocale(Locale locale){
        compactCalendarController.setLocale(locale);
        invalidate();
    }

    /**
     * to determine the abbreviation to use as the day column names.
     * The default is to use the default locale and to abbreviate the day names to one character.
     * Setting this to true will displace the short weekday string provided by java.
     */
    public void setUseThreeLetterAbbreviation(boolean useThreeLetterAbbreviation){
        compactCalendarController.setUseWeekDayAbbreviation(useThreeLetterAbbreviation);
        invalidate();
    }

    /**
     * draws the indicator for events as a small dot under the day rather than a circle behind the day.
     */
    public void drawSmallIndicatorForEvents(boolean shouldDrawDaysHeader){
        compactCalendarController.showSmallIndicator(shouldDrawDaysHeader);
    }

    /**
     * Sets the name for each day of the week.
     */
    public void setDayColumnNames(String[] dayColumnNames){
       compactCalendarController.setDayColumnNames(dayColumnNames);
    }

    public int getHeightPerDay(){
        return compactCalendarController.getHeightPerDay();
    }

    public void setListener(CompactCalendarViewListener listener){
        this.listener = listener;
    }

    public Date getFirstDayOfCurrentMonth(){
        return compactCalendarController.getFirstDayOfCurrentMonth();
    }

    public void setCurrentDate(Date dateTimeMonth){
        compactCalendarController.setCurrentDate(dateTimeMonth);
        invalidate();
    }

    public int getWeekNumberForCurrentMonth(){
        return compactCalendarController.getWeekNumberForCurrentMonth();
    }

    public void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader){
        compactCalendarController.setShouldDrawDaysHeader(shouldDrawDaysHeader);
    }

   /**
    * Adds an event to be drawn as an indicator in the calendar.
    */
   public void addEvent(CalendarDayEvent event){
        compactCalendarController.addEvent(event);
   }

   public void removeEvent(CalendarDayEvent event){
       compactCalendarController.removeEvent(event);
   }

    public void showNextMonth(){
        compactCalendarController.showNextMonth();
        invalidate();
        if(listener != null){
             listener.onMonthScroll(compactCalendarController.getFirstDayOfCurrentMonth());
        }
    }

    public void showPreviousMonth(){
        compactCalendarController.showPreviousMonth();
        invalidate();
        if(listener != null){
             listener.onMonthScroll(compactCalendarController.getFirstDayOfCurrentMonth());
        }
    }

    @Override
    protected void onMeasure(int parentWidth, int parentHeight) {
        super.onMeasure(parentWidth, parentHeight);
        if(getWidth() > 0 && getHeight() > 0) {
            compactCalendarController.onMeasure(getWidth(), getHeight(), getPaddingRight(), getPaddingLeft());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        compactCalendarController.onDraw(canvas);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(compactCalendarController.computeScroll()){
            invalidate();
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if(compactCalendarController.onTouch(event)){
            invalidate();
            if(listener != null){
                listener.onMonthScroll(compactCalendarController.getFirstDayOfCurrentMonth());
            }
            return true;
        }
        return gestureDetector.onTouchEvent(event);
    }

}
