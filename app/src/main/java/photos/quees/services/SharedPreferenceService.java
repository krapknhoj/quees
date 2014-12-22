package photos.quees.services;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceService {
    private String SHARED_PREF_NAME = "Quees";

    public SharedPreferenceService() {
    }

    public android.content.SharedPreferences getPref(Activity activity) {
        return activity.getSharedPreferences(SHARED_PREF_NAME, activity.MODE_PRIVATE);
    }

    public String getSharedPreferenceAsString(Activity activity, String key) {
        return getPref(activity).getString(key, null);
    }

    public Long getSharedPreferenceAsLong(Activity activity, String key) {
        return getPref(activity).getLong(key, new Long(0));
    }

    public Editor getEditor(Activity activity) {
        return getPref(activity).edit();
    }

    public void setSharedPreferenceAsString(Activity activity, String key, String value) {
        getEditor(activity).putString(key, value).commit();
    }

    public void setSharedPreferenceAsLong(Activity activity, String key, Long value) {
        getEditor(activity).putLong(key, value).commit();
    }

    public void clearSharedPreferences(Activity activity) {
        getEditor(activity).clear().commit();
    }
}
