package com.mortex.drag.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Morteza Rahimi on 17,February,2019
 */
@Module
public class DragModule {
    Application app;

    public DragModule(Application app){
        this.app = app;
    }


    @Provides @Singleton
    Application provideApplication(){
        return app;
    }

}
