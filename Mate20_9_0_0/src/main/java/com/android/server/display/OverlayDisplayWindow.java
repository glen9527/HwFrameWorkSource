package com.android.server.display;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManager.DisplayListener;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import com.android.internal.util.DumpUtils.Dump;
import java.io.PrintWriter;

final class OverlayDisplayWindow implements Dump {
    private static final boolean DEBUG = false;
    private static final String TAG = "OverlayDisplayWindow";
    private final boolean DISABLE_MOVE_AND_RESIZE = false;
    private final float INITIAL_SCALE = 0.5f;
    private final float MAX_SCALE = 1.0f;
    private final float MIN_SCALE = 0.3f;
    private final float WINDOW_ALPHA = 0.8f;
    private final Context mContext;
    private final Display mDefaultDisplay;
    private final DisplayInfo mDefaultDisplayInfo = new DisplayInfo();
    private int mDensityDpi;
    private final DisplayListener mDisplayListener = new DisplayListener() {
        public void onDisplayAdded(int displayId) {
        }

        public void onDisplayChanged(int displayId) {
            if (displayId != OverlayDisplayWindow.this.mDefaultDisplay.getDisplayId()) {
                return;
            }
            if (OverlayDisplayWindow.this.updateDefaultDisplayInfo()) {
                OverlayDisplayWindow.this.relayout();
                OverlayDisplayWindow.this.mListener.onStateChanged(OverlayDisplayWindow.this.mDefaultDisplayInfo.state);
                return;
            }
            OverlayDisplayWindow.this.dismiss();
        }

        public void onDisplayRemoved(int displayId) {
            if (displayId == OverlayDisplayWindow.this.mDefaultDisplay.getDisplayId()) {
                OverlayDisplayWindow.this.dismiss();
            }
        }
    };
    private final DisplayManager mDisplayManager;
    private GestureDetector mGestureDetector;
    private final int mGravity;
    private int mHeight;
    private final Listener mListener;
    private float mLiveScale = 1.0f;
    private float mLiveTranslationX;
    private float mLiveTranslationY;
    private final String mName;
    private final OnGestureListener mOnGestureListener = new SimpleOnGestureListener() {
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            OverlayDisplayWindow.access$724(OverlayDisplayWindow.this, distanceX);
            OverlayDisplayWindow.access$824(OverlayDisplayWindow.this, distanceY);
            OverlayDisplayWindow.this.relayout();
            return true;
        }
    };
    private final OnScaleGestureListener mOnScaleGestureListener = new SimpleOnScaleGestureListener() {
        public boolean onScale(ScaleGestureDetector detector) {
            OverlayDisplayWindow.access$932(OverlayDisplayWindow.this, detector.getScaleFactor());
            OverlayDisplayWindow.this.relayout();
            return true;
        }
    };
    private final OnTouchListener mOnTouchListener = new OnTouchListener() {
        public boolean onTouch(View view, MotionEvent event) {
            float oldX = event.getX();
            float oldY = event.getY();
            event.setLocation(event.getRawX(), event.getRawY());
            OverlayDisplayWindow.this.mGestureDetector.onTouchEvent(event);
            OverlayDisplayWindow.this.mScaleGestureDetector.onTouchEvent(event);
            int actionMasked = event.getActionMasked();
            if (actionMasked == 1 || actionMasked == 3) {
                OverlayDisplayWindow.this.saveWindowParams();
            }
            event.setLocation(oldX, oldY);
            return true;
        }
    };
    private ScaleGestureDetector mScaleGestureDetector;
    private final boolean mSecure;
    private final SurfaceTextureListener mSurfaceTextureListener = new SurfaceTextureListener() {
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            OverlayDisplayWindow.this.mListener.onWindowCreated(surfaceTexture, OverlayDisplayWindow.this.mDefaultDisplayInfo.getMode().getRefreshRate(), OverlayDisplayWindow.this.mDefaultDisplayInfo.presentationDeadlineNanos, OverlayDisplayWindow.this.mDefaultDisplayInfo.state);
        }

        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            OverlayDisplayWindow.this.mListener.onWindowDestroyed();
            return true;
        }

        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
        }

        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }
    };
    private TextureView mTextureView;
    private String mTitle;
    private TextView mTitleTextView;
    private int mWidth;
    private View mWindowContent;
    private final WindowManager mWindowManager;
    private LayoutParams mWindowParams;
    private float mWindowScale;
    private boolean mWindowVisible;
    private int mWindowX;
    private int mWindowY;

    public interface Listener {
        void onStateChanged(int i);

        void onWindowCreated(SurfaceTexture surfaceTexture, float f, long j, int i);

        void onWindowDestroyed();
    }

    static /* synthetic */ float access$724(OverlayDisplayWindow x0, float x1) {
        float f = x0.mLiveTranslationX - x1;
        x0.mLiveTranslationX = f;
        return f;
    }

    static /* synthetic */ float access$824(OverlayDisplayWindow x0, float x1) {
        float f = x0.mLiveTranslationY - x1;
        x0.mLiveTranslationY = f;
        return f;
    }

    static /* synthetic */ float access$932(OverlayDisplayWindow x0, float x1) {
        float f = x0.mLiveScale * x1;
        x0.mLiveScale = f;
        return f;
    }

    public OverlayDisplayWindow(Context context, String name, int width, int height, int densityDpi, int gravity, boolean secure, Listener listener) {
        ThreadedRenderer.disableVsync();
        this.mContext = context;
        this.mName = name;
        this.mGravity = gravity;
        this.mSecure = secure;
        this.mListener = listener;
        this.mDisplayManager = (DisplayManager) context.getSystemService("display");
        this.mWindowManager = (WindowManager) context.getSystemService("window");
        this.mDefaultDisplay = this.mWindowManager.getDefaultDisplay();
        updateDefaultDisplayInfo();
        resize(width, height, densityDpi, false);
        createWindow();
    }

    public void show() {
        if (!this.mWindowVisible) {
            this.mDisplayManager.registerDisplayListener(this.mDisplayListener, null);
            if (updateDefaultDisplayInfo()) {
                clearLiveState();
                updateWindowParams();
                this.mWindowManager.addView(this.mWindowContent, this.mWindowParams);
                this.mWindowVisible = true;
            } else {
                this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
            }
        }
    }

    public void dismiss() {
        if (this.mWindowVisible) {
            this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
            this.mWindowManager.removeView(this.mWindowContent);
            this.mWindowVisible = false;
        }
    }

    public void resize(int width, int height, int densityDpi) {
        resize(width, height, densityDpi, true);
    }

    private void resize(int width, int height, int densityDpi, boolean doLayout) {
        this.mWidth = width;
        this.mHeight = height;
        this.mDensityDpi = densityDpi;
        this.mTitle = this.mContext.getResources().getString(17039947, new Object[]{this.mName, Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Integer.valueOf(this.mDensityDpi)});
        if (this.mSecure) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(this.mTitle);
            stringBuilder.append(this.mContext.getResources().getString(17039946));
            this.mTitle = stringBuilder.toString();
        }
        if (doLayout) {
            relayout();
        }
    }

    public void relayout() {
        if (this.mWindowVisible) {
            updateWindowParams();
            this.mWindowManager.updateViewLayout(this.mWindowContent, this.mWindowParams);
        }
    }

    public void dump(PrintWriter pw, String prefix) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mWindowVisible=");
        stringBuilder.append(this.mWindowVisible);
        pw.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("mWindowX=");
        stringBuilder.append(this.mWindowX);
        pw.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("mWindowY=");
        stringBuilder.append(this.mWindowY);
        pw.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("mWindowScale=");
        stringBuilder.append(this.mWindowScale);
        pw.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("mWindowParams=");
        stringBuilder.append(this.mWindowParams);
        pw.println(stringBuilder.toString());
        if (this.mTextureView != null) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("mTextureView.getScaleX()=");
            stringBuilder.append(this.mTextureView.getScaleX());
            pw.println(stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("mTextureView.getScaleY()=");
            stringBuilder.append(this.mTextureView.getScaleY());
            pw.println(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("mLiveTranslationX=");
        stringBuilder.append(this.mLiveTranslationX);
        pw.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("mLiveTranslationY=");
        stringBuilder.append(this.mLiveTranslationY);
        pw.println(stringBuilder.toString());
        stringBuilder = new StringBuilder();
        stringBuilder.append("mLiveScale=");
        stringBuilder.append(this.mLiveScale);
        pw.println(stringBuilder.toString());
    }

    private boolean updateDefaultDisplayInfo() {
        if (this.mDefaultDisplay.getDisplayInfo(this.mDefaultDisplayInfo)) {
            return true;
        }
        Slog.w(TAG, "Cannot show overlay display because there is no default display upon which to show it.");
        return false;
    }

    private void createWindow() {
        this.mWindowContent = LayoutInflater.from(this.mContext).inflate(17367211, null);
        this.mWindowContent.setOnTouchListener(this.mOnTouchListener);
        this.mTextureView = (TextureView) this.mWindowContent.findViewById(16909172);
        this.mTextureView.setPivotX(0.0f);
        this.mTextureView.setPivotY(0.0f);
        this.mTextureView.getLayoutParams().width = this.mWidth;
        this.mTextureView.getLayoutParams().height = this.mHeight;
        int i = 0;
        this.mTextureView.setOpaque(false);
        this.mTextureView.setSurfaceTextureListener(this.mSurfaceTextureListener);
        this.mTitleTextView = (TextView) this.mWindowContent.findViewById(16909173);
        this.mTitleTextView.setText(this.mTitle);
        this.mWindowParams = new LayoutParams(2026);
        LayoutParams layoutParams = this.mWindowParams;
        layoutParams.flags |= 16778024;
        if (this.mSecure) {
            layoutParams = this.mWindowParams;
            layoutParams.flags |= 8192;
        }
        layoutParams = this.mWindowParams;
        layoutParams.privateFlags |= 2;
        this.mWindowParams.alpha = 0.8f;
        this.mWindowParams.gravity = 51;
        this.mWindowParams.setTitle(this.mTitle);
        this.mGestureDetector = new GestureDetector(this.mContext, this.mOnGestureListener);
        this.mScaleGestureDetector = new ScaleGestureDetector(this.mContext, this.mOnScaleGestureListener);
        this.mWindowX = (this.mGravity & 3) == 3 ? 0 : this.mDefaultDisplayInfo.logicalWidth;
        if ((this.mGravity & 48) != 48) {
            i = this.mDefaultDisplayInfo.logicalHeight;
        }
        this.mWindowY = i;
        this.mWindowScale = 0.5f;
    }

    private void updateWindowParams() {
        float scale = Math.max(0.3f, Math.min(1.0f, Math.min(Math.min(this.mWindowScale * this.mLiveScale, ((float) this.mDefaultDisplayInfo.logicalWidth) / ((float) this.mWidth)), ((float) this.mDefaultDisplayInfo.logicalHeight) / ((float) this.mHeight))));
        float offsetScale = ((scale / this.mWindowScale) - 1.0f) * 0.5f;
        int width = (int) (((float) this.mWidth) * scale);
        int height = (int) (((float) this.mHeight) * scale);
        int y = (int) ((((float) this.mWindowY) + this.mLiveTranslationY) - (((float) height) * offsetScale));
        int x = Math.max(0, Math.min((int) ((((float) this.mWindowX) + this.mLiveTranslationX) - (((float) width) * offsetScale)), this.mDefaultDisplayInfo.logicalWidth - width));
        y = Math.max(0, Math.min(y, this.mDefaultDisplayInfo.logicalHeight - height));
        this.mTextureView.setScaleX(scale);
        this.mTextureView.setScaleY(scale);
        this.mWindowParams.x = x;
        this.mWindowParams.y = y;
        this.mWindowParams.width = width;
        this.mWindowParams.height = height;
    }

    private void saveWindowParams() {
        this.mWindowX = this.mWindowParams.x;
        this.mWindowY = this.mWindowParams.y;
        this.mWindowScale = this.mTextureView.getScaleX();
        clearLiveState();
    }

    private void clearLiveState() {
        this.mLiveTranslationX = 0.0f;
        this.mLiveTranslationY = 0.0f;
        this.mLiveScale = 1.0f;
    }
}
