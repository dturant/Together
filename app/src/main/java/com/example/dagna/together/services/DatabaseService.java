package com.example.dagna.together.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DatabaseService extends Service {
    public DatabaseService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
