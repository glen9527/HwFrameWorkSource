package com.android.server.content;

import android.content.IContentService;
import android.os.RemoteException;
import android.os.ShellCommand;
import java.io.PrintWriter;

public class ContentShellCommand extends ShellCommand {
    final IContentService mInterface;

    ContentShellCommand(IContentService service) {
        this.mInterface = service;
    }

    public int onCommand(String cmd) {
        if (cmd == null) {
            return handleDefaultCommands(cmd);
        }
        PrintWriter pw = getOutPrintWriter();
        try {
            int i = (cmd.hashCode() == -796331115 && cmd.equals("reset-today-stats")) ? 0 : -1;
            if (i != 0) {
                return handleDefaultCommands(cmd);
            }
            return runResetTodayStats();
        } catch (RemoteException e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Remote exception: ");
            stringBuilder.append(e);
            pw.println(stringBuilder.toString());
            return -1;
        }
    }

    private int runResetTodayStats() throws RemoteException {
        this.mInterface.resetTodayStats();
        return 0;
    }

    public void onHelp() {
        PrintWriter pw = getOutPrintWriter();
        pw.println("Content service commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println(BackupManagerConstants.DEFAULT_BACKUP_FINISHED_NOTIFICATION_RECEIVERS);
        pw.println("  reset-today-stats");
        pw.println("    Reset 1-day sync stats.");
        pw.println();
    }
}
