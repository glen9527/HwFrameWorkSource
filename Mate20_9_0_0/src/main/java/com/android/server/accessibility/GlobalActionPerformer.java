package com.android.server.accessibility;

import android.app.StatusBarManager;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.SystemClock;
import android.view.KeyEvent;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ScreenshotHelper;
import com.android.server.LocalServices;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.wm.WindowManagerInternal;
import java.util.function.Supplier;

public class GlobalActionPerformer {
    private final Context mContext;
    private Supplier<ScreenshotHelper> mScreenshotHelperSupplier;
    private final WindowManagerInternal mWindowManagerService;

    public GlobalActionPerformer(Context context, WindowManagerInternal windowManagerInternal) {
        this.mContext = context;
        this.mWindowManagerService = windowManagerInternal;
        this.mScreenshotHelperSupplier = null;
    }

    @VisibleForTesting
    public GlobalActionPerformer(Context context, WindowManagerInternal windowManagerInternal, Supplier<ScreenshotHelper> screenshotHelperSupplier) {
        this(context, windowManagerInternal);
        this.mScreenshotHelperSupplier = screenshotHelperSupplier;
    }

    /* JADX WARNING: Missing block: B:6:0x0014, code:
            return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean performGlobalAction(int action) {
        long identity = Binder.clearCallingIdentity();
        boolean openRecents;
        switch (action) {
            case 1:
                sendDownAndUpKeyEvents(4);
                Binder.restoreCallingIdentity(identity);
                return true;
            case 2:
                sendDownAndUpKeyEvents(3);
                Binder.restoreCallingIdentity(identity);
                return true;
            case 3:
                openRecents = openRecents();
                Binder.restoreCallingIdentity(identity);
                return openRecents;
            case 4:
                expandNotifications();
                Binder.restoreCallingIdentity(identity);
                return true;
            case 5:
                expandQuickSettings();
                Binder.restoreCallingIdentity(identity);
                return true;
            case 6:
                showGlobalActions();
                Binder.restoreCallingIdentity(identity);
                return true;
            case 7:
                openRecents = toggleSplitScreen();
                Binder.restoreCallingIdentity(identity);
                return openRecents;
            case 8:
                openRecents = lockScreen();
                Binder.restoreCallingIdentity(identity);
                return openRecents;
            case 9:
                try {
                    openRecents = takeScreenshot();
                    break;
                } finally {
                    break;
                }
            default:
                Binder.restoreCallingIdentity(identity);
                return false;
        }
        Binder.restoreCallingIdentity(identity);
    }

    private void sendDownAndUpKeyEvents(int keyCode) {
        long token = Binder.clearCallingIdentity();
        long downTime = SystemClock.uptimeMillis();
        int i = keyCode;
        long j = downTime;
        sendKeyEventIdentityCleared(i, 0, j, downTime);
        sendKeyEventIdentityCleared(i, 1, j, SystemClock.uptimeMillis());
        Binder.restoreCallingIdentity(token);
    }

    private void sendKeyEventIdentityCleared(int keyCode, int action, long downTime, long time) {
        KeyEvent event = KeyEvent.obtain(downTime, time, action, keyCode, 0, 0, -1, 0, 8, 257, null);
        InputManager.getInstance().injectInputEvent(event, 0);
        event.recycle();
    }

    private void expandNotifications() {
        long token = Binder.clearCallingIdentity();
        ((StatusBarManager) this.mContext.getSystemService("statusbar")).expandNotificationsPanel();
        Binder.restoreCallingIdentity(token);
    }

    private void expandQuickSettings() {
        long token = Binder.clearCallingIdentity();
        ((StatusBarManager) this.mContext.getSystemService("statusbar")).expandSettingsPanel();
        Binder.restoreCallingIdentity(token);
    }

    private boolean openRecents() {
        long token = Binder.clearCallingIdentity();
        try {
            StatusBarManagerInternal statusBarService = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
            if (statusBarService == null) {
                return false;
            }
            statusBarService.toggleRecentApps();
            Binder.restoreCallingIdentity(token);
            return true;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void showGlobalActions() {
        this.mWindowManagerService.showGlobalActions();
    }

    private boolean toggleSplitScreen() {
        long token = Binder.clearCallingIdentity();
        try {
            StatusBarManagerInternal statusBarService = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
            if (statusBarService == null) {
                return false;
            }
            statusBarService.toggleSplitScreen();
            Binder.restoreCallingIdentity(token);
            return true;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private boolean lockScreen() {
        ((PowerManager) this.mContext.getSystemService(PowerManager.class)).goToSleep(SystemClock.uptimeMillis(), 7, 0);
        this.mWindowManagerService.lockNow();
        return true;
    }

    private boolean takeScreenshot() {
        (this.mScreenshotHelperSupplier != null ? (ScreenshotHelper) this.mScreenshotHelperSupplier.get() : new ScreenshotHelper(this.mContext)).takeScreenshot(1, true, true, new Handler(Looper.getMainLooper()));
        return true;
    }
}
