package github.josedoce.anotador.model;

import android.database.Cursor;

import github.josedoce.anotador.annotations.Enc;

public class Annotation {
    private Integer id;
    @Enc
    private String title;
    @Enc
    private String description;
    @Enc
    private String email;
    @Enc
    private String password;
    @Enc
    private String url;
    @Enc
    private String date;
    @Enc
    private String hour;

    public Annotation() {}

    public Annotation(Cursor cursor) {
        int cId = cursor.getColumnIndex("id");
        int cTitle = cursor.getColumnIndex("title");
        int cDescription = cursor.getColumnIndex("description");
        int cEmail = cursor.getColumnIndex("email");
        int cPassword = cursor.getColumnIndex("password");
        int cUrl = cursor.getColumnIndex("url");
        int cDate = cursor.getColumnIndex("date");
        setId(cursor.getInt(cId));
        setTitle(cursor.getString(cTitle));
        setDescription(cursor.getString(cDescription));
        setEmail(cursor.getString(cEmail));
        setPassword(cursor.getString(cPassword));
        setUrl(cursor.getString(cUrl));
        String[] vDate = cursor.getString(cDate).split(",");
        setDate(vDate[0]);
        setHour(vDate[1]);
    }

    public Annotation(String title, String description, String email, String password, String url, String date, String hour) {
        this.title = title;
        this.description = description;
        this.email = email;
        this.password = password;
        this.url = url;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
