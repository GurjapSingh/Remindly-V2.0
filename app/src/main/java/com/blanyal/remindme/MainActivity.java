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
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.renderscript.RenderScript;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import android.widget.Toast;


import android.content.SharedPreferences.Editor;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mList;
    private SimpleAdapter mAdapter;
    private Toolbar mToolbar;
    private TextView mNoReminderView;
    private FloatingActionButton mAddReminderButton;
    private int mTempPost;
    private LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    private ReminderDatabase rb;
    private MultiSelector mMultiSelector = new MultiSelector();
    private AlarmReceiver mAlarmReceiver;
    private ImageButton mSettingButton;
    private ImageButton mSortButton;
    private int snackBarColor = Color.parseColor("#00BCD4");
    //LOCATION SERVICES
    public static final String LOCATION_FENCE_KEY = "LocationFenceKey";
    private LocationManager locationManager;
    private LocationListener locationListener;
    public static Double latit;
    public static Double longit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize reminder database
        rb = new ReminderDatabase(getApplicationContext());

        // Initialize views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mAddReminderButton = (FloatingActionButton) findViewById(R.id.add_reminder);
        mList = (RecyclerView) findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);
        mSettingButton = (ImageButton) findViewById(R.id.settings);
        mSortButton = (ImageButton) findViewById(R.id.sorting);

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

        // Create recycler view
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new SimpleAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);

        // Setup toolbar
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.app_name);

        // On clicking the floating action button (ADD REMINDER BUTTON)
        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });


        // On clicking the settings action button
        mSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        // On clicking the sort reminders button
        mSortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences myPrefs = getSharedPreferences("sortSelection", MODE_PRIVATE);

                PopupMenu dropDownMenu = new PopupMenu(getApplicationContext(), mSortButton);

                dropDownMenu.getMenuInflater().inflate(R.menu.drop_down_menu, dropDownMenu.getMenu());

                for (int i = 0; i < 6; i++) {
                    if (dropDownMenu.getMenu().getItem(i).getTitle().equals(myPrefs.getAll().get("sortSelection"))) {
                        dropDownMenu.getMenu().getItem(i).setChecked(true);
                        break;
                    }
                }
                dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(getApplicationContext(), "Sorting by: " + menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        menuItem.setChecked(true);
                        //Get myPrefs to get saved shared Preferences
                        SharedPreferences myPrefs = getSharedPreferences("sortSelection", MODE_PRIVATE);

                        //Use editor to put the new selection in preferences
                        Editor editor = myPrefs.edit();
                        editor.putString("sortSelection", (String) menuItem.getTitle());
                        editor.commit();

                        recreate();
                        return true;
                    }
                });
                dropDownMenu.show();
            }
        });

        // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();

        //Set up daily notification alarm
        alarmMethod();

        //LOCATION SERVICES
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("APPEND", "LAT AND LONG ARE: " + location.getLatitude() + " and " + location.getLongitude());
                latit = location.getLatitude();
                longit = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
            return;
        } else {
            configure();
        }
    }


    public static String getLat() {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(latit);
    }

    public static String getLong() {
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(longit);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configure();
                return;
        }
    }

    public void configure() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }

        locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);
    }

    private void alarmMethod() {
        int reminderNumber = rb.getActiveReminders();
        if (reminderNumber > 0) {
            String namevalue = Integer.toString(reminderNumber);
            String daily = rb.getNames();

            long t = System.currentTimeMillis();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 10);
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 00);
            if (t <= calendar.getTimeInMillis()) {
                Intent myIntent = new Intent(MainActivity.this, NotifyService.class);
                myIntent.putExtra("ID", namevalue);
                myIntent.putExtra("DAILY", daily);
                myIntent.setAction("NOTE");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager am = (AlarmManager) MainActivity.this.getSystemService(MainActivity.this.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

            //Toast.makeText(this, "Start Alarm: " + namevalue + ".", Toast.LENGTH_SHORT).show();
        }
    }


    // Create context menu for long press actions
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.delete_reminder_new, menu);
    }

    // Multi select items in recycler view
    private android.support.v7.view.ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.delete_reminder_new, menu);
            return true;
        }
        // this function executes when the back button in the navigation bar is pressed
        public void onDestroyActionMode(android.support.v7.view.ActionMode actionMode ) {
            mMultiSelector.clearSelections();
            mMultiSelector.setSelectable(false);
        }
        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                // On clicking discard reminders
                case R.id.delete_selected_reminder:

                    // Get the reminder id associated with the recycler view item
                    for (int i = IDmap.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            int id = IDmap.get(i);

                            // Get reminder from reminder database using id
                            Reminder temp = rb.getReminder(id);
                            // Delete reminder
                            rb.deleteReminder(temp);
                            // Remove reminder from recycler view
                            mAdapter.removeItemSelected(i);
                            // Delete reminder alarm
                            mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
                        }
                    }

                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    // Recreate the recycler items
                    // This is done to remap the item and reminder ids
                    mAdapter.onDeleteItem(getDefaultItemCount());

                    // Display snackbar to confirm delete
                    Snackbar snackbarIn = Snackbar.make(findViewById(R.id.mainActivityView), "Deleted", Snackbar.LENGTH_LONG);
                    View viewIn = snackbarIn.getView();
                    viewIn.setBackgroundColor(snackBarColor);
                    snackbarIn.show();

                    // To check is there are saved reminders
                    // If there are no reminders display a message asking the user to create reminders
                    List<Reminder> mTest = rb.getAllReminders();

                    if (mTest.isEmpty()) {
                        mNoReminderView.setVisibility(View.VISIBLE);
                    } else {
                        mNoReminderView.setVisibility(View.GONE);
                    }

                    // Close the context menu
                    actionMode.finish();
                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    return true;

                // On clicking save reminders
                /*case R.id.save_reminder:
                    // Close the context menu
                    actionMode.finish();
                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    return true;*/

                default:
                    break;
            }
            return false;
        }
    };

    // On clicking a reminder item
    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.setItemCount(getDefaultItemCount());
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                Snackbar snackbarIn = Snackbar.make(findViewById(R.id.mainActivityView), result, Snackbar.LENGTH_LONG);
                View viewIn = snackbarIn.getView();
                viewIn.setBackgroundColor(snackBarColor);
                snackbarIn.show();
            }
        }
    }

    // Recreate recycler view
    // This is done so that newly created reminders are displayed
    @Override
    public void onResume() {
        super.onResume();

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }

        mAdapter.setItemCount(getDefaultItemCount());
    }

    // Layout manager for recycler view
    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    protected int getDefaultItemCount() {
        return 100;
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main_menu, menu);
        return false;
    }

    // Setup menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // start licenses activity
            case R.id.action_licenses:
                Intent intent = new Intent(this, LicencesActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Adapter class for recycler view
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
        private ArrayList<ReminderItem> mItems;

        public SimpleAdapter() {
            mItems = new ArrayList<>();
        }

        public void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
            notifyDataSetChanged();
        }

        public void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
        }

        public void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        // View holder for recycler view items
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.recycle_items, container, false);

            return new VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            ReminderItem item = mItems.get(position);
            itemHolder.setReminderTitle(item.mTitle, item.mPriority);
            itemHolder.setReminderDateTime(item.mDateTime);
            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
            itemHolder.setActiveImage(item.mActive);
            itemHolder.setType(item.mType);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        // Class for recycler view items
        public class ReminderItem {
            public String mTitle;
            public String mDateTime;
            public String mRepeat;
            public String mRepeatNo;
            public String mRepeatType;
            public String mActive;
            public String mType;
            public String mPriority;

            public ReminderItem(String Title, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active, String Type, String Priority) {
                this.mTitle = Title;
                this.mDateTime = DateTime;
                this.mRepeat = Repeat;
                this.mRepeatNo = RepeatNo;
                this.mRepeatType = RepeatType;
                this.mActive = Active;
                this.mType = Type;
                this.mPriority = new Integer(Priority).toString();
            }
        }

        // Class to compare date and time so that items are sorted in ascending order
        public class DateTimeComparator implements Comparator {
            DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");

            public int compare(Object a, Object b) {
                String o1 = ((DateTimeSorter) a).getDateTime();
                String o2 = ((DateTimeSorter) b).getDateTime();

                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        // UI and data class for recycler view items
        public class VerticalItemHolder extends SwappingHolder implements View.OnClickListener, View.OnLongClickListener {
            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText, mTypeText;
            private ImageView mActiveImage, mThumbnailImage;
            private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
            private TextDrawable mDrawableBuilder;
            private SimpleAdapter mAdapter;

            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
                super(itemView, mMultiSelector);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                // Initialize adapter for the items
                mAdapter = adapter;

                // Initialize views
                mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
                mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
                mTypeText = (TextView) itemView.findViewById(R.id.recycle_reminder_type);
            }

            // On clicking a reminder item
            @Override
            public void onClick(View v) {
                if(mMultiSelector.isSelectable()){
                    if(mMultiSelector.isSelected(this.getAdapterPosition(),this.getItemId())){
                        mMultiSelector.setSelected(this,false);
                    }
                    else {
                        mMultiSelector.setSelected(this,true);
                    }
                }
                else if (!mMultiSelector.tapSelection(this)) {
                    mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = IDmap.get(mTempPost);
                    selectReminder(mReminderClickID);

                } else if (mMultiSelector.getSelectedPositions().isEmpty()) {
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            // On long press enter action mode with context menu
            @Override
            public boolean onLongClick(View v) {
                // Clear selected items in recycler view
//                mMultiSelector.clearSelections();
                if (!mMultiSelector.isSelectable()) {
                    mMultiSelector.setSelectable(true);
                    AppCompatActivity activity = MainActivity.this;
                    activity.startSupportActionMode(mDeleteMode);
                    mMultiSelector.setSelected(this, true);
                }
                /*else {
                    mMultiSelector.setSelectable(false);
                    mMultiSelector.clearSelections();
                }*/

                return true;
            }

            // Set reminder title view
            public void setReminderTitle(String title, String priorityNum) {
                mTitleText.setText(title);
                String letter = "A";

                if (title != null && !title.isEmpty()) {
                    letter = priorityNum;
                }

                int color = mColorGenerator.getRandomColor();

                // Create a circular icon consisting of  a random background colour and first letter of title
                mDrawableBuilder = TextDrawable.builder().buildRound(letter, color);
                mThumbnailImage.setImageDrawable(mDrawableBuilder);
            }

            // Set date and time views
            public void setReminderDateTime(String datetime) {
                mDateAndTimeText.setText(datetime);
            }

            // Set repeat views
            public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if (repeat.equals("true")) {
                    mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType);
                } else if (repeat.equals("false")) {
                    mRepeatInfoText.setText("Repeat Off");
                }
            }

            // Set active image as on or off
            public void setActiveImage(String active) {
                if (active.equals("true")) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_on_white_24dp);
                } else if (active.equals("false")) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
                }
            }

            public void setType(String Type) {
                mTypeText.setText("Type: " + Type);
            }
        }

        // Generate random test data
        public ReminderItem generateDummyData() {
            return new ReminderItem("1", "2", "3", "4", "5", "6", "7", "8");
        }

        public Date strToDate(String strDate) {

            try {
                Date myDate;
                DateFormat thisDate = new SimpleDateFormat("dd/MM/yyyy hh:mm");
                myDate = thisDate.parse(strDate);
                return myDate;
            } catch (ParseException e) {
                System.out.println(e);
            }
            return new Date();
        }

        // sort the items in the list
        public void sortList(List<String> toSort, List<Integer> IDList, String sortBy) {
            int size = toSort.size();

            // sort by priority
            if (sortBy.equals("priorityLevel")) {
                for (int i = 0; i < size - 1; i++) {
                    for (int j = i + 1; j < size; j++) {
                        if (Integer.parseInt(toSort.get(i)) < Integer.parseInt(toSort.get(j))) {
                            // Swap the string in toSort
                            String sTemp = toSort.get(i);
                            toSort.set(i, toSort.get(j));
                            toSort.set(j, sTemp);

                            // Swap the ID in IDList
                            Integer iTemp = IDList.get(i);
                            IDList.set(i, IDList.get(j));
                            IDList.set(j, iTemp);
                        }
                    }
                }
            }
            // sort by when reminders are created or in the order that they will be executed
            else if (sortBy.equals("dateTimeOfCreation") || sortBy.equals("dateTimeOfEvent")) {
                // Use Selection sort to sort the List, swapping both the toSort List indexes, their related IDs and the dates in the dateList

                List<Date> dateList = new ArrayList<>();
                for (String e : toSort) {
                    dateList.add(strToDate(e));
                }

                for (int i = 0; i < size - 1; i++) {
                    for (int j = i + 1; j < size; j++) {
                        if (dateList.get(i).after(dateList.get(j))) {
                            String sTemp = toSort.get(i);
                            toSort.set(i, toSort.get(j));
                            toSort.set(j, sTemp);


                            // Swap the ID in IDList
                            Integer iTemp = IDList.get(i);
                            IDList.set(i, IDList.get(j));
                            IDList.set(j, iTemp);

                            // Swap dates in dateList
                            Date dTemp = dateList.get(i);
                            dateList.set(i, dateList.get(j));
                            dateList.set(j, dTemp);
                        }
                    }
                }
            } else {
                for (int i = 0; i < size - 1; i++) {
                    for (int j = i + 1; j < size; j++) {
                        if (toSort.get(i).compareTo(toSort.get(j)) > 0) {
                            // Swap the string in toSort
                            String sTemp = toSort.get(i);
                            toSort.set(i, toSort.get(j));
                            toSort.set(j, sTemp);

                            // Swap the ID in IDList
                            Integer iTemp = IDList.get(i);
                            IDList.set(i, IDList.get(j));
                            IDList.set(j, iTemp);
                        }
                    }
                }
//                Toast.makeText(getApplicationContext(), "This sorting type not implemented yet", Toast.LENGTH_LONG).show();
            }

        }

        public int findIndex(List<Integer> searchList, Integer key) {
            int size = searchList.size();

            for (int i = 0; i < size; i++) {
                if ((searchList.get(i)).equals(key))
                    return i;
            }

            return -1;
        }

        public ArrayList<SimpleAdapter.ReminderItem> sortByList(List<String> sortedList, List<String> Titles, List<String> DateAndTime,
                                                                List<String> Repeats, List<String> RepeatNos, List<String> RepeatTypes,
                                                                List<String> Actives, List<Integer> IDList, List<String> Types, String sortBy, List<String> Priority) {
            // items List to contain all items once sorted on preferred characteristic
            ArrayList<SimpleAdapter.ReminderItem> items = new ArrayList<>();
            List<Integer> originalIDList = new ArrayList<>(IDList);
            int size = sortedList.size();

            //Sort the List specified to be sorted on (first param)
            sortList(sortedList, IDList, sortBy);

            // Add data to each recycler view item
            for (int i = 0; i < size; i++) {
                int index = findIndex(originalIDList, IDList.get(i));

                items.add(new SimpleAdapter.ReminderItem(Titles.get(index), DateAndTime.get(index), Repeats.get(index),
                        RepeatNos.get(index), RepeatTypes.get(index), Actives.get(index), Types.get(index), Priority.get(index)));
                IDmap.put(i, originalIDList.get(index));
            }

            return items;
        }

        // Generate real data for each item
        public List<ReminderItem> generateData(int count) {
            SharedPreferences myPrefs = getSharedPreferences("sortSelection", MODE_PRIVATE);

            // Get all reminders from the database
            List<Reminder> reminders = rb.getAllReminders();

            // Initialize lists
            List<String> Titles = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> dateTimeOfEvent = new ArrayList<>();
            List<Integer> IDList = new ArrayList<>();
            List<String> dateTimeOfCreation = new ArrayList<>();
            List<String> Priority = new ArrayList<>();
            List<String> Type = new ArrayList<>();

            // Add details of all reminders in their respective lists
            for (Reminder r : reminders) {
                Titles.add(r.getTitle());
                dateTimeOfEvent.add(r.getEventDate() + " " + r.getEventTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatNo());
                RepeatTypes.add(r.getRepeatType());
                Actives.add(r.getActive());
                IDList.add(r.getID());
                dateTimeOfCreation.add(r.getCreateDate());
                Priority.add(r.getPriority());
                Type.add(r.getType());
            }

            String sortSelection = myPrefs.getString("sortSelection", "uninitialized");

            // Determine what to sort on based on sortSelection
            // The first param is the list to sort on, the rest are copies of the other attributes which need to be added
            if (sortSelection.equals("Alphabetically"))
                return (sortByList(Titles, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "alphabet",new ArrayList<>(Priority)));
            else if (sortSelection.equals("Date of Creation"))
                return (sortByList(dateTimeOfCreation, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "dateTimeOfCreation",new ArrayList<>(Priority)));
            else if (sortSelection.equals("Date of Event"))
                return (sortByList(dateTimeOfEvent, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "dateTimeOfEvent",new ArrayList<>(Priority)));
            else if (sortSelection.equals("Priority Level"))
                return (sortByList(Priority, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "priorityLevel",new ArrayList<>(Priority)));
            else if (sortSelection.equals("Alarm Status"))
                return (sortByList(Actives, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "alarmStatus",new ArrayList<>(Priority)));
            else if (sortSelection.equals("Type"))
                return (sortByList(Type, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "type",new ArrayList<>(Priority)));
                // no sorting method was found (usually happens the first time you install the app), sort by date of creation (original method) by default
            else if (sortSelection.equals("uninitialized")) {
                return (sortByList(dateTimeOfCreation, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "dateTimeOfCreation",new ArrayList<>(Priority)));
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong here", Toast.LENGTH_LONG).show();
                return (sortByList(dateTimeOfCreation, new ArrayList<>(Titles), new ArrayList<>(dateTimeOfEvent), new ArrayList<>(Repeats), new ArrayList<>(RepeatNos), new ArrayList<>(RepeatTypes), new ArrayList<>(Actives), new ArrayList<>(IDList), new ArrayList<>(Type), "dateTimeOfCreation",new ArrayList<>(Priority)));

            }
        }
    }
}
