package com.android.callrecorder.bean;

import java.io.Serializable;

public abstract class Entity implements Comparable, Serializable
{
    public int compareTo(Object paramObject)
    {
        if (this == paramObject) {
            return 0;
        }
        return -1;
    }
}
