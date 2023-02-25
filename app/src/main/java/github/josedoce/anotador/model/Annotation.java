package github.josedoce.anotador.model;

import android.database.Cursor;

import java.util.List;

import github.josedoce.anotador.annotations.Enc;

public class Annotation {
    private Integer id;
    @Enc
    private String title;
    @Enc
    private String description;
    @Enc
    private String date;
    @Enc
    private String hour;

    private List<Field> fieldList = null;

    public Annotation() {}

    public Annotation(Cursor cursor) {
        int cId = cursor.getColumnIndex("id");
        int cTitle = cursor.getColumnIndex("title");
        int cDescription = cursor.getColumnIndex("description");
        int cEmail = cursor.getColumnIndex("email");
        int cPassword = cursor.getColumnIndex("password");
        int cUrl = cursor.getColumnIndex("url");
        int cDate = cursor.getColumnIndex("date");
        int cFieldType = cursor.getColumnIndex("fieldType");
        setId(cursor.getInt(cId));
        setTitle(cursor.getString(cTitle));
        setDescription(cursor.getString(cDescription));
        //setEmail(cursor.getString(cEmail));
        //setPassword(cursor.getString(cPassword));
        //setUrl(cursor.getString(cUrl));
        String[] vDate = cursor.getString(cDate).split(",");
        setDate(vDate[0]);
        setHour(vDate[1]);
        //setFieldType(cursor.getString(cFieldType));
    }

    public Annotation(String title, String description, String date, String hour) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.hour = hour;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public List<Field> getFieldList() {
        return fieldList;
    }

    public void setFieldList(List<Field> fieldList) {
        this.fieldList = fieldList;
    }

}
