package io.ashton.fastpress.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Gwt_fast_press implements EntryPoint {
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    VerticalPanel content = new VerticalPanel();
    content.getElement().getStyle().setWidth(100, Unit.PCT);
    content.getElement().getStyle().setProperty("margin", "auto");

    InlineLabel title = new InlineLabel("Test Fast Button");

    final VerticalPanel debugPanel = new VerticalPanel();

    TestFastPressElement testButtonHello = new TestFastPressElement("Hello", new Command() {
      @Override
      public void execute() {
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
      }
    });
    testButtonHello.setDebugPanel(debugPanel);

    TestFastPressElement testButtonGoodBye =
        new TestFastPressElement("Fast GoodBye", new Command() {
          @Override
          public void execute() {
            debugPanel.add(new HTML(" Fast Good Bye! "));
          }
        });
    testButtonGoodBye.setDebugPanel(debugPanel);

    TestFastPressElement testButtonFoobar = new TestFastPressElement("Fast Foo Bar", new Command() {
      @Override
      public void execute() {
        debugPanel.add(new HTML(" Fast Foo Bar! "));
      }
    });
    testButtonFoobar.setDebugPanel(debugPanel);

    final TestSlowPressElement testSlowOne = new TestSlowPressElement(" Slow One");

    testSlowOne.addDomHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        testSlowOne.showClickStyleChange();
        debugPanel.add(new HTML(" Slow One Pressed"));
      }
    }, ClickEvent.getType());

    final TestSlowPressElement testSlowTwo = new TestSlowPressElement(" Slow Two");

    testSlowTwo.addDomHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        testSlowTwo.showClickStyleChange();
        debugPanel.add(new HTML(" Slow Two Pressed"));
      }
    }, ClickEvent.getType());

    final TestSlowPressElement testSlowThree = new TestSlowPressElement(" Slow Three");

    testSlowThree.addDomHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        testSlowThree.showClickStyleChange();
        debugPanel.add(new HTML(" Slow Three Pressed"));
      }
    }, ClickEvent.getType());

    TestFastPressElement testButton5 = new TestFastPressElement("Fast Clear", new Command() {
      @Override
      public void execute() {
        debugPanel.clear();
      }
    });

    // Add elements before adding to DOM for performance (?)
    content.add(title);
    content.add(new InlineLabel("-- FAST --"));

    content.add(testButtonHello);
    content.add(new HTML("<br/>"));
    content.add(testButtonGoodBye);
    content.add(new HTML("<br/>"));
    content.add(testButtonFoobar);
    content.add(new HTML("<br/> ---------- SLOW ---------- <br/>"));
    content.add(testSlowOne);
    content.add(new HTML("<br/>"));
    content.add(testSlowTwo);
    content.add(new HTML("<br/>"));
    content.add(testSlowThree);
    content.add(new HTML("<br/> ================= <br/>"));

    content.add(testButton5);

    content.add(new InlineLabel("----"));
    content.add(debugPanel);
    content.add(new InlineLabel("----------------------------------"));

    RootPanel.get().add(content);
  }
}
