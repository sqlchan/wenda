package com.sqlchan.wenda.async;

import java.util.List;

/**
 * Created by Administrator on 2017/7/15.
 */
public interface EventHandle {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();
}
