package com.example.expensetracker.pojo;

import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.util.Objects;

public class SubCategory extends SettingsParent implements JsonIO {
    String name;
    int drawableId;

    public SubCategory() {
        this.name = "";
        this.drawableId = 0;
    }

    public SubCategory(String name, int drawableId) {
        this.name = name;
        this.drawableId = drawableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    @Override
    public void readFromJson(JsonReader reader) throws IOException {
        reader.beginObject();
        if (Objects.equals(reader.nextName(), "name")) {
            this.name = reader.nextString();
        }
        if (Objects.equals(reader.nextName(), "drawId")) {
            this.drawableId = reader.nextInt();
        }
        reader.endObject();
    }
    @Override
    public void writeToJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("name").value(this.name);
        writer.name("drawId").value(this.drawableId);
        writer.endObject();
    }

    @Override
    public SettingsType getType() {
        return SettingsType.SUBCATEGORY;
    }
}
