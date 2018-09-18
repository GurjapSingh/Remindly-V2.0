/*
 * Copyright 2015 Blanyal D'Souza.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.blanyal.remindme;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ReminderAddActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener {

    private Toolbar mToolbar;
    private EditText mTitleText;
    private TextView mDateText, mTimeText, mRepeatText, mPriorityTypeText, mReminderTypeText;  //mRepeatNoText, mRepeatTypeText,
    private FloatingActionButton mFAB1;
    private FloatingActionButton mFAB2;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private String mTitle; //
    private String mTime; //
    private String mDate; //
    private String mRepeat; //
    private String mRepeatNo;
    private String mRepeatType; //
    private String mActive;
    private String mPriority; //
    private String mType;
    private String mCreateDate;

    private RelativeLayout ReminderTypeLayout;

    /*Location Service vars*/
    public static final int REQUEST_PERMISSION_RESULT_CODE = 22;
    public static final int PLACE_PICKER_REQUEST = 1;
    private TextView pname;
    private double longtitude = 0;
    private double latitude = 0;
    private String placename;
    private String address;

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";
    private static final String KEY_PRIORITY = "priority_key";
    private static final String KEY_CREATEDATE = "create_date";

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);



        // Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        //mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        //mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mPriorityTypeText = (TextView) findViewById(R.id.set_priority_type);
        mReminderTypeText = (TextView) findViewById(R.id.set_reminder_type);
        ReminderTypeLayout = (RelativeLayout) findViewById(R.id.ReminderType);

        //LOCATION
        pname=(TextView) findViewById(R.id.pname);

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.title_activity_add_reminder);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialize default values
        mActive = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour(s)";
        mPriority = "1";
        mType = "Other";

        // saving data about create date and time
        DateFormat df = new SimpleDateFormat("d/M/yyyy hh:mm");
        Date today = Calendar.getInstance().getTime();
        mCreateDate = df.format(today);

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

        mDate = mDay + "/" + mMonth + "/" + mYear;
        mTime = mHour + ":" + mMinute;

        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Setup TextViews using reminder values
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        //mRepeatNoText.setText(mRepeatNo);
        //mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType);
        mPriorityTypeText.setText(mPriority);
        mReminderTypeText.setText(mType);

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(saveRepeat);
            mRepeat = saveRepeat;

            mRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);

            mRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        // Setup up active buttons
        if (mActive.equals("false")) {
            mFAB1.setVisibility(View.VISIBLE);
            mFAB2.setVisibility(View.GONE);

        } else if (mActive.equals("true")) {
            mFAB1.setVisibility(View.GONE);
            mFAB2.setVisibility(View.VISIBLE);
        }
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNo);
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatType);
        outState.putCharSequence(KEY_ACTIVE, mActive);
        outState.putCharSequence(KEY_PRIORITY, mPriority);
        outState.putCharSequence(KEY_CREATEDATE, mCreateDate);

    }

    // On clicking Time picker
    public void setTime(View v) {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    // On clicking Date picker
    public void setDate(View v) {
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    // Obtain time from time picker
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    // Obtain date from date picker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDateText.setText(mDate);
    }

    // On clicking the active button
    public void selectFab1(View v) {
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB1.setVisibility(View.GONE);
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mFAB2.setVisibility(View.VISIBLE);
        mActive = "true";
    }

    // On clicking the inactive button
    public void selectFab2(View v) {
        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
        mFAB2.setVisibility(View.GONE);
        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
        mFAB1.setVisibility(View.VISIBLE);
        mActive = "false";
    }

    // On clicking the repeat switch
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType);
        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);
        }
    }

    public void clickRepeat(View v) {
        final Switch switch1 = (Switch) this.findViewById(R.id.repeat_switch);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater repeatInflater = this.getLayoutInflater();
        final View setRepeatView = repeatInflater.inflate(R.layout.dialog_setrepeat, null);
        builder.setTitle("Repeat Settings");
        builder.setView(setRepeatView);
        EditText editText = (EditText) setRepeatView.findViewById(R.id.RepeatNo_Dialog);
        editText.setText(mRepeatNo);
        Spinner spinner = (Spinner) setRepeatView.findViewById(R.id.RepeatTypeSpinner);
        spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition(mRepeatType));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = (EditText) setRepeatView.findViewById(R.id.RepeatNo_Dialog);
                Spinner spinner = (Spinner) setRepeatView.findViewById(R.id.RepeatTypeSpinner);
                mRepeatNo = editText.getText().toString();
                mRepeatType = spinner.getSelectedItem().toString();
                mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType);
                switch1.setChecked(true);
                mRepeat = "true";
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    // On clicking Priority button
    public void selectPriorityType(View v) {
        final String[] items = new String[10];

        items[0] = "1";
        items[1] = "2";
        items[2] = "3";
        items[3] = "4";
        items[4] = "5";
        items[5] = "6";
        items[6] = "7";
        items[7] = "8";
        items[8] = "9";
        items[9] = "10";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Reminder's Priority");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mPriority = items[item];
                mPriorityTypeText.setText(mPriority);
                //mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void selectReminderType(View v) {
        PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), ReminderTypeLayout);
        dropDownMenu.getMenuInflater().inflate(R.menu.type_drop_down, dropDownMenu.getMenu());
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                mType = (String) menuItem.getTitle();
                mReminderTypeText.setText("mType");

                return true;
            }
        });
        dropDownMenu.show();
    }

    /*LOCATION SERVICES*/
    public void selectReminderLocation(View v){
        Log.i("Location Button", "Clicked");

        /*Location Services: Permission Check*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_RESULT_CODE);
        }

        /*addReminder onClick function*/
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try{
            Intent intent = builder.build(NewGeoFence.getActivity(this));
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            Log.d("placepicker:","myerror1");
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(data == null){
            Log.d("TAG", "DATA INTENT IS NULL");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PLACE_PICKER_REQUEST) {
            Place place = PlacePicker.getPlace(NewGeoFence.getActivity(this), data);
            placename = place.getName().toString();
            address = place.getAddress().toString();
            pname.setText(place.getName());
            //padd.setText(place.getAddress());
            longtitude = place.getLatLng().longitude;
            latitude = place.getLatLng().latitude;
            //Log.d("VERBOSE", String.valueOf(longtitude)+" is long, "+String.valueOf(latitude)+" is the lat");
        }
        Log.i("NAME", placename);
        Log.i("ADDRESS", address);
    }

    // On clicking repeat type button
//    public void selectRepeatType(View v) {
//        final String[] items = new String[5];
//
//        items[0] = "Minute";
//        items[1] = "Hour";
//        items[2] = "Day";
//        items[3] = "Week";
//        items[4] = "Month";
//
//        // Create List Dialog
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Select Type");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int item) {
//
//                mRepeatType = items[item];
//                mRepeatTypeText.setText(mRepeatType);
//                mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }


    // On clicking repeat interval button
//    public void setRepeatNo(View v) {
//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Enter Number");
//
//        // Create EditText box to input repeat number
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_NUMBER);
//        alert.setView(input);
//        alert.setPositiveButton("Ok",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        if (input.getText().toString().length() == 0) {
//                            mRepeatNo = Integer.toString(1);
//                            mRepeatNoText.setText(mRepeatNo);
//                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
//                        } else {
//                            mRepeatNo = input.getText().toString().trim();
//                            mRepeatNoText.setText(mRepeatNo);
//                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
//                        }
//                    }
//                });
//        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // do nothing
//            }
//        });
//        alert.show();
//    }

    // On clicking the save button
    public void saveReminder() {
        ReminderDatabase rb = new ReminderDatabase(this);
        //Log.i("PLACENAME", placename);
        // Creating Reminder
        DecimalFormat df = new DecimalFormat("#.##");
        int ID = rb.addReminder(new Reminder(mTitle, mDate, mTime, mRepeat, mRepeatNo, mRepeatType, mActive, mPriority, mType, mCreateDate, placename, address, df.format(longtitude), df.format(latitude)));

        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Check repeat type
        if (mRepeatType.equals("Minute")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
        } else if (mRepeatType.equals("Hour")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals("Day")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals("Week")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if (mRepeatType.equals("Month")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }

        // Create a new notification
        if (mActive.equals("true")) {
            if (mRepeat.equals("true")) {
                new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendar, ID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new AlarmReceiver().setAlarm(getApplicationContext(), mCalendar, ID);
            }
        }

        // Create snackbar to confirm new reminder

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", "Saved");
        setResult(RESULT_OK, returnIntent);
        onBackPressed();
    }

    public void hideKeyboard(View view){
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // On clicking the back arrow
            // Discard any changes
            case android.R.id.home:
                onBackPressed();
                return true;

            // On clicking save reminder button
            // Update reminder
            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if (mTitleText.getText().toString().length() == 0)
                    mTitleText.setError("Reminder Title cannot be blank!");

                else {
                    saveReminder();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.discard_reminder:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Discarded");
                setResult(RESULT_OK, returnIntent);
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}