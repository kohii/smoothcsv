package com.smoothcsv.framework.event;

public interface SCListener<E extends SCEvent> {

  void call(E event);
}
