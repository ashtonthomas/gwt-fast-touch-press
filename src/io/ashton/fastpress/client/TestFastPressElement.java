package io.ashton.fastpress.client;

import io.ashton.fastpress.client.fast.FastPressElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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

  public TestFastPressElement(String textString){
    initWidget(uiBinder.createAndBindUi(this));
    text.setText(textString);
  }


  @Override
  public void onHoldPressDownStyle() {
    clearStyles();
    addStyleName(style.wrapHoldPress());
  }

  @Override
  public void onHoldPressOffStyle() {
    clearStyles();
    addStyleName(style.wrapNormal());
  }

  @Override
  public void onDisablePressStyle() {
    // TODO Auto-generated method stub

  }


  @Override
  public void onEnablePressStyle() {
    // TODO Auto-generated method stub

  }

  private void clearStyles(){
    removeStyleName(style.wrapNormal());
    removeStyleName(style.wrapHoldPress());
  }


}
