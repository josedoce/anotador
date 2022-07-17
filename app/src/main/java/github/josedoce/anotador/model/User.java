package github.josedoce.anotador.model;

import android.database.Cursor;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class User {

    private String login;
    private String password;

    public User(){}

    public User(Cursor cursor){
        int column1 = cursor.getColumnIndex("login");
        int column2 = cursor.getColumnIndex("password");
        setLogin(cursor.getString(column1));
        setPassword(cursor.getString(column2));

    }

    public User(String login, String password){
        this.login = login;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean verify(String password){
        return BCrypt.verifyer()
                .verify(password.toCharArray(), this.getPassword())
                .verified;
    }
}
