package com.example.expensetracker.pojo;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;

public interface JsonIO {
    void readFromJson(JsonReader reader) throws IOException;
    void writeToJson(JsonWriter writer) throws IOException;
}
