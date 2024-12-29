package com.google.android.settings.communal;

import android.content.Context;
import android.content.IntentFilter;
import android.provider.Settings;
import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;

public class HubModeContentPreferenceController extends TogglePreferenceController {
    public static final String GLANCEABLE_HUB_CONTENT_SETTING = "glanceable_hub_content_setting";

    public HubModeContentPreferenceController(Context context, String str) {
        super(context, str);
    }

    public int getAvailabilityStatus() {
        return AVAILABLE;
    }

    public Class getBackgroundWorkerClass() {
        return super.getBackgroundWorkerClass();
    }

    int getDefaultWidgetCategories() {
        return 2;
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
        if ((Settings.Secure.getInt(mContext.getContentResolver(), GLANCEABLE_HUB_CONTENT_SETTING, getDefaultWidgetCategories()) & 1) == 0) {
            z = false;
        }
        return z;
    }

    public boolean setChecked(boolean z) {
        Settings.Secure.putInt(mContext.getContentResolver(), GLANCEABLE_HUB_CONTENT_SETTING, (z ? 1 : 0) | 2);
        return true;
    }

    public boolean useDynamicSliceSummary() {
        return super.useDynamicSliceSummary();
    }
}
