package com.example.expensetracker.pojo;

import android.util.JsonReader;
import android.util.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Category extends SettingsParent implements JsonIO {
    private String name;

    private  int id;
    private int colorId;
    final private ArrayList<SubCategory> subCategories;

    private String tableName = "category";

    public Category(int id, String name, int colorId, ArrayList<SubCategory> subCategories) {
        this.id = id;
        this.name = name;
        this.colorId = colorId;
        this.subCategories = subCategories;
    }

    public Category(int id, String name) {
        this.name = name;
        this.id = id;
        this.subCategories = new ArrayList<>();
    }

    public Category() {
        this.name = "";
        this.colorId = 0;
        this.subCategories = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColorId() {
        return colorId;
    }

    public void setColorId(int colorId) {
        this.colorId = colorId;
    }

    public ArrayList<SubCategory> getSubCategories() {
        return subCategories;
    }

    @Override
    public void readFromJson(JsonReader reader) throws IOException {
        reader.beginObject();
        this.subCategories.clear();
        if (Objects.equals(reader.nextName(), "name")) {
            this.name = reader.nextString();
        }
        if (Objects.equals(reader.nextName(), "colorId")) {
            this.colorId = reader.nextInt();
        }
        if(Objects.equals(reader.nextName(), "subcategories")) {
            reader.beginArray();
            while(reader.hasNext()) {
                SubCategory subCategory = new SubCategory();
                subCategory.readFromJson(reader);
                this.subCategories.add(subCategory);
            }
            reader.endArray();
        }
        reader.endObject();
    }
    @Override
    public void writeToJson(JsonWriter writer) throws IOException {
        writer.beginObject();
        writer.name("name").value(this.name);
        writer.name("colorId").value(this.colorId);
        writer.name("subcategories");
        writer.beginArray();
        for(SubCategory subCategory : subCategories) {
            subCategory.writeToJson(writer);
        }
        writer.endArray();
        writer.endObject();
    }

    @Override
    public SettingsType getType() {
        return SettingsType.CATEGORY;
    }
}
