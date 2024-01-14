package com.example.cryptotracker.Supports;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

import java.util.concurrent.Callable;

public class DataStoreSingleton {
    RxDataStore<Preferences> datastore;
    private static final DataStoreSingleton ourInstance = new DataStoreSingleton();

    public static DataStoreSingleton getInstance() {
        return ourInstance;
    }

    private DataStoreSingleton() { }

    public void setDataStore(RxDataStore<Preferences> datastore) {
        this.datastore = datastore;
    }

    public RxDataStore<Preferences> getDataStore() {
        return datastore;
    }

    public void clearDataStore() {
        datastore.updateDataAsync(preferences -> {
            MutablePreferences mutablePreferences = preferences.toMutablePreferences();
            mutablePreferences.clear();
            return Single.just(mutablePreferences);
        }).subscribe();
    }
}