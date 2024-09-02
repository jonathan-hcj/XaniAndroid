package com.xaniapp.xani.business;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Single;


public class DatastoreBusiness {

    public enum Key {
        USERNAME,
        HASH
    }

    private static final String STORE_NAME = "xani";
    private static final DatastoreBusiness instance = new DatastoreBusiness();
    private RxDataStore<Preferences> datastore;

    private DatastoreBusiness() { }

    public static DatastoreBusiness getInstance(Context context) {
        if (instance.datastore == null) {
            instance.datastore = new RxPreferenceDataStoreBuilder(context, STORE_NAME).build();
        }

        return instance;
    }

    private final Preferences pref_error = new Preferences() {
        @Nullable
        @Override
        public <T> T get(@NonNull Key<T> key) {
            return null;
        }

        @Override
        public <T> boolean contains(@NonNull Key<T> key) {
            return false;
        }

        @NonNull
        @Override
        public Map<Key<?>, Object> asMap() {
            return new HashMap<>();
        }
    };

    public boolean setStringValue(Key Key, String value){

        var PREF_KEY = PreferencesKeys.stringKey(Key.name());
        var updateResult =  datastore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(PREF_KEY, value);
            return Single.just(mutablePreferences);
        }).onErrorReturnItem(pref_error);

        return updateResult.blockingGet() != pref_error;
    }

    public @Nullable String getStringValue(Key Key) {

        String result = null;
        var PREF_KEY = PreferencesKeys.stringKey(Key.name());
        var value = datastore.data().firstOrError().map(prefs -> prefs.get(PREF_KEY)).onErrorReturnItem("null");
        var result1 = value.blockingGet();

        if (!result1.equals("null")) {
            result = result1;
        }

        return result;
    }

    public boolean setBoolValue(Key Key, boolean boolValue){
        return setStringValue(Key, boolValue ? "1" : "0");
    }

    boolean getBoolValue(Key Key) {
        return getStringValue(Key).equals("1");
    }
}
