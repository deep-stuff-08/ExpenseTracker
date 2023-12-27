package com.example.expensetracker;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

import java.util.Date;

public class SmsReader {
    public void readMessagesSentAfter(Context context, Date date) {
        Cursor cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, new String[]{Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.ADDRESS}, null, null, null);
        assert cursor != null;
        cursor.moveToFirst();
        if(cursor.isAfterLast()) {
            Log.d("DebugSms", "No Entries");
        } else {
            do {
                Log.d("DebugSms", " : " + cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)));
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
}
