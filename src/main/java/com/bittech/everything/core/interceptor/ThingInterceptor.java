package com.bittech.everything.core.interceptor;


import com.bittech.everything.core.model.Thing;

@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
