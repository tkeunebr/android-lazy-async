package fr.tkeunebr.androidlazyasync;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.tkeunebr.androidlazyasync.util.ThreadedAsyncTaskHelper;

/**
 * A base UI-less fragment to easily manage async queries while being closely tight
 * to the corresponding Activity or Fragment lifecycle.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class AsyncFragment extends Fragment {
    public static final int THREAD_DEFAULT_POLICY = 0;
    public static final int THREAD_POOL_EXECUTOR_POLICY = 1;
    protected FragmentTask mTask;
    private AsyncCallbacks mCallbacks;
    private boolean mIsRunning;
    private boolean mExpectNullResult;
    private int mThreadMode;

    public AsyncFragment() {
    }

    // Public API

    /**
     * Gets the {@link AsyncFragment} instance corresponding to the given class, instantiating
     * it if necessary.
     *
     * @param fm    the {@link FragmentManager}
     * @param clazz the {@link AsyncFragment} subclass to instantiate
     * @return the {@link AsyncFragment} instance.
     */
    public static <T extends AsyncFragment> T get(FragmentManager fm, Class<T> clazz) {
        final String tag = clazz.getSimpleName();
        Fragment instance = fm.findFragmentByTag(tag);
        if (instance == null) {
            try {
                instance = clazz.newInstance();
                fm.beginTransaction().add(instance, tag).commit();
            } catch (java.lang.InstantiationException e) {
                throw new IllegalArgumentException("Class must be instantiable");
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Class must be accessible");
            }
        }
        return clazz.cast(instance);
    }

    /**
     * Starts a new task and cancels the previous one if necessary, using
     * the default threading mode {@code THREAD_DEFAULT_POLICY}.
     */
    public void startNewTask() {
        startNewTask(THREAD_DEFAULT_POLICY);
    }

    /**
     * Starts a new task and cancels the previous one if necessary.
     *
     * @param threadMode the threading mode to use, must be either
     *                   {@code THREAD_DEFAULT_POLICY} or {@code THREAD_POOL_EXECUTOR_POLICY}.
     */
    public void startNewTask(final int threadMode) {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }
        final Fragment target = getTargetFragment();
        if (target instanceof AsyncCallbacks) {
            setCallbacks((AsyncCallbacks) target);
        }
        final Activity activity = getActivity();
        if (activity instanceof AsyncCallbacks) {
            setCallbacks((AsyncCallbacks) activity);
        }
        mThreadMode = threadMode;
        execute();
    }

    /**
     * Sets the callbacks to the given @param callbacks.
     * These callbacks will be propagated to the {@link Activity} or {@link Fragment}
     * owner.
     */
    public void setCallbacks(AsyncCallbacks callbacks) {
        if (mTask != null && callbacks == null) {
            mTask.cancel(true);
            mTask = null;
        }
        mCallbacks = callbacks;
    }

    /**
     * @return true if the task is currently running, false otherwise.
     */
    public final boolean isRunning() {
        return mIsRunning;
    }

    /**
     * Sets the internal expectNullResult boolean at the given
     *
     * @param expectNullResult value, the default value being false.
     *                         You should set it to true if you expect your background {@link FragmentTask}
     *                         to always returns null. This will prevent the raw
     *                         {@link fr.tkeunebr.androidlazyasync.AsyncFragment.AsyncCallbacks#onError()}
     *                         from being called when there is no results coming from the background task.
     */
    public void setExpectNullResult(boolean expectNullResult) {
        mExpectNullResult = expectNullResult;
    }

    // Lifecycle

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof AsyncCallbacks) {
            setCallbacks((AsyncCallbacks) activity);
        } else {
            final Fragment target = getTargetFragment();
            if (target instanceof AsyncCallbacks) {
                setCallbacks((AsyncCallbacks) target);
            }
        }
    }

    // This method will only be called once when the retained
    // {@link Fragment} is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            setUserVisibleHint(false);
        }
    }

    @Override
    public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    // Set the callback to null so we don't accidentally leak the
    // {@link Fragment} or {@link Activity} instance.
    @Override
    public void onDetach() {
        mCallbacks = null;

        super.onDetach();
    }

    @Override
    public void onDestroy() {
        if (mTask != null) {
            mTask.cancel(true);
            mTask = null;
        }

        super.onDestroy();
    }

    /**
     * This method is called to execute the background task.
     */
    protected abstract void execute();

    /**
     * Interface used to propagate messages to the {@link Fragment}
     * or {@link Activity} caller, depending on the underlying {@link FragmentTask} state.
     *
     * @param <Result>   the result type returned by the task.
     * @param <Progress> the progress type used by the task.
     *                   Note that it is your responsibility to call {@code FragmentTask#publishProgress(Result)}
     *                   within your {@code FragmentTask#doInBackground()}.
     */
    public interface AsyncCallbacks<Result, Progress> {
        void onPreExecute();

        void onProgressUpdate(Progress... values);

        void onPostExecute(Result result);

        void onError();
    }

    /**
     * An abstract layer that sits on top of {@link AsyncTask},
     * used to propagate appropriate callbacks to the attached
     * {@link Activity} or {@link Fragment}.
     * {@link AsyncFragment} subclasses need to call the {@code FragmentTask#exec()}
     * to start the task.
     */
    public abstract class FragmentTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
        @Override
        protected void onPreExecute() {
            if (mCallbacks != null) {
                mCallbacks.onPreExecute();
            }
            mIsRunning = true;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onProgressUpdate(Progress... values) {
            if (mCallbacks != null) {
                mCallbacks.onProgressUpdate(values);
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Result result) {
            mIsRunning = false;
            if (mCallbacks != null) {
                if (result == null && !mExpectNullResult) {
                    mCallbacks.onError();
                } else {
                    mCallbacks.onPostExecute(result);
                }
            }
        }

        @Override
        protected void onCancelled() {
            mCallbacks = null;
            mIsRunning = false;
        }

        public FragmentTask<Params, Progress, Result> exec(Params... params) {
            mTask = this;
            if (mThreadMode == THREAD_DEFAULT_POLICY) {
                execute(params);
            } else if (mThreadMode == THREAD_POOL_EXECUTOR_POLICY) {
                ThreadedAsyncTaskHelper.execute(this, params);
            } else {
                throw new IllegalArgumentException("threadMode must be either THREAD_DEFAULT_POLICY" +
                        "or THREAD_POOL_EXECUTOR_POLICY");
            }
            return this;
        }
    }
}
