package com.aapex.rakshak;

public interface MyTaskListener {
    void onTaskSuccess();
    void onTaskFailed(String error);
}
