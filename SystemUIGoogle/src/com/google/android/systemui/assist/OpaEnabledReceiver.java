/*
 * Copyright (C) 2022 The PixelExperience Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.systemui.assist;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;

import com.android.internal.annotations.VisibleForTesting;
import com.android.systemui.broadcast.BroadcastDispatcher;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.dagger.qualifiers.Background;
import com.android.systemui.dagger.qualifiers.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;

@SysUISingleton
public class OpaEnabledReceiver {
    private final @Background Executor mBgExecutor;
    private final BroadcastDispatcher mBroadcastDispatcher;
    private final ContentObserver mContentObserver;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private final @Main Executor mFgExecutor;
    private final OpaEnabledSettings mOpaEnabledSettings;
    private final BroadcastReceiver mBroadcastReceiver = new OpaEnabledBroadcastReceiver();
    private final List<OpaEnabledListener> mListeners = new ArrayList<>();
    private boolean mIsAGSAAssistant;
    private boolean mIsLongPressHomeEnabled;
    private boolean mIsOpaEligible;
    private boolean mIsOpaEnabled;

    @Inject
    public OpaEnabledReceiver(Context context, BroadcastDispatcher broadcastDispatcher, @Main Executor fgExecutor, @Background Executor bgExecutor, OpaEnabledSettings opaEnabledSettings) {
        mContext = context;
        mContentResolver = context.getContentResolver();
        mContentObserver = new AssistantContentObserver(context);
        mBroadcastDispatcher = broadcastDispatcher;
        mFgExecutor = fgExecutor;
        mBgExecutor = bgExecutor;
        mOpaEnabledSettings = opaEnabledSettings;
        updateOpaEnabledState(false);
        registerContentObserver();
        registerEnabledReceiver(-2);
    }

    public void addOpaEnabledListener(OpaEnabledListener opaEnabledListener) {
        mListeners.add(opaEnabledListener);
        opaEnabledListener.onOpaEnabledReceived(mContext, mIsOpaEligible, mIsAGSAAssistant, mIsOpaEnabled, mIsLongPressHomeEnabled);
    }

    public void onUserSwitching(int userId) {
        updateOpaEnabledState(true);
        mContentResolver.unregisterContentObserver(mContentObserver);
        registerContentObserver();
        mBroadcastDispatcher.unregisterReceiver(mBroadcastReceiver);
        registerEnabledReceiver(userId);
    }

    private void updateOpaEnabledState(boolean notifyListeners) {
        updateOpaEnabledState(notifyListeners, null);
    }

    private void updateOpaEnabledState(final boolean notifyListeners, final BroadcastReceiver.PendingResult pendingResult) {
        mBgExecutor.execute(() -> {
            synchronized (this) {
                try {
                    mIsOpaEligible = mOpaEnabledSettings.isOpaEligible();
                    mIsAGSAAssistant = mOpaEnabledSettings.isAgsaAssistant();
                    mIsOpaEnabled = mOpaEnabledSettings.isOpaEnabled();
                    mIsLongPressHomeEnabled = mOpaEnabledSettings.isLongPressHomeEnabled();

                    if (notifyListeners) {
                        mFgExecutor.execute(() -> dispatchOpaEnabledState(mContext));
                    }
                } finally {
                    if (pendingResult != null) {
                        pendingResult.finish();
                    }
                }
            }
        });
    }

    public void dispatchOpaEnabledState() {
        dispatchOpaEnabledState(mContext);
    }

    private void dispatchOpaEnabledState(Context context) {
        Log.i("OpaEnabledReceiver", "Dispatching OPA eligible = " + mIsOpaEligible + "; AGSA = " + mIsAGSAAssistant + "; OPA enabled = " + mIsOpaEnabled);
        for (OpaEnabledListener listener : mListeners) {
            listener.onOpaEnabledReceived(context, mIsOpaEligible, mIsAGSAAssistant, mIsOpaEnabled, mIsLongPressHomeEnabled);
        }
    }

    private void registerContentObserver() {
        mContentResolver.registerContentObserver(Settings.Secure.getUriFor("assistant"), false, mContentObserver, -2);
        mContentResolver.registerContentObserver(Settings.Secure.getUriFor("assist_long_press_home_enabled"), false, mContentObserver, -2);
    }

    private void registerEnabledReceiver(int userId) {
        mBroadcastDispatcher.registerReceiver(mBroadcastReceiver, new IntentFilter("com.google.android.systemui.OPA_ENABLED"), mBgExecutor, new UserHandle(userId));
        mBroadcastDispatcher.registerReceiver(mBroadcastReceiver, new IntentFilter("com.google.android.systemui.OPA_USER_ENABLED"), mBgExecutor, new UserHandle(userId));
    }

    @VisibleForTesting
    BroadcastReceiver getBroadcastReceiver() {
        return mBroadcastReceiver;
    }

    private class AssistantContentObserver extends ContentObserver {
        public AssistantContentObserver(Context context) {
            super(new Handler(context.getMainLooper()));
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            updateOpaEnabledState(true);
        }
    }

    private class OpaEnabledBroadcastReceiver extends BroadcastReceiver {
        private OpaEnabledBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.google.android.systemui.OPA_ENABLED".equals(intent.getAction())) {
                mOpaEnabledSettings.setOpaEligible(intent.getBooleanExtra("OPA_ENABLED", false));
            } else if ("com.google.android.systemui.OPA_USER_ENABLED".equals(intent.getAction())) {
                mOpaEnabledSettings.setOpaEnabled(intent.getBooleanExtra("OPA_USER_ENABLED", false));
            }
            updateOpaEnabledState(true, goAsync());
        }
    }
}
