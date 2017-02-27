package com.skkoolbus.skkoolbus;

import android.media.Image;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    private final CountDownLatch countDownLatch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final long time = System.currentTimeMillis();

        final RelativeLayout container = (RelativeLayout) findViewById(R.id.activity_main);
        final ImageView icLionView = (ImageView) findViewById(R.id.icLionView);
        final ImageView icSkkuUnivView = (ImageView) findViewById(R.id.icSkkuUnivView);

        final WebView webView = new WebView(getApplicationContext());
        webView.setVisibility(View.INVISIBLE);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        webView.setLayoutParams(layoutParams);

        container.addView(webView);

        if (Build.VERSION.SDK_INT >= 21) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                long interval;
                long delay = 4000;
                if ((interval = System.currentTimeMillis() - time) < delay) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            countDownLatch.countDown();
                        }
                    }, delay - interval);

                } else {
                    countDownLatch.countDown();
                }
            }
        });

        webView.loadUrl("http://skkoolbus.com/");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        container.removeView(icLionView);
                        container.removeView(icSkkuUnivView);
                        webView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }
}
