package edu.utrack.util;

/**
 * Created by Tobi on 07/03/2018.
 */

@FunctionalInterface
public interface BiCallback<T, S> {
    void callback(T t, S s);
}
