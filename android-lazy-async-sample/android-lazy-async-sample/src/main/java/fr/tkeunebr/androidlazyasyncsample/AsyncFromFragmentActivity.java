package fr.tkeunebr.androidlazyasyncsample;

import android.app.Activity;
import android.os.Bundle;

public final class AsyncFromFragmentActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fragment);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.main_container, new QueryFragment()).commit();
        }
    }
}
