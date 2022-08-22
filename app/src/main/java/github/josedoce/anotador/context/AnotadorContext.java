package github.josedoce.anotador.context;

import android.app.Application;

public class AnotadorContext extends Application {
    private String user;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
