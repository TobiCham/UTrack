package edu.utrack.util;

/**
 * Created by Tobi on 07/03/2018.
 */

@FunctionalInterface
public interface Callback<T> {

    void callback(T t);
}
