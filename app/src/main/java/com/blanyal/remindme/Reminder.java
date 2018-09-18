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

import java.util.ArrayDeque;

// Reminder class
public class Reminder {
    private int mID;
    private String mTitle;
    private String mDate;
    private String mTime;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private String mPriority;
    private String mCreateDate;
    private String mType;
    //location services
    private String placeName;
    private String placeAddress;
    private String latitude;
    private String longitude;


    public Reminder(int ID, String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String Priority, String Type, String CreateDate, String Placename, String Address, String Latitude, String Longitude) {
        mID = ID;
        mTitle = Title;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mActive = Active;
        mPriority = Priority;
        mType = Type;
        mCreateDate = CreateDate;
        placeName = Placename;
        placeAddress = Address;
        latitude = Latitude;
        longitude = Longitude;
    }

    public Reminder(String Title, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String Priority, String Type, String CreateDate, String PlaceName, String Address, String Latitude, String Longitude) {
        mTitle = Title;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mActive = Active;
        mPriority = Priority;
        mType = Type;
        mCreateDate = CreateDate;
        placeName = PlaceName;
        placeAddress = Address;
        latitude = Latitude;
        longitude = Longitude;
    }

    public Reminder() {
    }

    public int getID() {
        return mID;
    }

    public void setID(int ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getEventDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getEventTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(String repeatType) {
        mRepeatType = repeatType;
    }

    public String getRepeatNo() {
        return mRepeatNo;
    }

    public void setRepeatNo(String repeatNo) {
        mRepeatNo = repeatNo;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String repeat) {
        mRepeat = repeat;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String active) {
        mActive = active;
    }

    public String getPriority() {
        return mPriority;
    }

    public void setPriority(String priority) {
        mPriority = priority;
    }

    public String getCreateDate() {
        return mCreateDate;
    }

    public void setCreateDate(String createDate) {
        mCreateDate = createDate;
    }

    public String getType() { return mType; }

    public void setType(String selectedType) { mType = selectedType;}

    public String getName() { return placeName;}
    public String getAddress() { return placeAddress; }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public void setName(String name) {placeName = name;};
    public void setAddress(String newAddress) { placeAddress = newAddress; }
    public void setLatitude(String newLat) { latitude = newLat; }
    public void setLongitude(String newLong) { longitude = newLong;}
}
