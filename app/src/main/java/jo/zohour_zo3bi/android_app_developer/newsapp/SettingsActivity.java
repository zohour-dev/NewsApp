package jo.zohour_zo3bi.android_app_developer.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.content.SharedPreferences;
import android.os.Bundle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.settings_container, new NewsPreferenceFragment()).
                commit();
    }//end onCreate()

    public static class NewsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Section preference
            Preference section = findPreference(getString(R.string.settings_section_key));
            bindPreferenceSummaryToValue(section);

            // Order-by preference
            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);
        }//end onCreatePreferences()

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringNewValue = newValue.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringNewValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringNewValue);
            }
            return true;
        }//end onPreferenceChange()

        /**
         * Update the Preference summary with the value of the selected option
         *
         * @param preference The specified preference
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String stringPreference = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, stringPreference);
        }//end bindPreferenceSummaryToValue()
    }//end NewsPreferenceFragment class
}//end SettingsActivity class
