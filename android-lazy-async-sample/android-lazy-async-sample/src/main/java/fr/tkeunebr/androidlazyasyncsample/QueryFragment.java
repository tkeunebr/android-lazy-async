package fr.tkeunebr.androidlazyasyncsample;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.tkeunebr.androidlazyasync.AsyncFragment;
import fr.tkeunebr.androidlazyasyncsample.async.SampleTaskFragmentProgress;

public final class QueryFragment extends Fragment implements AsyncFragment.AsyncCallbacks<Void, Integer> {
    private static final String STATE_SUCESS = "success";
    private static final String STATE_ACTIONBAR_TITLE = "actionbar_title";
    private SampleTaskFragmentProgress mTaskFragment;
    private ProgressBar mProgressBar;
    private boolean mSuccess;
    private String mActionBarDefaultTitle;
    private String mActionBarCurrentTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTaskFragment = AsyncFragment.get(getFragmentManager(), SampleTaskFragmentProgress.class);
        mTaskFragment.setTargetFragment(this, 0);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_query, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final Activity activity = getActivity();
        mActionBarDefaultTitle = activity.getTitle().toString();

        if (savedInstanceState == null) {
            mTaskFragment.startNewTask();
        } else {
            if (mTaskFragment.isRunning()) {
                mActionBarCurrentTitle = savedInstanceState.getString(STATE_ACTIONBAR_TITLE);
                if (mActionBarCurrentTitle != null) {
                    activity.setTitle(mActionBarCurrentTitle);
                }
                prepareUiForQuery();
            } else {
                mSuccess = savedInstanceState.getBoolean(STATE_SUCESS, false);
                if (mSuccess) {
                    activity.findViewById(R.id.text_success).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.action_refresh:
                if (!mTaskFragment.isRunning()) {
                    mTaskFragment.startNewTask();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(STATE_SUCESS, mSuccess);
        outState.putString(STATE_ACTIONBAR_TITLE, mActionBarCurrentTitle);
    }

    private void prepareUiForQuery() {
        clearMessage();
        showProgressBar();
    }

    private void clearUi() {
        clearMessage();
        clearProgressBar();
        clearActionBar();
    }

    private void clearActionBar() {
        getActivity().getActionBar().setTitle(mActionBarDefaultTitle);
    }

    private void clearMessage() {
        final View success = getActivity().findViewById(R.id.text_success);
        if (success.getVisibility() == View.VISIBLE) {
            success.setVisibility(View.GONE);
        }
    }

    private void showMessage() {
        getActivity().findViewById(R.id.text_success).setVisibility(View.VISIBLE);
    }

    private void clearProgressBar() {
        ViewGroup root = (ViewGroup) getActivity().findViewById(R.id.container);
        root.removeView(mProgressBar);
    }

    private void showProgressBar() {
        final Activity context = getActivity();
        ViewGroup root = (ViewGroup) context.findViewById(R.id.container);
        if (mProgressBar == null) {
            mProgressBar = (ProgressBar) context.getLayoutInflater().inflate(R.layout.progressbar, root, false);
        }
        root.addView(mProgressBar);
    }

    @Override
    public void onPreExecute() {
        prepareUiForQuery();
    }

    @Override
    public void onProgressUpdate(Integer... progress) {
        getActivity().setTitle(mActionBarDefaultTitle +
                ", remaining: " + String.valueOf(SampleTaskFragmentProgress.DURATION_SECONDS - progress[0])
                + " sec");
    }

    @Override
    public void onPostExecute(Void aVoid) {
        mSuccess = true;
        showMessage();
        clearProgressBar();
        clearActionBar();
    }

    @Override
    public void onError() {
        Toast.makeText(getActivity(), "An error occurred. Please retry later", Toast.LENGTH_SHORT).show();
        clearUi();
    }
}
