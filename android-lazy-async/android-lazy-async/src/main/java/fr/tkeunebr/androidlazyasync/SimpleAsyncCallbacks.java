package fr.tkeunebr.androidlazyasync;

/**
 * Simple partial {@link AsyncFragment.AsyncCallbacks} implementation
 * to hide a few methods that are used less often.
 *
 * @param <Result> the type of the object returned
 *                 by the underlying {@link fr.tkeunebr.androidlazyasync.AsyncFragment.FragmentTask}.
 */
public abstract class SimpleAsyncCallbacks<Result> implements AsyncFragment.AsyncCallbacks<Result, Void> {
    @Override
    public void onProgressUpdate(Void... values) {
    }
}
