package fr.tkeunebr.androidlazyasyncsample;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.tkeunebr.androidlazyasync.AsyncFragment;
import fr.tkeunebr.androidlazyasync.SimpleAsyncCallbacks;
import fr.tkeunebr.androidlazyasyncsample.async.SampleTaskFragment;

public final class AsyncFromActivity extends Activity {
    private static final String STATE_SUCESS = "success";
    private SampleTaskFragment mTaskFragment;
    private ProgressBar mProgressBar;
    private boolean mSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_query);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        mTaskFragment = AsyncFragment.get(getFragmentManager(), SampleTaskFragment.class);
        mTaskFragment.setCallbacks(new Callbacks());
        if (savedInstanceState == null) {
            mTaskFragment.startNewTask();
        } else {
            if (mTaskFragment.isRunning()) {
                prepareUiForQuery();
            } else {
                mSuccess = savedInstanceState.getBoolean(STATE_SUCESS, false);
                if (mSuccess) {
                    findViewById(R.id.text_success).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
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
    }

    private void prepareUiForQuery() {
        clearMessage();
        showProgressBar();
    }

    private void clearUi() {
        clearMessage();
        clearProgressBar();
    }

    private void clearMessage() {
        final View success = findViewById(R.id.text_success);
        if (success.getVisibility() == View.VISIBLE) {
            success.setVisibility(View.GONE);
        }
    }

    private void showMessage() {
        findViewById(R.id.text_success).setVisibility(View.VISIBLE);
    }

    private void clearProgressBar() {
        ViewGroup root = (ViewGroup) findViewById(R.id.container);
        root.removeView(mProgressBar);
    }

    private void showProgressBar() {
        ViewGroup root = (ViewGroup) findViewById(R.id.container);
        if (mProgressBar == null) {
            mProgressBar = (ProgressBar) getLayoutInflater().inflate(R.layout.progressbar, root, false);
        }
        root.addView(mProgressBar);
    }

    private final class Callbacks extends SimpleAsyncCallbacks<Void> {
        @Override
        public void onPreExecute() {
            prepareUiForQuery();
        }

        @Override
        public void onPostExecute(Void result) {
            mSuccess = true;
            showMessage();
            clearProgressBar();
        }

        @Override
        public void onError() {
            Toast.makeText(AsyncFromActivity.this, "An error occurred. Please retry later", Toast.LENGTH_SHORT).show();
            clearUi();
        }
    }
}
