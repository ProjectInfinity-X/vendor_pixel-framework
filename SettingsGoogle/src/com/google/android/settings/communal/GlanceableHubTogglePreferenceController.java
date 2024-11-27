package com.google.android.settings.communal;

import android.content.Context;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import androidx.preference.Preference;
import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.RestrictedLockUtils;
import com.android.settingslib.RestrictedLockUtilsInternal;
import com.android.settingslib.RestrictedSwitchPreference;
import com.android.settingslib.core.AbstractPreferenceController;

public class GlanceableHubTogglePreferenceController extends TogglePreferenceController {
    public GlanceableHubTogglePreferenceController(Context context, String str) {
        super(context, str);
    }

    private void disablePreferenceIfManaged(RestrictedSwitchPreference restrictedSwitchPreference) {
        RestrictedLockUtils.EnforcedAdmin checkIfKeyguardFeaturesDisabled = RestrictedLockUtilsInternal.checkIfKeyguardFeaturesDisabled(mContext, 1, UserHandle.myUserId());
        if (checkIfKeyguardFeaturesDisabled != null) {
            restrictedSwitchPreference.setDisabledByAdmin(checkIfKeyguardFeaturesDisabled);
            restrictedSwitchPreference.setChecked(false);
        }
    }

    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    public Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    public IntentFilter getIntentFilter() {
        return super.getIntentFilter();
    }

    public int getSliceHighlightMenuRes() {
        return R.string.menu_key_communal;
    }

    public boolean hasAsyncUpdate() {
        return super.hasAsyncUpdate();
    }

    public boolean isChecked() {
        boolean z = true;
        if (Settings.Secure.getInt(mContext.getContentResolver(), "glanceable_hub_enabled", 1) != 1) {
            z = false;
        }
        return z;
    }

    public boolean setChecked(boolean z) {
        Settings.Secure.putInt(mContext.getContentResolver(), "glanceable_hub_enabled", z ? 1 : 0);
        return true;
    }

    public void updateState(Preference preference) {
        super.updateState(preference);
        disablePreferenceIfManaged((RestrictedSwitchPreference) preference);
    }

    public boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
