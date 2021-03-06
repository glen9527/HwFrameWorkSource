package com.android.server.connectivity;

import android.content.Context;
import android.net.ConnectivityMetricsEvent;
import android.net.IIpConnectivityMetrics.Stub;
import android.net.INetdEventCallback;
import android.net.ip.IpClient;
import android.net.metrics.ApfProgramEvent;
import android.os.Binder;
import android.provider.Settings.Global;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.RingBuffer;
import com.android.internal.util.TokenBucket;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.connectivity.metrics.nano.IpConnectivityLogClass.IpConnectivityEvent;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;

public final class IpConnectivityMetrics extends SystemService {
    private static final boolean DBG = false;
    private static final int DEFAULT_BUFFER_SIZE = 2000;
    private static final int DEFAULT_LOG_SIZE = 500;
    private static final int ERROR_RATE_LIMITED = -1;
    private static final int MAXIMUM_BUFFER_SIZE = 20000;
    private static final int MAXIMUM_CONNECT_LATENCY_RECORDS = 20000;
    private static final int NYC = 0;
    private static final int NYC_MR1 = 1;
    private static final int NYC_MR2 = 2;
    private static final ToIntFunction<Context> READ_BUFFER_SIZE = -$$Lambda$IpConnectivityMetrics$B0oR30xfeM300kIzUVaV_zUNLCg.INSTANCE;
    private static final String SERVICE_NAME = "connmetrics";
    private static final String TAG = IpConnectivityMetrics.class.getSimpleName();
    public static final int VERSION = 2;
    @VisibleForTesting
    public final Impl impl;
    @GuardedBy("mLock")
    private final ArrayMap<Class<?>, TokenBucket> mBuckets;
    @GuardedBy("mLock")
    private ArrayList<ConnectivityMetricsEvent> mBuffer;
    @GuardedBy("mLock")
    private int mCapacity;
    private final ToIntFunction<Context> mCapacityGetter;
    @VisibleForTesting
    final DefaultNetworkMetrics mDefaultNetworkMetrics;
    @GuardedBy("mLock")
    private int mDropped;
    @GuardedBy("mLock")
    private final RingBuffer<ConnectivityMetricsEvent> mEventLog;
    private final Object mLock;
    @VisibleForTesting
    NetdEventListenerService mNetdListener;

    public final class Impl extends Stub {
        static final String CMD_DEFAULT = "";
        static final String CMD_FLUSH = "flush";
        static final String CMD_IPCLIENT = "ipclient";
        static final String CMD_LIST = "list";
        static final String CMD_PROTO = "proto";

        public int logEvent(ConnectivityMetricsEvent event) {
            enforceConnectivityInternalPermission();
            return IpConnectivityMetrics.this.append(event);
        }

        /* JADX WARNING: Removed duplicated region for block: B:26:0x0055  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x0082  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x007c  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0069 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x0063  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0055  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x0082  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x007c  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0069 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x0063  */
        /* JADX WARNING: Removed duplicated region for block: B:26:0x0055  */
        /* JADX WARNING: Removed duplicated region for block: B:38:0x0082  */
        /* JADX WARNING: Removed duplicated region for block: B:36:0x007c  */
        /* JADX WARNING: Removed duplicated region for block: B:30:0x0069 A:{SKIP} */
        /* JADX WARNING: Removed duplicated region for block: B:28:0x0063  */
        /* JADX WARNING: Missing block: B:19:0x0042, code:
            if (r0.equals(CMD_FLUSH) != false) goto L_0x0051;
     */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            String[] strArr;
            enforceDumpPermission();
            int i = 0;
            String cmd = args.length > 0 ? args[0] : "";
            int hashCode = cmd.hashCode();
            if (hashCode == 3322014) {
                if (cmd.equals(CMD_LIST)) {
                    i = 3;
                    strArr = null;
                    switch (i) {
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        default:
                            break;
                    }
                }
            } else if (hashCode != 97532676) {
                if (hashCode == 106940904) {
                    if (cmd.equals(CMD_PROTO)) {
                        i = 1;
                        strArr = null;
                        switch (i) {
                            case 0:
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                break;
                            default:
                                break;
                        }
                    }
                } else if (hashCode == 1864910770 && cmd.equals("ipclient")) {
                    i = 2;
                    strArr = null;
                    switch (i) {
                        case 0:
                            IpConnectivityMetrics.this.cmdFlush(pw);
                            return;
                        case 1:
                            IpConnectivityMetrics.this.cmdListAsProto(pw);
                            return;
                        case 2:
                            if (args != null && args.length > 1) {
                                strArr = (String[]) Arrays.copyOfRange(args, 1, args.length);
                            }
                            IpClient.dumpAllLogs(pw, strArr);
                            return;
                        case 3:
                            IpConnectivityMetrics.this.cmdList(pw);
                            return;
                        default:
                            IpConnectivityMetrics.this.cmdList(pw);
                            pw.println("");
                            IpClient.dumpAllLogs(pw, null);
                            return;
                    }
                }
            }
            i = -1;
            strArr = null;
            switch (i) {
                case 0:
                    break;
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }

        private void enforceConnectivityInternalPermission() {
            enforcePermission("android.permission.CONNECTIVITY_INTERNAL");
        }

        private void enforceDumpPermission() {
            enforcePermission("android.permission.DUMP");
        }

        private void enforcePermission(String what) {
            IpConnectivityMetrics.this.getContext().enforceCallingOrSelfPermission(what, "IpConnectivityMetrics");
        }

        private void enforceNetdEventListeningPermission() {
            if (Binder.getCallingUid() != 1000) {
                throw new SecurityException(String.format("Uid %d has no permission to listen for netd events.", new Object[]{Integer.valueOf(Binder.getCallingUid())}));
            }
        }

        public boolean addNetdEventCallback(int callerType, INetdEventCallback callback) {
            enforceNetdEventListeningPermission();
            if (IpConnectivityMetrics.this.mNetdListener == null) {
                return false;
            }
            return IpConnectivityMetrics.this.mNetdListener.addNetdEventCallback(callerType, callback);
        }

        public boolean removeNetdEventCallback(int callerType) {
            enforceNetdEventListeningPermission();
            if (IpConnectivityMetrics.this.mNetdListener == null) {
                return true;
            }
            return IpConnectivityMetrics.this.mNetdListener.removeNetdEventCallback(callerType);
        }
    }

    public interface Logger {
        DefaultNetworkMetrics defaultNetworkMetrics();
    }

    private class LoggerImpl implements Logger {
        private LoggerImpl() {
        }

        public DefaultNetworkMetrics defaultNetworkMetrics() {
            return IpConnectivityMetrics.this.mDefaultNetworkMetrics;
        }
    }

    public IpConnectivityMetrics(Context ctx, ToIntFunction<Context> capacityGetter) {
        super(ctx);
        this.mLock = new Object();
        this.impl = new Impl();
        this.mEventLog = new RingBuffer(ConnectivityMetricsEvent.class, 500);
        this.mBuckets = makeRateLimitingBuckets();
        this.mDefaultNetworkMetrics = new DefaultNetworkMetrics();
        this.mCapacityGetter = capacityGetter;
        initBuffer();
    }

    public IpConnectivityMetrics(Context ctx) {
        this(ctx, READ_BUFFER_SIZE);
    }

    public void onStart() {
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            this.mNetdListener = new NetdEventListenerService(getContext());
            publishBinderService(SERVICE_NAME, this.impl);
            NetdEventListenerService netdEventListenerService = this.mNetdListener;
            publishBinderService(NetdEventListenerService.SERVICE_NAME, this.mNetdListener);
            LocalServices.addService(Logger.class, new LoggerImpl());
        }
    }

    @VisibleForTesting
    public int bufferCapacity() {
        return this.mCapacityGetter.applyAsInt(getContext());
    }

    private void initBuffer() {
        synchronized (this.mLock) {
            this.mDropped = 0;
            this.mCapacity = bufferCapacity();
            this.mBuffer = new ArrayList(this.mCapacity);
        }
    }

    private int append(ConnectivityMetricsEvent event) {
        synchronized (this.mLock) {
            this.mEventLog.append(event);
            int left = this.mCapacity - this.mBuffer.size();
            if (event == null) {
                return left;
            } else if (isRateLimited(event)) {
                return -1;
            } else if (left == 0) {
                this.mDropped++;
                return 0;
            } else {
                this.mBuffer.add(event);
                int i = left - 1;
                return i;
            }
        }
    }

    private boolean isRateLimited(ConnectivityMetricsEvent event) {
        TokenBucket tb = (TokenBucket) this.mBuckets.get(event.data.getClass());
        return (tb == null || tb.get()) ? false : true;
    }

    private String flushEncodedOutput() {
        List events;
        int dropped;
        synchronized (this.mLock) {
            events = this.mBuffer;
            dropped = this.mDropped;
            initBuffer();
        }
        List<IpConnectivityEvent> protoEvents = IpConnectivityEventBuilder.toProto(events);
        this.mDefaultNetworkMetrics.flushEvents(protoEvents);
        if (this.mNetdListener != null) {
            this.mNetdListener.flushStatistics(protoEvents);
        }
        try {
            return Base64.encodeToString(IpConnectivityEventBuilder.serialize(dropped, protoEvents), 0);
        } catch (IOException e) {
            Log.e(TAG, "could not serialize events", e);
            return BackupManagerConstants.DEFAULT_BACKUP_FINISHED_NOTIFICATION_RECEIVERS;
        }
    }

    private void cmdFlush(PrintWriter pw) {
        pw.print(flushEncodedOutput());
    }

    private void cmdList(PrintWriter pw) {
        pw.println("metrics events:");
        for (ConnectivityMetricsEvent ev : getEvents()) {
            pw.println(ev.toString());
        }
        pw.println(BackupManagerConstants.DEFAULT_BACKUP_FINISHED_NOTIFICATION_RECEIVERS);
        if (this.mNetdListener != null) {
            this.mNetdListener.list(pw);
        }
        pw.println(BackupManagerConstants.DEFAULT_BACKUP_FINISHED_NOTIFICATION_RECEIVERS);
        this.mDefaultNetworkMetrics.listEvents(pw);
    }

    private void cmdListAsProto(PrintWriter pw) {
        for (IpConnectivityEvent ev : IpConnectivityEventBuilder.toProto(getEvents())) {
            pw.print(ev.toString());
        }
        if (this.mNetdListener != null) {
            this.mNetdListener.listAsProtos(pw);
        }
        this.mDefaultNetworkMetrics.listEventsAsProto(pw);
    }

    private List<ConnectivityMetricsEvent> getEvents() {
        List<ConnectivityMetricsEvent> asList;
        synchronized (this.mLock) {
            asList = Arrays.asList((ConnectivityMetricsEvent[]) this.mEventLog.toArray());
        }
        return asList;
    }

    static /* synthetic */ int lambda$static$0(Context ctx) {
        int size = Global.getInt(ctx.getContentResolver(), "connectivity_metrics_buffer_size", 2000);
        if (size <= 0) {
            return 2000;
        }
        return Math.min(size, 20000);
    }

    private static ArrayMap<Class<?>, TokenBucket> makeRateLimitingBuckets() {
        ArrayMap<Class<?>, TokenBucket> map = new ArrayMap();
        map.put(ApfProgramEvent.class, new TokenBucket(60000, 50));
        return map;
    }
}
