package com.mortex.drag.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mortex.drag.DragApplication;
import com.mortex.drag.R;
import com.mortex.drag.data.api.ApiService;
import com.mortex.drag.data.api.model.TimeResponse;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Inject
    ApiService apiService;

    private TextView square;
    private ViewGroup rootlayout;
    private int _xDelta;
    private int _yDelta;
    private ResizeAnimation resizeAnimationUp;
    private View drawerView;
    private Boolean resized = false;
    private int[] drawerLocation = new int[2];
    private int[] squareLocation = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DragApplication.get(this).getDragComponent().inject(this);

        initViews();

        setSquareParams();

        drawerView.getLocationInWindow(drawerLocation);
        square.getLocationInWindow(squareLocation);

        final Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getTime();
                mainHandler.postDelayed(this, 1000);
            }
        };
        mainHandler.post(runnable);

    }

    private void initViews() {
        rootlayout = findViewById(R.id.root_view);
        square = rootlayout.findViewById(R.id.obj);
        drawerView = rootlayout.findViewById(R.id.drawer_view);

        ResizeAnimation resizeAnimationDown = new ResizeAnimation(drawerView, 100, 50);
        drawerView.startAnimation(resizeAnimationDown);

        resizeAnimationUp = new ResizeAnimation(drawerView, 500, 50);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setSquareParams() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(300, 300);
        square.setLayoutParams(layoutParams);
        square.setOnTouchListener(new TouchListener());
    }

    private final class TouchListener implements View.OnTouchListener {
        @SuppressLint("ClickableViewAccessibility")
        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            switch (event.getAction() & MotionEvent.ACTION_MASK) {

                case MotionEvent.ACTION_DOWN:
                    calculateX_Y(view, X, Y);
                    break;

                case MotionEvent.ACTION_UP:
                    handleReplacement(view, X, Y);
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    break;

                case MotionEvent.ACTION_POINTER_UP:
                    break;

                case MotionEvent.ACTION_MOVE:
                    handleSquareMovement(view, X, Y);
                    break;
            }

            rootlayout.invalidate();
            return true;
        }

    }

    private void handleSquareMovement(View view, int X, int Y) {
        RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) view.getLayoutParams();
        layoutParams2.leftMargin = X - _xDelta;
        layoutParams2.topMargin = Y - _yDelta;
        layoutParams2.rightMargin = -250;
        layoutParams2.bottomMargin = -250;

        if (!resized) {
            resized = true;
            drawerView.startAnimation(resizeAnimationUp);
        }
        view.setLayoutParams(layoutParams2);
    }

    private void handleReplacement(View view, int X, int Y) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        _xDelta = X - layoutParams.leftMargin;
        _yDelta = Y - layoutParams.topMargin;
        layoutParams.leftMargin = X - _xDelta;
        layoutParams.topMargin = Y - _yDelta;
        layoutParams.rightMargin = -250;
        layoutParams.bottomMargin = -250;

        square.getLocationInWindow(squareLocation);
        drawerView.getLocationInWindow(drawerLocation);

        int squareYposition = squareLocation[1] + 300;
        int drawerYPosition = drawerLocation[1];

        Log.d("drawerY", "y: " + (drawerYPosition));
        Log.d("SQUARE", "Y: " + squareYposition);

        if (squareYposition > drawerYPosition && squareYposition < drawerYPosition + 0.25 * square.getHeight()) {
            moveTop();

        } else if (squareYposition > drawerYPosition + 0.25 * square.getHeight()) {
            //should drop in drawer
        }

        view.setLayoutParams(layoutParams);
    }

    private void calculateX_Y(View view, int X, int Y) {
        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
        _xDelta = X - lParams.leftMargin;
        _yDelta = Y - lParams.topMargin;

        Log.d("ACTIONDOWN", "X: " + X);
        Log.d("SQUARE", "Y: " + Y);
        Log.d("ACTIONDOWN", "leftMgn: " + lParams.leftMargin);
        Log.d("ACTIONDOWN", "topMgn: " + lParams.topMargin);
        Log.d("ACTIONDOWN", "X_Delt: " + _xDelta);
        Log.d("ACTIONDOWN", "Y_Delt: " + _yDelta);
    }

    private void moveTop() {

        ObjectAnimator anim = ObjectAnimator.ofFloat(square, "y", rootlayout.getTop());
        anim.setDuration(250);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                moveDown();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();

    }

    private void moveDown() {

        ObjectAnimator anim = ObjectAnimator.ofFloat(square, "y", drawerView.getY() + drawerView.getHeight() / 4);
        anim.setDuration(250);
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                    rotateSquare(square);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        anim.start();
    }

    private void getTime() {
        final Call<TimeResponse> call = apiService.getTime();
        call.enqueue(new Callback<TimeResponse>() {
            @Override
            public void onResponse(@NonNull Call<TimeResponse> call, @NonNull Response<TimeResponse> response) {
                if (response.body() != null) {
                    square.setText(separateTime(response.body().getDatetime()));
                    rotateSquare(square);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TimeResponse> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, R.string.check_net, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void rotateSquare(final TextView tv) {
        ObjectAnimator imageViewObjectAnimator = ObjectAnimator.ofFloat(tv,
                "rotation", 0f, 360f);
        imageViewObjectAnimator.setDuration(500); // miliseconds
        imageViewObjectAnimator.start();

    }


    private String separateTime(String time) {
        String[] parts = time.split("\\.");
        time = parts[0];

        return time.substring(12);
    }

}