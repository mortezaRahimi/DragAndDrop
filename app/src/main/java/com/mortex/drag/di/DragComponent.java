package com.mortex.drag.di;

import com.mortex.drag.ui.MainActivity;
import com.mortex.drag.data.api.ApiModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Morteza Rahimi on 17,February,2019
 */
@Singleton
@Component(modules = {
        ApiModule.class,
        DragModule.class
})
public interface DragComponent {

    void inject(MainActivity mainActivity);
}
