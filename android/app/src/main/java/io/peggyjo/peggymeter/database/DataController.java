package io.peggyjo.peggymeter.database;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import io.peggyjo.peggymeter.model.MoodListener;
import io.peggyjo.peggymeter.model.SettingListener;

/**
 * Created by vyakunin on 3/4/18.
 */
public class DataController {
    private static final String TAG = "PeggiMeter.DataCtrl";

    private MoodAdapter moodAdapter;
    private SettingAdapter settingAdapter;
    private String uid;
    private final List<MoodListener> moodListeners;
    private final List<SettingListener> settingListeners;


    public DataController() {
        moodListeners = new ArrayList<>();
        settingListeners = new ArrayList<>();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {
            Log.i(TAG, "Logging user");
            firebaseAuth.signInAnonymously().addOnCompleteListener((x) -> initDB());
        } else {
            initDB();
            Log.i(TAG, "Logged as user " + user.getUid());
        }
    }

    private void initDB() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        uid = user.getUid();
        moodAdapter = new MoodAdapter(this);
        for (MoodListener listener : moodListeners) {
            moodAdapter.addListener(listener);
        }
        settingAdapter = new SettingAdapter(this);
        for (SettingListener listener : settingListeners) {
            settingAdapter.addListener(listener);
        }
    }

    public synchronized void addMoodListener(MoodListener listener) {
        if (moodAdapter != null) {
            moodAdapter.addListener(listener);
        } else {
            moodListeners.add(listener);
        }
    }

    public synchronized void addSettingListener(SettingListener listener) {
        if (settingAdapter != null) {
            settingAdapter.addListener(listener);
        } else {
            settingListeners.add(listener);
        }
    }

    DatabaseReference getReference() {
        return FirebaseDatabase.getInstance().getReference("users").child(uid);
    }

    public MoodAdapter getMoodAdapter() {
        return moodAdapter;
    }

    public SettingAdapter getSettingAdapter() {
        return settingAdapter;
    }
}