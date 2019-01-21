/*
 * Copyright (c) 2018 NUTES/UEPB
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package br.edu.uepb.nutes.simplesurvey.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro;

import br.edu.uepb.nutes.simplesurvey.R;

public abstract class SimpleSurvey extends AppIntro {
    private final String TAG = "SimpleSurvey";
    protected IBasePage currentPage;
    protected Snackbar snackbarMessageBlockedPage;
    protected final int PAGE_END = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /**
         * Config pages.
         */
        setColorTransitionsEnabled(true);
        setFadeAnimation();
        showSeparator(false);
        showSkipButton(false);
        setNextPageSwipeLock(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setImmersive(true);
        }

        this.initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

        if (snackbarMessageBlockedPage != null)
            snackbarMessageBlockedPage.dismiss();

        if (newFragment instanceof IBasePage) {
            currentPage = (IBasePage) newFragment;
            Log.d(TAG, "onSlideChanged() - isBlocked: "
                    + currentPage.isBlocked() + " |  page: " + currentPage.getPageNumber());

            if (currentPage.getPageNumber() == PAGE_END) return;

            setNextPageSwipeLock(currentPage.isBlocked());

            // Capture event onSwipeLeft
            currentPage.getView().setOnTouchListener(new OnSwipePageTouchListener(this) {
                @Override
                public void onSwipeLeft() {
                    super.onSwipeLeft();
                    if (currentPage.isBlocked()) showMessageBlocked();
                }

                @Override
                public void onSwipeRight() {
                    super.onSwipeRight();
                    setNextPageSwipeLock(currentPage.isBlocked());
                }
            });
        }
    }

    /**
     * Show message page blocked.
     */
    protected void showMessageBlocked() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                /**
                 * Create snackbar
                 */
                snackbarMessageBlockedPage = Snackbar.make(currentPage.getView(),
                        R.string.message_blocked_page,
                        Snackbar.LENGTH_LONG);
                snackbarMessageBlockedPage.setAction(R.string.text_ok,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbarMessageBlockedPage.dismiss();
                            }
                        });

                snackbarMessageBlockedPage.show();
            }
        });
    }

    /**
     * Init view
     */
    protected abstract void initView();
}
