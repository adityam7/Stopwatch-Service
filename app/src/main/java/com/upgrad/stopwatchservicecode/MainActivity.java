package com.upgrad.stopwatchservicecode;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button startStopwatch, stopStopwatch;
    TextView timer;

    StopwatchService mBoundService;
    boolean mIsServiceBound = false;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        startStopwatch = (Button) findViewById(R.id.start_stopwatch);
        stopStopwatch = (Button) findViewById(R.id.stop_stopwatch);
        timer = (TextView) findViewById(R.id.timer);

        startStopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StopwatchService.class);
                startService(intent);
                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
                mRunnable.run();
            }
        });

        stopStopwatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsServiceBound) {
                    unbindService(mServiceConnection);
                    mIsServiceBound = false;
                }
                Intent intent = new Intent(MainActivity.this, StopwatchService.class);
                stopService(intent);
            }
        });
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsServiceBound) {
                timer.setText(mBoundService.getTimestamp());
            }
            mHandler.postDelayed(mRunnable, 100);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (mIsServiceBound) {
            unbindService(mServiceConnection);
            mIsServiceBound = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StopwatchService.MyBinder myBinder = (StopwatchService.MyBinder) service;
            mBoundService = myBinder.getService();
            mIsServiceBound = true;
        }
    };
}