package fr.tkeunebr.androidlazyasyncsample.async;

import android.os.Bundle;
import android.os.SystemClock;

import fr.tkeunebr.androidlazyasync.AsyncFragment;

public final class SampleTaskFragmentProgress extends AsyncFragment {
    public static final int DURATION_SECONDS = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setExpectNullResult(true);
    }

    @Override
    protected void execute() {
        new SampleTask().exec();
    }

    private final class SampleTask extends FragmentTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            for (int i = 0; !isCancelled() && i < DURATION_SECONDS; i++) {
                publishProgress(i);
                SystemClock.sleep(1000);
            }
            return null;
        }
    }
}
