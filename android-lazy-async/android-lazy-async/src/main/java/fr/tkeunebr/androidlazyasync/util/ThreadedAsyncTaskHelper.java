package fr.tkeunebr.androidlazyasync.util;

import android.os.AsyncTask;
import android.os.Build;

public final class ThreadedAsyncTaskHelper {
    private ThreadedAsyncTaskHelper() {
    }

    /**
     * Execute an {@link android.os.AsyncTask} on a thread pool.
     *
     * @param task Task to execute.
     * @param args Optional arguments to pass to {@link android.os.AsyncTask#execute(Object[])}.
     * @param <T>  Task argument type.
     */
    public static <T> void execute(AsyncTask<T, ?, ?> task, T... args) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            task.execute(args);
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
        }
    }
}
