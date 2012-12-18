package io.ashton.fastpress.client.fast;

import java.util.Date;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 *
 * TODO: I would like to change the term TouchClick to just Press Also, I need to add addHandler
 * method that subclasses will inherit that works in the same way addClickHandler (returning
 * HandlerRegistration) - Should not be using abstract method onTouchClickFire
 *
 *
 * GWT Implementation influenced by Google's FastPressElement:
 * https://developers.google.com/mobile/articles/fast_buttons
 *
 * Using Code examples and comments from:
 * http://stackoverflow.com/questions/9596807/converting-gwt-click-events-to-touch-events
 *
 * The FastPressElement is used to avoid the 300ms delay on mobile devices (Only do this if you want
 * to ignore the possibility of a double tap - The browser waits to see if we actually want to
 * double top)
 *
 * The "press" event will occur significantly fast (around 300ms faster). However the biggest
 * improvement is from enabling fast consecutive touches.
 *
 * If you try to rapidly touch one or more FastPressElements, you will notice a MUCH great
 * improvement.
 *
 * @author ashton
 *
 */
public abstract class FastPressElement extends Composite implements HasPressHandlers {

  private boolean touchHandled = false;
  private boolean clickHandled = false;
  private boolean touchMoved = false;
  private boolean isEnabled = true;
  private int touchId;
  private int flashDelay = 75; // Default time delay in ms to flash style change

  public FastPressElement() {
    // Sink Click and Touch Events
    // I am not going to sink Mouse events since
    // I don't think we will gain anything

    sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS); // Event.TOUCHEVENTS adds all (Start, End,
                                                   // Cancel, Change)

  }

  // TEMP
  private long timeStart;
  private VerticalPanel debugPanel;

  public void setDebugPanel(VerticalPanel debugPanel) {
    this.debugPanel = debugPanel;
  }

  private long getUnixTimeStamp() {
    Date date = new Date();
    return date.getTime();
  }

  // END TEMP/DEBUG

  public FastPressElement(int msDelay) {
    this();
    if (msDelay >= 0) {
      flashDelay = msDelay;
    }
  }

  public void setEnabled(boolean enabled) {
    this.isEnabled = enabled;
  }

  /**
   * Use this method in the same way you would use addClickHandler or addDomHandler
   *
   */
  @Override
  public HandlerRegistration addPressHandler(PressHandler handler) {
    // Use Widget's addHandler to ensureHandlers and add the type/return handler
    // We don't use addDom/BitlessHandlers since we aren't sinkEvents
    // We also aren't even dealing with a DomEvent
    return addHandler(handler, PressEvent.getType());
  }

  /**
   *
   * @param event
   */
  private void firePressEvent(Event event) {
    // This better be a ClickEvent or TouchEndEvent
    // TODO might want to verify
    // (hitting issue with web.bindery vs g.gwt.user package diff)

    // only fire if enabled
    if (isEnabled) {
      PressEvent pressEvent = new PressEvent(event);
      fireEvent(pressEvent);
    }
  }

  /**
   * Implement the handler for pressing but NOT releasing the button. Normally you just want to show
   * some CSS style change to alert the user the element is active but not yet pressed
   *
   * ONLY FOR STYLE CHANGE - Will briefly be called onClick
   *
   */
  public abstract void onHoldPressDown();

  /**
   * Implement the handler for release of press. This should just be some CSS or Style change.
   *
   * ONLY FOR STYLE CHANGE - Will briefly be called onClick
   */
  public abstract void onHoldPressOff();

  @Override
  public Widget getWidget() {
    return super.getWidget();
  }

  @Override
  public void onBrowserEvent(Event event) {
    switch (DOM.eventGetType(event)) {
      case Event.ONTOUCHSTART: {
        onTouchStart(event);
        break;
      }
      case Event.ONTOUCHEND: {
        onTouchEnd(event);
        break;
      }
      case Event.ONTOUCHMOVE: {
        onTouchMove(event);
        break;
      }
      case Event.ONTOUCHCANCEL:
        onTouchCancel(event);
        break;
      case Event.ONCLICK: {
        onClick(event);
        return;
      }
    }

    // Let parent handle event if not one of the above (?)
    super.onBrowserEvent(event);
  }

  private void onTouchCancel(Event event) {
    // Just mark as moved so we don't have a touchEnd/fire
    touchMoved = true;
  }

  private void onClick(Event event) {
    event.stopPropagation();

    if (touchHandled) {
      // if the touch is already handled, we are on a device
      // that supports touch (so you aren't in the desktop browser)

      // Let's see how much time we saved from touch
      long timeEnd = getUnixTimeStamp();
      if(debugPanel != null){
        debugPanel.add(new HTML("Touch/Click Delay of: "+(timeEnd - timeStart)+" ms (SAVED!)."));
      }
      // End benchmark

      touchHandled = false;
      clickHandled = true;
      super.onBrowserEvent(event);
    } else {
      if (clickHandled) {
        // Not sure how this situation would occur
        // onClick being called twice..
        event.preventDefault();
      } else {
        // Press not handled yet

        clickHandled = false;

        // We still want to briefly fire the style change
        // To give good user feedback
        // Show HoldPress when possible
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            // Show hold press
            onHoldPressDown();

            // Now schedule a delay (which will allow the actual
            // onTouchClickFire to executed
            Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
              @Override
              public boolean execute() {
                // Clear the style change
                onHoldPressOff();
                return false;
              }
            }, flashDelay);
          }
        });

        firePressEvent(event);

      }
    }
  }

  private void onTouchStart(Event event) {
    // TEMP benchmark
    timeStart = getUnixTimeStamp();

    onHoldPressDown(); // Show style change

    // Stop the event from bubbling up
    event.stopPropagation();

    // Only handle if we have exactly one touch
    if (event.getTargetTouches().length() == 1) {

      Touch start = event.getTargetTouches().get(0);
      touchId = start.getIdentifier();
      touchMoved = false;
    }

  }

  /**
   * Check to see if the touch has moved off of the element.
   *
   * NOTE that in iOS the elasticScroll may make the touch/move cancel more difficult.
   *
   * @param event
   */
  private void onTouchMove(Event event) {
    if (!touchMoved) {
      Touch move = null;

      for (int i = 0; i < event.getChangedTouches().length(); i++) {
        if (event.getChangedTouches().get(i).getIdentifier() == touchId) {
          move = event.getChangedTouches().get(i);
        }
      }

      if (move != null) {
        // Check to see if we moved off of the original element

        int yCord = move.getClientY();
        int xCord = move.getClientX();

        boolean yTop = getWidget().getAbsoluteTop() > yCord; // is y NOT above element
        boolean yBottom = (getWidget().getAbsoluteTop() + getWidget().getOffsetHeight()) < yCord; // is
                                                                                                   // y
                                                                                                   // NOT
                                                                                                   // below
                                                                                                   // the
                                                                                                   // element
        boolean xLeft = getWidget().getAbsoluteLeft() > xCord; // is x NOT to the left of element
        boolean xRight = (getWidget().getAbsoluteLeft() + getWidget().getOffsetWidth()) > xCord; // is
                                                                                                  // x
                                                                                                  // NOT
                                                                                                  // to
                                                                                                  // the
                                                                                                  // right
                                                                                                  // of
                                                                                                  // the
                                                                                                  // element

        if (yTop && yBottom && xLeft && xRight) {
          touchMoved = true;
        }

      }

    }

  }

  private void onTouchEnd(Event event) {
    if (!touchMoved) {
      touchHandled = true;
      firePressEvent(event);
      onHoldPressOff();// Change back the style
    }
  }

}
