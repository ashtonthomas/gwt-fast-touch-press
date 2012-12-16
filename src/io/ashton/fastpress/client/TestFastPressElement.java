package io.ashton.fastpress.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Widget;

public class TestFastPressElement extends FastPressElement {

  private static TestFastPressElementUiBinder uiBinder = GWT.create(TestFastPressElementUiBinder.class);

  interface TestFastPressElementUiBinder extends UiBinder<Widget, TestFastPressElement> {
  }

  interface Style extends CssResource {
    String wrapNormal();
    String wrapHoldPress();
  }

  @UiField InlineLabel text;
  @UiField Style style;
  private Command onTouchClick;

  public TestFastPressElement(String textString, Command onTouchClick){
    initWidget(uiBinder.createAndBindUi(this));
    this.onTouchClick = onTouchClick;
    text.setText(textString);
  }

  @Override
  public void onTouchClickFire(Event event) {
    onTouchClick.execute();
  }

  @Override
  public void onHoldPressDown() {
    clearStyles();
    addStyleName(style.wrapHoldPress());
  }

  @Override
  public void onHoldPressOff() {
    clearStyles();
    addStyleName(style.wrapNormal());
  }

  private void clearStyles(){
    removeStyleName(style.wrapNormal());
    removeStyleName(style.wrapHoldPress());
  }

}
