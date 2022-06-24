package com.idialogics.autofobuser.Utils;

import android.animation.Animator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.idialogics.autofobuser.R;

import java.util.Locale;

public class DigitTextView extends FrameLayout {

    private static int ANIMATION_DURATION = 250;
    TextView currentTextView, nextTextView;

    public DigitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DigitTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.digit_text_view, this);
        currentTextView = (TextView) getRootView().findViewById(R.id.currentTextView);
        nextTextView = (TextView) getRootView().findViewById(R.id.nextTextView);

        nextTextView.setTranslationY(getHeight());

        setValue(0, false);
    }

    public void setValue(final int desiredValue, boolean animate) {
        if (currentTextView.getText() == null || currentTextView.getText().length() == 0) {
            currentTextView.setText(String.format(Locale.getDefault(), "%d", desiredValue));
        }

        if (animate) {
            final int oldValue = Integer.parseInt(currentTextView.getText().toString());

            if (oldValue > desiredValue) {
                nextTextView.setText(String.format(Locale.getDefault(), "%d", oldValue - 1));

                currentTextView.animate().translationY(-getHeight()).setDuration(ANIMATION_DURATION).start();
                nextTextView.setTranslationY(nextTextView.getHeight());
                nextTextView.animate().translationY(0).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentTextView.setText(String.format(Locale.getDefault(), "%d", oldValue - 1));
                        currentTextView.setTranslationY(0);
                        if (oldValue - 1 != desiredValue) {
                            setValue(desiredValue, animate);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
            } else if (oldValue < desiredValue) {
                nextTextView.setText(String.format(Locale.getDefault(), "%d", oldValue + 1));

                currentTextView.animate().translationY(getHeight()).setDuration(ANIMATION_DURATION).start();
                nextTextView.setTranslationY(-nextTextView.getHeight());
                nextTextView.animate().translationY(0).setDuration(ANIMATION_DURATION).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        currentTextView.setText(String.format(Locale.getDefault(), "%d", oldValue + 1));
                        currentTextView.setTranslationY(0);
                        if (oldValue + 1 != desiredValue) {
                            setValue(desiredValue, animate);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                }).start();
            }
        } else {

            currentTextView.setText(desiredValue + "");
        }
    }
}