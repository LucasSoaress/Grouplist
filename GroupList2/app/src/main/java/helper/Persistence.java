package helper;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class Persistence extends Application
{
    @Override
    public void onCreate()
    {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        super.onCreate();
    }
}
