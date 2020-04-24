package com.thinqtv.thinqtv_android.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic class that holds a list of data, and the success of the action.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private final List<T> data;
    private final boolean success;
    Result(List<T> data, boolean success) {
        this.data = data;
        this.success = success;
    }
    Result(T data, boolean success) {
        List<T> dataList = new ArrayList<>();
        dataList.add(data);
        this.data = dataList;
        this.success = success;
    }
    public boolean isSuccess() {
        return success;
    }
    public List<T> getData() {
        return data;
    }
}