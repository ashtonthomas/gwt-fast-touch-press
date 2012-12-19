package io.ashton.fastpress.client.fast;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
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
 * NOTE: Different browsers will handle quick swipe or long hold/drag touches differently. This is
 * an edge case if the user is long pressing or pressing while dragging the finger slightly (but
 * staying on the element) - The browser may or may not fire the event. However, the browser will
 * always fire the regular tap/press very quickly. iOS/Safari will honor a long press better than
 * some android devices. If you are having problems with your long press or very quick press, it is
 * probably because the mobile browser isn't firing the events (This is probably for a reason and in
 * the best interest of the user)
 *
 * TODO We should be able to embed fastElements and have the child fastElements NOT bubble the event
 * So we can embed the elements if needed (???)
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

  // We can just check to see if the user keeps their finger on the element
  // while they move their finger. This is because iOS has a very responsive
  // scroll/swipe which is proportional to the move. Therefore, if you try to scroll
  // on a page but your finger starts on a fastPressElement when you let go your
  // finger will still be on the element and thus firing a pressEvent.
  // use the below threshold to detect any kind of swipe or scroll.
  // There are some drawback here since you in fact want to allow for a greater
  // threshold (as long as you stay on the element) but we cannot satisfy this
  // all the time.
  private int swipDetectThreshold = 25;

  // If we use swipDetect Threshold we will need start X,Y
  private int startY;
  private int startX;

  public FastPressElement() {
    // Sink Click and Touch Events
    // I am not going to sink Mouse events since
    // I don't think we will gain anything

    sinkEvents(Event.ONCLICK | Event.TOUCHEVENTS); // Event.TOUCHEVENTS adds all (Start, End,
                                                   // Cancel, Change)

  }

  public FastPressElement(int msDelay) {
    this();
    if (msDelay >= 0) {
      flashDelay = msDelay;
    }
  }

  public void setEnabled(boolean enabled) {
    if (enabled) {
      onEnablePressStyle();
    } else {
      onDisablePressStyle();
    }
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
    // This better verify a ClickEvent or TouchEndEvent
    // TODO might want to verify
    // (hitting issue with web.bindery vs g.gwt.user package diff)
    PressEvent pressEvent = new PressEvent(event);
    fireEvent(pressEvent);
  }

  /**
   * Implement the handler for pressing but NOT releasing the button. Normally you just want to show
   * some CSS style change to alert the user the element is active but not yet pressed
   *
   * ONLY FOR STYLE CHANGE - Will briefly be called onClick
   *
   * TIP: Don't make a dramatic style change. Take note that if a user is just trying to scroll, and
   * start on the element and then scrolls off, we may not want to distract them too much. If a user
   * does scroll off the element,
   *
   */
  public abstract void onHoldPressDownStyle();

  /**
   * Implement the handler for release of press. This should just be some CSS or Style change.
   *
   * ONLY FOR STYLE CHANGE - Will briefly be called onClick
   *
   * TIP: This should just go back to the normal style.
   */
  public abstract void onHoldPressOffStyle();

  /**
   * Change styling to disabled
   */
  public abstract void onDisablePressStyle();

  /**
   * Change styling to enabled
   *
   * TIP:
   */
  public abstract void onEnablePressStyle();

  @Override
  public Widget getWidget() {
    return super.getWidget();
  }

  @Override
  public void onBrowserEvent(Event event) {
    switch (DOM.eventGetType(event)) {
      case Event.ONTOUCHSTART: {
        if (isEnabled) {
          onTouchStart(event);
        }
        break;
      }
      case Event.ONTOUCHEND: {
        if (isEnabled) {
          onTouchEnd(event);
        }
        break;
      }
      case Event.ONTOUCHMOVE: {
        if (isEnabled) {
          onTouchMove(event);
        }
        break;
      }
      case Event.ONCLICK: {
        if (isEnabled) {
          onClick(event);
        }
        return;
      }
      default: {
        // Let parent handle event if not one of the above (?)
        super.onBrowserEvent(event);
      }
    }

  }

  private void onClick(Event event) {
    event.stopPropagation();

    if (touchHandled) {
      // if the touch is already handled, we are on a device
      // that supports touch (so you aren't in the desktop browser)

      touchHandled = false;// reset for next press
      clickHandled = true;//

      super.onBrowserEvent(event);

    } else {
      if (clickHandled) {
        // Not sure how this situation would occur
        // onClick being called twice..
        event.preventDefault();
      } else {
        // Press not handled yet

        // We still want to briefly fire the style change
        // To give good user feedback
        // Show HoldPress when possible
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
          @Override
          public void execute() {
            // Show hold press
            onHoldPressDownStyle();

            // Now schedule a delay (which will allow the actual
            // onTouchClickFire to executed
            Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
              @Override
              public boolean execute() {
                // Clear the style change
                onHoldPressOffStyle();
                return false;
              }
            }, flashDelay);
          }
        });

        clickHandled = false;
        firePressEvent(event);

      }
    }
  }

  private void onTouchStart(Event event) {

    onHoldPressDownStyle(); // Show style change

    // Stop the event from bubbling up
    event.stopPropagation();

    // Only handle if we have exactly one touch
    if (event.getTargetTouches().length() == 1) {
      Touch start = event.getTargetTouches().get(0);

      this.startX = start.getClientX();
      this.startY = start.getClientY();

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

      // Check to see if we moved off of the original element

      // Use Page coordinates since we compare with widget's absolute coordinates
      int yCord = move.getPageY();
      int xCord = move.getPageX();

      int deltaX = Math.abs(startX - move.getClientX());
      int deltaY = Math.abs(startY - move.getClientY());

      if (deltaX > swipDetectThreshold || deltaY > swipDetectThreshold) {

        // Move exceeds threshold. This may be a swip gesture or scroll
        touchMoved = true;

      } else {

        // Within threshold. Check to make sure we are still on the element
        boolean yTop = getWidget().getAbsoluteTop() > yCord; // is y above element
        boolean yBottom = (getWidget().getAbsoluteTop() + getWidget().getOffsetHeight()) < yCord; // y
        // below
        boolean xLeft = getWidget().getAbsoluteLeft() > xCord; // is x to the left of element
        boolean xRight = (getWidget().getAbsoluteLeft() + getWidget().getOffsetWidth()) < xCord; // x
        // to
        // the
        // right
        if (yTop || yBottom || xLeft || xRight) {
          touchMoved = true;
          onHoldPressOffStyle();// Go back to normal style
        }

      }

    }

  }

  private void onTouchEnd(Event event) {
    if (!touchMoved) {
      touchHandled = true;
      firePressEvent(event);
      event.preventDefault();
      onHoldPressOffStyle();// Change back the style
    }
  }

}
