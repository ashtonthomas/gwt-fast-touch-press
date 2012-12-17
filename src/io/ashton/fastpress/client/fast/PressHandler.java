package io.ashton.fastpress.client.fast;

import com.google.gwt.event.shared.EventHandler;


public interface PressHandler extends EventHandler {
  void onPress(PressEvent event);

}
