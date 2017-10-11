package com.jaynm.pulltorefresh;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@IntDef({SwipeMenuListView.DIRECTION_LEFT_TO_RIGHT, SwipeMenuListView.DIRECTION_RIGHT_TO_LEFT})
@Retention(RetentionPolicy.SOURCE)
public @interface SwipeDirection {
}
