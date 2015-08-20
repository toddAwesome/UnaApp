package com.arshsingh93.unaapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.widget.OverScroller;

//import com.github.sundeepk.compactcalendarview.domain.CalendarDayEvent;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Todd on 8/19/2015.
 */
class CompactCalendarController {

    private int paddingWidth = 40;
    private int paddingHeight = 40;
    private Paint dayPaint = new Paint();
    private Rect rect;
    private int textHeight;
    private int textWidth;
    private static final int DAYS_IN_WEEK = 7;
    private int widthPerDay;
    private String[] dayColumnNames;
    private float distanceX;
    private PointF accumulatedScrollOffset = new PointF();
    private OverScroller scroller;
    private int monthsScrolledSoFar;
    private Date currentDate = new Date();
    private Locale locale = Locale.getDefault();
    private Calendar currentCalender = Calendar.getInstance(locale);
    private Calendar calendarWithFirstDayOfMonth = Calendar.getInstance(locale);
    private Calendar eventsCalendar = Calendar.getInstance(locale);
    private Direction currentDirection = Direction.NONE;
    private int heightPerDay;
    private int currentDayBackgroundColor;
    private int calenderTextColor;
    private int firstDayBackgroundColor;
    private int calenderBackgroundColor = Color.WHITE;
    private int textSize = 30;
    private int width;
    private int height;
    private int paddingRight;
    private int paddingLeft;
    private boolean shouldDrawDaysHeader = true;
    private Map<String, List<CalendarDayEvent>> events = new HashMap<>();
    private boolean showSmallIndicator;

    private enum Direction {
        NONE, HORIZONTAL, VERTICAL
    }

    /**
     * all calenders objects
     * @param dayPaint
     * @param scroller
     * @param rect
     * @param attrs
     * @param context
     * @param currentDayBackgroundColor
     * @param calenderTextColor
     * @param firstDayBackgroundColor
     */
    CompactCalendarController(Paint dayPaint, OverScroller scroller, Rect rect, AttributeSet attrs,
                              Context context, int currentDayBackgroundColor, int calenderTextColor, int firstDayBackgroundColor){
        this.dayPaint = dayPaint;
        this.scroller = scroller;
        this.rect = rect;
        this.currentDayBackgroundColor = currentDayBackgroundColor;
        this.calenderTextColor = calenderTextColor;
        this.firstDayBackgroundColor = firstDayBackgroundColor;
        loadAttributes(attrs, context);
        init();
    }

    /**
     * colors and look of calender
     * @param attrs
     * @param context
     */
    private void loadAttributes(AttributeSet attrs, Context context) {
        if(attrs != null && context != null){
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs,  R.styleable.CompactCalendarView, 0, 0);
            try{
                currentDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarCurrentDayBackgroundColor, currentDayBackgroundColor);
                calenderTextColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarTextColor, calenderTextColor);
                firstDayBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarFirstDayOfMonthBackgroundColor, firstDayBackgroundColor);
                firstDayBackgroundColor = TheColorUtil.getProperColor();
                calenderBackgroundColor = typedArray.getColor(R.styleable.CompactCalendarView_compactCalendarBackgroundColor, calenderBackgroundColor);
                calenderBackgroundColor = TheColorUtil.getProperColor();
                textSize = typedArray.getDimensionPixelSize(R.styleable.CompactCalendarView_compactCalendarTextSize,
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, context.getResources().getDisplayMetrics()));
            }finally{
                typedArray.recycle();
            }
        }
    }

    private void init() {
        setUseWeekDayAbbreviation(false);
        dayPaint.setTextAlign(Paint.Align.CENTER);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        dayPaint.setTypeface(Typeface.SANS_SERIF);
        dayPaint.setTextSize(textSize);
        dayPaint.setColor(calenderTextColor);
        dayPaint.getTextBounds("31", 0, "31".length(), rect);
        textHeight = rect.height() * 3;
        textWidth = rect.width() * 2;

        currentCalender.setTime(currentDate);
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);

        eventsCalendar.setFirstDayOfWeek(Calendar.MONDAY);
    }

    /**
     * sets the first day of the month and makes it white
     * @param calendarWithFirstDayOfMonth
     * @param currentDate
     * @param monthOffset
     */
    private void setCalenderToFirstDayOfMonth(Calendar calendarWithFirstDayOfMonth, Date currentDate, int monthOffset) {
        calendarWithFirstDayOfMonth.setTime(currentDate);
        calendarWithFirstDayOfMonth.add(Calendar.MONTH, -monthsScrolledSoFar + monthOffset);
        calendarWithFirstDayOfMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MINUTE, 0);
        calendarWithFirstDayOfMonth.set(Calendar.SECOND, 0);
        calendarWithFirstDayOfMonth.set(Calendar.MILLISECOND, 0);
        calendarWithFirstDayOfMonth.set(Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * go to the next month
     */
    void showNextMonth(){
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 1);
        setCurrentDate(calendarWithFirstDayOfMonth.getTime());
    }

    /**
     * go to the previous month
     */
    void showPreviousMonth(){
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -1);
        setCurrentDate(calendarWithFirstDayOfMonth.getTime());
    }

    void setLocale(Locale locale){
        if(locale == null){
            throw new IllegalArgumentException("Locale cannot be null");
        }
        this.locale = locale;
    }

    /**
     * makes week day abbreviations
     * @param useThreeLetterAbbreviation
     */
    void setUseWeekDayAbbreviation(boolean useThreeLetterAbbreviation){
        DateFormatSymbols dateFormatSymbols = new DateFormatSymbols(locale);
        String[] dayNames = dateFormatSymbols.getShortWeekdays();
        if(dayNames == null){
            throw new IllegalStateException("Unable to determine weekday names from default locale");
        }
        if(dayNames.length != 8){
            throw new IllegalStateException("Expected weekday names from default locale to be of size 7 but: "
                    + Arrays.toString(dayNames) + " with size " + dayNames.length + " was returned.");
        }
        if (useThreeLetterAbbreviation) {
            this.dayColumnNames = new String[]{dayNames[2], dayNames[3], dayNames[4], dayNames[5], dayNames[6], dayNames[7], dayNames[1]};
        } else {
            this.dayColumnNames = new String[]{dayNames[2].substring(0, 1), dayNames[3].substring(0, 1),
                    dayNames[4].substring(0, 1), dayNames[5].substring(0, 1), dayNames[6].substring(0, 1), dayNames[7].substring(0, 1), dayNames[1].substring(0, 1)};
        }
    }

    /**
     * sets the days in column order
     * @param dayColumnNames
     */
    void setDayColumnNames(String[] dayColumnNames){
        if(dayColumnNames == null || dayColumnNames.length != 7){
            throw new IllegalArgumentException("Column names cannot be null and must contain a value for each day of the week");
        }
        this.dayColumnNames = dayColumnNames;
    }
    void setShouldDrawDaysHeader(boolean shouldDrawDaysHeader){this.shouldDrawDaysHeader = shouldDrawDaysHeader;}

    void showSmallIndicator(boolean showSmallIndicator){this.showSmallIndicator = showSmallIndicator;}

    void onMeasure(int width, int height, int paddingRight, int paddingLeft) {
            widthPerDay = (width) / DAYS_IN_WEEK;
            heightPerDay = height / 7;
            this.width = width;
            this.height = height;
            this.paddingRight = paddingRight;
            this.paddingLeft = paddingLeft;
    }

    void onDraw(Canvas canvas) {
        paddingWidth = widthPerDay / 2;
        paddingHeight = heightPerDay / 2;
        calculateXPositionOffset();

        drawCalenderBackground(canvas);

        drawScrollableCalender(canvas);
    }

    /**
     * when calender is clicked
     * @param event
     * @return
     */
    boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (currentDirection == Direction.HORIZONTAL) {
                monthsScrolledSoFar = Math.round(accumulatedScrollOffset.x / width);
                float remainingScrollAfterFingerLifted = (accumulatedScrollOffset.x - monthsScrolledSoFar * width);
                scroller.startScroll((int) accumulatedScrollOffset.x, 0, (int) -remainingScrollAfterFingerLifted, 0);
                currentDirection = Direction.NONE;
                setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);
                return true;
            }
            currentDirection = Direction.NONE;
        }
        return false;
    }

    int getHeightPerDay(){
        return heightPerDay;
    }

    int getWeekNumberForCurrentMonth(){
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(currentDate);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    Date getFirstDayOfCurrentMonth(){
        Calendar calendar = Calendar.getInstance(locale);
        calendar.setTime(currentDate);
        calendar.add(Calendar.MONTH, -monthsScrolledSoFar);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setToMidnight(calendar);
        return calendar.getTime();
    }

    /**
     * sets the current date
     * @param dateTimeMonth
     */
    void setCurrentDate(Date dateTimeMonth){
        currentDate = new Date(dateTimeMonth.getTime());
        currentCalender.setTime(currentDate);
        setToMidnight(currentCalender);
        monthsScrolledSoFar = 0;
        accumulatedScrollOffset.x = 0;
    }

    /**
     * sets timers back to zero
     * @param calendar
     */
    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * hard coded but adds a list of events
     * @param event
     */
    void addEvent(CalendarDayEvent event){
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<CalendarDayEvent> uniqCalendarDayEvents = events.get(key);
        if(uniqCalendarDayEvents == null){
            uniqCalendarDayEvents = new ArrayList<>();
        }
        if(!uniqCalendarDayEvents.contains(event)){
            uniqCalendarDayEvents.add(event);
        }
        events.put(key, uniqCalendarDayEvents);
    }

    /**
     * made to remove events but not implemnted in the app
     * @param event
     */
    void removeEvent(CalendarDayEvent event){
        eventsCalendar.setTimeInMillis(event.getTimeInMillis());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<CalendarDayEvent> uniqCalendarDayEvents = events.get(key);
        if(uniqCalendarDayEvents != null){
            uniqCalendarDayEvents.remove(event);
        }
    }

    /**
     * gets the events in the list
     * @param date
     * @return
     */
    List<CalendarDayEvent> getEvents(Date date){
        eventsCalendar.setTimeInMillis(date.getTime());
        String key = getKeyForCalendarEvent(eventsCalendar);
        List<CalendarDayEvent> uniqEvents = events.get(key);
        if(events != null){
            return uniqEvents;
        }else{
            return new ArrayList<>();
        }
    }

    /**
     * E.g. 4 2016 becomes 2016_4
     */
    private String getKeyForCalendarEvent(Calendar cal) {
        return cal.get(Calendar.YEAR) + "_" + cal.get(Calendar.MONTH);
    }

    Date onSingleTapConfirmed(MotionEvent e) {
        monthsScrolledSoFar = Math.round(accumulatedScrollOffset.x / width);
        int dayColumn = Math.round((paddingLeft + e.getX() - paddingWidth - paddingRight) / widthPerDay);
        int dayRow = Math.round((e.getY() - paddingHeight) / heightPerDay);

        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);

        /*
         * Start Monday as day 1 and Sunday as day 7. Not Sunday as day 1 and Monday as day 2
         */
        int firstDayOfMonth = calendarWithFirstDayOfMonth.get(Calendar.DAY_OF_WEEK) - 1;
        firstDayOfMonth = firstDayOfMonth <= 0 ? 7 : firstDayOfMonth;

        int dayOfMonth = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;

        calendarWithFirstDayOfMonth.add(Calendar.DATE, dayOfMonth);

        currentCalender.setTimeInMillis(calendarWithFirstDayOfMonth.getTimeInMillis());
        return currentCalender.getTime();
    }

    boolean onDown(MotionEvent e) {
        scroller.forceFinished(true);
        return true;
    }

    boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        scroller.forceFinished(true);
        return true;
    }

    boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (currentDirection == Direction.NONE) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                currentDirection = Direction.HORIZONTAL;
            } else {
                currentDirection = Direction.VERTICAL;
            }
        }

        this.distanceX = distanceX;
        return true;
    }

    boolean computeScroll() {
        if (scroller.computeScrollOffset()) {
            accumulatedScrollOffset.x = scroller.getCurrX();
            return true;
        }
        return false;
    }

    private void drawScrollableCalender(Canvas canvas) {
        monthsScrolledSoFar = (int) (accumulatedScrollOffset.x / width);

        drawPreviousMonth(canvas);

        drawCurrentMonth(canvas);

        drawNextMonth(canvas);
    }

    private void drawNextMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar + 1)));
    }

    private void drawCurrentMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, 0);
        drawMonth(canvas, calendarWithFirstDayOfMonth, width * -monthsScrolledSoFar);
    }

    private void drawPreviousMonth(Canvas canvas) {
        setCalenderToFirstDayOfMonth(calendarWithFirstDayOfMonth, currentDate, -1);
        drawMonth(canvas, calendarWithFirstDayOfMonth, (width * (-monthsScrolledSoFar - 1)));
    }

    private void calculateXPositionOffset() {
        if (currentDirection == Direction.HORIZONTAL) {
            accumulatedScrollOffset.x -= distanceX;
        }
    }

    private void drawCalenderBackground(Canvas canvas) {
        dayPaint.setColor(calenderBackgroundColor);
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextColor);
    }

    void drawEvents(Canvas canvas, Calendar currentMonthToDrawCalender, int offset){
        List<CalendarDayEvent> uniqCalendarDayEvents =
                events.get(getKeyForCalendarEvent(currentMonthToDrawCalender));
        boolean shouldDrawCurrentDayCircle = currentMonthToDrawCalender.get(Calendar.MONTH) == currentCalender.get(Calendar.MONTH);
        if(uniqCalendarDayEvents != null){
            for(int i = 0; i < uniqCalendarDayEvents.size() ; i++){
                CalendarDayEvent event = uniqCalendarDayEvents.get(i);
                long timeMillis = event.getTimeInMillis();
                eventsCalendar.setTimeInMillis(timeMillis);

                int dayOfWeek = eventsCalendar.get(Calendar.DAY_OF_WEEK) - 1;
                dayOfWeek = dayOfWeek <= 0 ? 7 : dayOfWeek;
                dayOfWeek = dayOfWeek - 1;

                int weekNumberForMonth = eventsCalendar.get(Calendar.WEEK_OF_MONTH);
                float xPosition = widthPerDay * dayOfWeek + paddingWidth + paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
                float yPosition = weekNumberForMonth * heightPerDay + paddingHeight;

                int dayOfMonth = eventsCalendar.get(Calendar.DAY_OF_MONTH);
                boolean isSameDayAsCurrentDay = (currentCalender.get(Calendar.DAY_OF_MONTH) == dayOfMonth && shouldDrawCurrentDayCircle);
                if(!isSameDayAsCurrentDay && dayOfMonth != 1){
                    if(showSmallIndicator){
                        //draw small indicators below the day in the calendar
                        drawSmallIndicatorCircle(canvas, xPosition , yPosition + 15, event.getColor());
                    }else{
                        drawCircle(canvas, xPosition, yPosition, event.getColor());
                    }
                }

            }
        }
    }

    void drawMonth(Canvas canvas, Calendar currentMonthToDrawCalender, int offset) {
        drawEvents(canvas, currentMonthToDrawCalender, offset);

        /**
         * offset by one because we want to start from Monday
         */
        int firstDayOfMonth = currentMonthToDrawCalender.get(Calendar.DAY_OF_WEEK) - 1;
        firstDayOfMonth = firstDayOfMonth <= 0 ? 7 : firstDayOfMonth;

        /**
         * offset by one because of 0 index based calculations
         */
        firstDayOfMonth = firstDayOfMonth - 1;
        boolean shouldDrawCurrentDayCircle = currentMonthToDrawCalender.get(Calendar.MONTH) == currentCalender.get(Calendar.MONTH);

        for(int dayColumn = 0, dayRow = 0; dayColumn <= 6; dayRow++){
            if(dayRow == 7){
                dayRow = 0;
                if(dayColumn <= 6){
                    dayColumn++;
                }
            }
            if(dayColumn == dayColumnNames.length){
                break;
            }
            float xPosition = widthPerDay * dayColumn + paddingWidth +  paddingLeft + accumulatedScrollOffset.x + offset - paddingRight;
            if(dayRow == 0){
                // first row, so draw the first letter of the day
                if(shouldDrawDaysHeader){
                    dayPaint.setTypeface(Typeface.DEFAULT_BOLD);
                    canvas.drawText(dayColumnNames[dayColumn], xPosition, paddingHeight, dayPaint);
                    dayPaint.setTypeface(Typeface.DEFAULT);
                }
            }else{
                int day = ((dayRow - 1) * 7 + dayColumn + 1) - firstDayOfMonth;
                float yPosition = dayRow * heightPerDay + paddingHeight;
                if(shouldDrawCurrentDayCircle && currentCalender.get(Calendar.DAY_OF_MONTH) == day){
                    // TODO calculate position of circle in a more reliable way
                    drawCircle(canvas, xPosition, yPosition, currentDayBackgroundColor);
                }
                if(day <= currentMonthToDrawCalender.getActualMaximum(Calendar.DAY_OF_MONTH) && day > 0){
                    if(day == 1){
                        drawCircle(canvas, xPosition, yPosition, firstDayBackgroundColor);
                    }
                    canvas.drawText(String.valueOf(day), xPosition, yPosition, dayPaint);
                }
            }

        }
    }

    /**
     * Draw Circle on certain days to highlight them
     */
    private void drawCircle(Canvas canvas, float x, float y, int color) {
        dayPaint.setColor(color);
        float radius = (float) (0.5 * Math.sqrt(widthPerDay * widthPerDay + heightPerDay * heightPerDay));
        // add some padding to height
        drawCircle(canvas, radius/2, x, y - (textHeight /6));
    }

    private void drawSmallIndicatorCircle(Canvas canvas, float x, float y, int color) {
        dayPaint.setColor(color);
        drawCircle(canvas, 5.0f, x, y);
    }

    private void drawCircle(Canvas canvas, float radius, float x, float y) {
        if(radius >= 34){
            radius = 34;
        }
        dayPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(x, y, radius, dayPaint);
        dayPaint.setStyle(Paint.Style.STROKE);
        dayPaint.setColor(calenderTextColor);
    }

}
