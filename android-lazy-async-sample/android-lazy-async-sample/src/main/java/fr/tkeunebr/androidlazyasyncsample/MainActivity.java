package fr.tkeunebr.androidlazyasyncsample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public final class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        findViewById(R.id.from_activity).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AsyncFromActivity.class));
            }
        });

        findViewById(R.id.from_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AsyncFromFragmentActivity.class));
            }
        });
    }
}
