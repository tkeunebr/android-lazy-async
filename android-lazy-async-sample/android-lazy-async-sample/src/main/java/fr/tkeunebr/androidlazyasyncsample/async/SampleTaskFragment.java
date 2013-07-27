package fr.tkeunebr.androidlazyasyncsample.async;

import android.os.Bundle;
import android.os.SystemClock;

import fr.tkeunebr.androidlazyasync.AsyncFragment;

public final class SampleTaskFragment extends AsyncFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setExpectNullResult(true);
    }

    @Override
    protected void execute() {
        new SampleTask().exec();
    }

    private final class SampleTask extends FragmentTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            SystemClock.sleep(5000);
            return null;
        }
    }
}
