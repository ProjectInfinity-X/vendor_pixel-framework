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

package com.google.android.systemui.assist.uihints;

import android.content.Context;

import com.android.app.viewcapture.ViewCaptureAwareWindowManager;
import com.android.internal.logging.MetricsLogger;
import com.android.systemui.assist.AssistLogger;
import com.android.systemui.assist.AssistManager;
import com.android.systemui.assist.ui.DefaultUiController;
import com.android.systemui.dagger.SysUISingleton;
import com.android.systemui.navigationbar.NavigationBarController;

import javax.inject.Inject;

import dagger.Lazy;


@SysUISingleton
public class GoogleDefaultUiController extends DefaultUiController {
    @Inject
    public GoogleDefaultUiController(
            Context context,
            AssistLogger assistLogger,
            ViewCaptureAwareWindowManager viewCaptureAwareWindowManager,
            MetricsLogger metricsLogger,
            Lazy<AssistManager> assistManagerLazy,
            NavigationBarController navigationBarController) {
        super(context, assistLogger, viewCaptureAwareWindowManager, metricsLogger, assistManagerLazy, navigationBarController);
        setGoogleAssistant(false);
    }

    public void setGoogleAssistant(boolean z) {
        ((AssistantInvocationLightsView) mInvocationLightsView).setGoogleAssistant(z);
    }
}
