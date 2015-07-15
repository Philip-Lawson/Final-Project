/**
 * 
 */
package com.example.appproofofconcept;

import com.example.testingagainnnn.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;

/**
 * @author Phil
 *
 */
public class SettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings_preferences);
		
		
		final CheckBoxPreference anonymousPreference = (CheckBoxPreference) findPreference(getString(R.string.anonymous_key));
		final EditTextPreference emailAddressPreference = (EditTextPreference) findPreference(getString(R.string.email_key));

		anonymousPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference pref,
							Object newValue) {
						boolean anonymous = (Boolean) newValue;

						if (!anonymous) {
							emailAddressPreference.setEnabled(true);
						} else {
							emailAddressPreference.setText("");
							emailAddressPreference.setEnabled(false);
						}
						return true;
					}
				});
		
		emailAddressPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference pref, Object newValue){
				
				String emailAddress = (String) newValue;
				
				if(emailAddress.equals("")){
					return true;
				} else if (EmailValidationStrategy.emailIsValid(emailAddress)){
					return true;
				} else {
					// notify the user of an invalid email address					
					FragmentTransaction fragment = getFragmentManager().beginTransaction();
					DialogFragment invalidEmailDialog = new InvalidEmailDialogFragment();
					invalidEmailDialog.show(fragment, "invalidEmail");
					return false;
				}				
			}
		});

		if (anonymousPreference.isChecked()) {
			emailAddressPreference.setText("");
			emailAddressPreference.setEnabled(false);
		}
	}
	
	class InvalidEmailDialogFragment extends DialogFragment {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(R.string.invalid_email_text);
			builder.setTitle(R.string.invalid_email_title);
			builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					dialog.cancel();
				}
			});
			
			return builder.create();
		}
		
	}
	
	
	
	

}
