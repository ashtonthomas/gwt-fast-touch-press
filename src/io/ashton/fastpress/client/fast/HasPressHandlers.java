package io.ashton.fastpress.client.fast;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasPressHandlers extends HasHandlers {
  HandlerRegistration addPressHandler(PressHandler handler);

}
