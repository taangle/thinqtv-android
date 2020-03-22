package com.thinqtv.thinqtv_android.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A generic class that holds a result success w/ data or an error exception.
 */
public class Result<T> {
    // hide the private constructor to limit subclass types (Success, Error)
    private List<T> data;
    private boolean success;
    public Result(List<T> data, boolean success) {
        this.data = data;
        this.success = success;
    }
    public Result(T data, boolean success) {
        List<T> dataList = new ArrayList<T>();
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
