package com.mortex.drag;

import android.app.Application;
import android.content.Context;

import com.mortex.drag.di.DaggerDragComponent;
import com.mortex.drag.di.DragComponent;
import com.mortex.drag.di.DragModule;

/**
 * Created by Morteza Rahimi on 17,February,2019
 */
public class DragApplication extends Application {

    DragComponent component;
    private static DragApplication instance;

    public static DragApplication get(Context context) {
        return (DragApplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        component = DaggerDragComponent.builder()
                .dragModule(new DragModule(this))
                .build();


    }

    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }

    public static DragApplication getInstance() {
        return instance;
    }

    public  DragComponent getDragComponent(){
        return component;
    }

}
