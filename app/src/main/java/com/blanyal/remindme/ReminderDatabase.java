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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReminderDatabase extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ReminderDatabase";

    // Table name
    private static final String TABLE_REMINDERS = "ReminderTable";

    // Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_TIME = "time";
    private static final String KEY_REPEAT = "repeat";
    private static final String KEY_REPEAT_NO = "repeat_no";
    private static final String KEY_REPEAT_TYPE = "repeat_type";
    private static final String KEY_ACTIVE = "active";
    private static final String KEY_PRIORITY = "priority_key";
    private static final String KEY_TYPE = "type_key";
    private static final String KEY_CREATEDATE = "create_date";
    //LOCATION SERVICES
    private static final String KEY_PLACENAME = "place_name";
    private static final String KEY_ADDRESS = "place_address";
    private static final String KEY_LATITUDE = "place_latitude";
    private static final String KEY_LONGITUDE = "place_longitude";

    public ReminderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_REMINDERS_TABLE = "CREATE TABLE " + TABLE_REMINDERS +
                "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_TIME + " INTEGER,"
                + KEY_REPEAT + " BOOLEAN,"
                + KEY_REPEAT_NO + " INTEGER,"
                + KEY_REPEAT_TYPE + " TEXT,"
                + KEY_ACTIVE + " BOOLEAN,"
                + KEY_PRIORITY + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_CREATEDATE + " TEXT,"
                + KEY_PLACENAME + " TEXT,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_LATITUDE + " REAL,"
                + KEY_LONGITUDE + " REAL"
                + ")";

        db.execSQL(CREATE_REMINDERS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        if (oldVersion >= newVersion)
            return;
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);

        // Create tables again
        onCreate(db);
    }

    // Adding new Reminder
    public int addReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, reminder.getTitle());
        values.put(KEY_DATE, reminder.getEventDate());
        values.put(KEY_TIME, reminder.getEventTime());
        values.put(KEY_REPEAT, reminder.getRepeat());
        values.put(KEY_REPEAT_NO, reminder.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getRepeatType());
        values.put(KEY_ACTIVE, reminder.getActive());
        values.put(KEY_PRIORITY, reminder.getPriority());
        values.put(KEY_TYPE, reminder.getType());
        values.put(KEY_CREATEDATE, reminder.getCreateDate());
        //LOCATION
        values.put(KEY_PLACENAME, reminder.getName());
        values.put(KEY_ADDRESS, reminder.getAddress());
        values.put(KEY_LATITUDE, reminder.getLatitude());
        values.put(KEY_LONGITUDE, reminder.getLongitude());

        // Inserting Row
        long ID = db.insert(TABLE_REMINDERS, null, values);
        db.close();
        return (int) ID;
    }

    // Getting single Reminder
    public Reminder getReminder(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_REMINDERS, new String[]
                        {
                                KEY_ID,
                                KEY_TITLE,
                                KEY_DATE,
                                KEY_TIME,
                                KEY_REPEAT,
                                KEY_REPEAT_NO,
                                KEY_REPEAT_TYPE,
                                KEY_ACTIVE,
                                KEY_PRIORITY,
                                KEY_TYPE,
                                KEY_CREATEDATE,
                                KEY_PLACENAME,
                                KEY_ADDRESS,
                                KEY_LATITUDE,
                                KEY_LONGITUDE
                        }, KEY_ID + "=?",

                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Reminder reminder = new Reminder(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                cursor.getString(2), cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10), cursor.getString(11), cursor.getString(12), cursor.getString(13), cursor.getString(14));

        return reminder;
    }

    // Getting all Reminders
    public List<Reminder> getAllReminders() {
        List<Reminder> reminderList = new ArrayList<>();

        // Select all Query
        String selectQuery = "SELECT * FROM " + TABLE_REMINDERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Reminder reminder = new Reminder();
                reminder.setID(Integer.parseInt(cursor.getString(0)));
                reminder.setTitle(cursor.getString(1));
                reminder.setDate(cursor.getString(2));
                reminder.setTime(cursor.getString(3));
                reminder.setRepeat(cursor.getString(4));
                reminder.setRepeatNo(cursor.getString(5));
                reminder.setRepeatType(cursor.getString(6));
                reminder.setActive(cursor.getString(7));
                reminder.setPriority(cursor.getString(8));
                reminder.setType(cursor.getString(9));
                reminder.setCreateDate(cursor.getString(10));
                //LOCATION
                reminder.setName(cursor.getString(11));
                reminder.setAddress(cursor.getString(12));
                reminder.setLatitude(cursor.getString(13));
                reminder.setLongitude(cursor.getString(14));

                // Adding Reminders to list
                reminderList.add(reminder);
            } while (cursor.moveToNext());
        }
        return reminderList;
    }

    // Getting Reminders Count
    public int getRemindersCount() {
        String countQuery = "SELECT * FROM " + TABLE_REMINDERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int number = cursor.getCount();
        cursor.close();
        return number;
        //return cursor.getCount();
    }

    // Getting the number of active reminders for the day
    public int getActiveReminders() {
        DateFormat df = new SimpleDateFormat("d/M/yyyy");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);

        String countQuery = "SELECT * FROM ReminderTable WHERE date='" + reportDate + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int number = cursor.getCount();
        cursor.close();

        return number;
    }

    public String getNames() {

        DateFormat df = new SimpleDateFormat("d/M/yyyy");
        Date today = Calendar.getInstance().getTime();
        String reportDate = df.format(today);

        String TAG = "DbHelper";
        Log.d(TAG, "findDates called");
        SQLiteDatabase db = this.getReadableDatabase();
        String tableName = "ReminderTable";

        String tableString = "\n";
        Cursor allRows = db.rawQuery("SELECT title FROM ReminderTable WHERE date='" + reportDate + "'", null);
        if (allRows.moveToFirst()) {
            String[] columnNames = allRows.getColumnNames();
            do {
                for (String name : columnNames) {
                    tableString += String.format("* %s",
                            allRows.getString(allRows.getColumnIndex(name)));
                }
                tableString += "\n";

            } while (allRows.moveToNext());
        }
        //System.out.println(tableString);
        return tableString;
    }

    // Updating single Reminder
    public int updateReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, reminder.getTitle());
        values.put(KEY_DATE, reminder.getEventDate());
        values.put(KEY_TIME, reminder.getEventTime());
        values.put(KEY_REPEAT, reminder.getRepeat());
        values.put(KEY_REPEAT_NO, reminder.getRepeatNo());
        values.put(KEY_REPEAT_TYPE, reminder.getRepeatType());
        values.put(KEY_ACTIVE, reminder.getActive());
        values.put(KEY_PRIORITY, reminder.getPriority());
        values.put(KEY_TYPE, reminder.getType());
        values.put(KEY_CREATEDATE,reminder.getCreateDate());
        //LOCATION
        values.put(KEY_PLACENAME, reminder.getName());
        values.put(KEY_ADDRESS, reminder.getAddress());
        values.put(KEY_LATITUDE, reminder.getLatitude());
        values.put(KEY_LONGITUDE, reminder.getLongitude());

        // Updating row
        return db.update(TABLE_REMINDERS, values, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getID())});
    }

    // Deleting single Reminder
    public void deleteReminder(Reminder reminder) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDERS, KEY_ID + "=?",
                new String[]{String.valueOf(reminder.getID())});
        db.close();
    }
}
