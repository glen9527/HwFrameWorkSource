package com.android.server.wm;

public abstract class AbsTaskStack extends WindowContainer<Task> {
    AbsTaskStack(WindowManagerService service) {
        super(service);
    }
}
