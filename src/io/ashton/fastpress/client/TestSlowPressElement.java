package io.ashton.fastpress.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class TestSlowPressElement extends Composite {

  private static TestSlowPressElementUiBinder uiBinder = GWT
      .create(TestSlowPressElementUiBinder.class);

  interface TestSlowPressElementUiBinder extends UiBinder<Widget, TestSlowPressElement> {
  }

  interface Style extends CssResource {
    String wrapNormal();
    String wrapHoldPress();
  }

  @UiField Style style;
  @UiField InlineLabel text;

  public TestSlowPressElement(String textString) {
    initWidget(uiBinder.createAndBindUi(this));
    text.setText(textString);
  }

  public void showClickStyleChange(){
    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
      @Override
      public void execute() {
        clearStyles();
        addStyleName(style.wrapHoldPress());
        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
          @Override
          public boolean execute() {
            clearStyles();
            addStyleName(style.wrapNormal());
            return false;
          }
        }, 75);
      }
    });
  }

  private void clearStyles(){
    removeStyleName(style.wrapNormal());
    removeStyleName(style.wrapHoldPress());
  }

}
