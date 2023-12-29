package com.example.expensetracker;

import android.content.Context;
import android.database.Cursor;
import android.provider.Telephony;
import android.util.Log;

import com.example.expensetracker.pojo.UnconfirmedEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReader {

    public ArrayList<UnconfirmedEntry> readMessagesSentAfter(Context context, Date dateAfter) {
        Cursor cursor = context.getContentResolver().query(Telephony.Sms.CONTENT_URI, new String[]{Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.ADDRESS, Telephony.Sms.DATE}, Telephony.Sms.DATE+">="+dateAfter.getTime(), null, null);
        assert cursor != null;
        cursor.moveToFirst();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy hh:mm:ss", Locale.getDefault());
        ArrayList<UnconfirmedEntry> entries = new ArrayList<>();
        if(cursor.getCount() > 0) {
            do {
                String body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY));
                int value = extractValue(body);
                if(value != 0) {
                    String sender = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    Date date = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)));
                    entries.add(new UnconfirmedEntry(sender, body, date, value));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    private int extractValue(String body) {
        Matcher matcher = Pattern.compile("(?i)(acct|card)( *)(X+)([0-9]{3,4})").matcher(body);
        int value = 0;
        if(matcher.find()) {
            Matcher amountMatcher = Pattern.compile("(?i)(RS|INR)(.?)( ?)([0-9]+)(.[0-9]{2})").matcher(body);
            if(amountMatcher.find()) {
                String amt = amountMatcher.group()
                        .replaceFirst("(?i)(RS|INR)(.?+)", "")
                        .trim();
                value = Math.round(Float.parseFloat(amt));
            }
            if(Pattern.compile("(?i)(debited|spent)").matcher(body).find()) {
                value *= -1;
            } else if(!Pattern.compile("(?i)(credited|recived)").matcher(body).find()) {
                throw new RuntimeException("Unknown Transaction");
            }
        }
        return value;
    }
}
