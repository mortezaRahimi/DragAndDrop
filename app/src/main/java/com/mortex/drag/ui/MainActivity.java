package com.mortex.drag.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
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

    private TextView img;
    private ViewGroup rootlayout;
    private int _xDelta;
    private int _yDelta;
    private RotateAnimation rotate;
    private ResizeAnimation resizeAnimationUp;
    private ResizeAnimation resizeAnimationDown;
    private View drawerView;
    private Rect rectf;
    private Boolean resized = false;
    private int[] drawerLocation = new int[2];
    private int[] squareLocation = new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DragApplication.get(this).getDragComponent().inject(this);

        rootlayout = (ViewGroup) findViewById(R.id.root_view);
        img = findViewById(R.id.obj);
        drawerView = findViewById(R.id.drawer_view);

        resizeAnimationDown = new ResizeAnimation(drawerView, 100, 50);
        resizeAnimationUp = new ResizeAnimation(drawerView, 500, 50);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(300, 300);

//        layoutParams.setMargins(0,50,0,0);
//        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        drawerView.getLocationInWindow(drawerLocation);
        img.getLocationInWindow(squareLocation);

        img.setLayoutParams(layoutParams);
        img.setOnTouchListener(new TouchListener());

        rotate = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotation);

        final Handler mainHnadler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                getTime();
                mainHnadler.postDelayed(this, 1000);
            }
        };

        mainHnadler.post(runnable);

        rectf = new Rect();

        drawerView.startAnimation(resizeAnimationDown);

    }

    private final class TouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent event) {
            //get raw x and y of event
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();

            // get action type and use bitwise to mask it (remove unwanted crap)
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                //action down is when touch first recognised
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();

                    //calculate position of x and y relative to top margin and left margin of layout
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
//                    Log.d("ACTIONDOWN", "X: " + X);
//                    int x = location[0];
//                    int drawerY = drawerLocation[1];
//                    Log.d("drawerY", "y: " + (drawerY));
//                    Log.d("SQUARE", "Y: " + Y);
//                    Log.d("ACTIONDOWN", "leftMgn: " + lParams.leftMargin);
//                    Log.d("ACTIONDOWN", "topMgn: " + lParams.topMargin);
//                    Log.d("ACTIONDOWN", "X_Delt: " + _xDelta);
//                    Log.d("ACTIONDOWN", "Y_Delt: " + _yDelta);
                    break;
                case MotionEvent.ACTION_UP:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;

                    img.getLocationInWindow(squareLocation);
                    drawerView.getLocationInWindow(drawerLocation);

                    int squareYposition = squareLocation[1] + 300;
                    int drawerYPosition = drawerLocation[1];

                    Log.d("drawerY", "y: " + (drawerYPosition));
                    Log.d("SQUARE", "Y: " + squareYposition);

                    if (squareYposition > drawerYPosition && squareYposition < drawerYPosition + 0.25 * img.getHeight()) {
                        moveTopAndBottom(squareYposition);
                    } else if (squareYposition > drawerYPosition + 0.25 * img.getHeight()) {
                        //should drop
                    }

                    // write new layout params back to the file

                    view.setLayoutParams(layoutParams);
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams2 = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    layoutParams2.leftMargin = X - _xDelta;
                    layoutParams2.topMargin = Y - _yDelta;
                    layoutParams2.rightMargin = -250;
                    layoutParams2.bottomMargin = -250;

                    if (!resized) {
                        resized = true;
                        drawerView.startAnimation(resizeAnimationUp);
                    }
//
//                    img.getLocationInWindow(squareLocation);
//                    drawerView.getLocationInWindow(drawerLocation);
//                    int squareYposition = squareLocation[1] + 300;
//                    int drawerYPosition = drawerLocation[1] ;
//
//                    Log.d("drawerY", "y: " + (drawerYPosition));
//                    Log.d("SQUARE", "Y: " + squareYposition);
//
//                    if (squareYposition > drawerYPosition && squareYposition < drawerYPosition + 0.25 * img.getHeight()) {
//                        moveTopAndBottom(squareYposition);
//                    } else if (squareYposition > drawerYPosition + 0.25 * img.getHeight()) {
//                        //should drop
//                    }
//
//                    // write new layout params back to the file
//
                    view.setLayoutParams(layoutParams2);


                    break;


            }
            // invalidate layout at end to cause redraw
            rootlayout.invalidate();

            // return true here, just because i guess???
            return true;
        }

    }

    private void moveTopAndBottom(int squareYPosition) {

        final ObjectAnimator objectanimator = ObjectAnimator.ofFloat(img, "y", 50);
        objectanimator.setDuration(1000);

        objectanimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                objectanimator.cancel();

            }
        });
        objectanimator.start();


//        TranslateAnimation anim = new TranslateAnimation(0, 0, squareYPosition - 300, 0);
//        anim.setDuration(1000);
//
//        anim.setAnimationListener(new TranslateAnimation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) img.getLayoutParams();
//                params.topMargin = 50;
////                params.leftMargin += amountToMoveRight;
//                img.setLayoutParams(params);
//            }
//        });
//
//        img.startAnimation(anim);
    }

    private void getTime() {
        final Call<TimeResponse> call = apiService.getTime();
        call.enqueue(new Callback<TimeResponse>() {
            @Override
            public void onResponse(Call<TimeResponse> call, Response<TimeResponse> response) {
                if (response.body() != null) {

                    img.setText(separateTime(response.body().getDatetime()));
                    img.startAnimation(rotate);

                }
            }

            @Override
            public void onFailure(Call<TimeResponse> call, Throwable t) {

            }
        });
    }


    private String separateTime(String time) {
        String[] parts = time.split("\\.");
        time = parts[0];

        return time.substring(12);
    }

}