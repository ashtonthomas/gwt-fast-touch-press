package io.ashton.fastpress.client;

import io.ashton.fastpress.client.fast.PressEvent;
import io.ashton.fastpress.client.fast.PressHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
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
    Anchor githubLink = new Anchor("https://github.com/ashtonthomas/gwt-fast-touch-press");
    githubLink.addClickHandler(new ClickHandler() {
      @Override
      public void onClick(ClickEvent event) {
        Window.open("https://github.com/ashtonthomas/gwt-fast-touch-press", "_blank", "");
      }
    });

    final VerticalPanel debugPanel = new VerticalPanel();


    TestFastPressElement testButtonHello = new TestFastPressElement("Fast Hello");
    testButtonHello.addPressHandler(new PressHandler() {
      @Override
      public void onPress(PressEvent event) {
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
        debugPanel.add(new HTML(" Fast Hello Pressed! "));
      }
    });


    TestFastPressElement testButtonGoodBye = new TestFastPressElement("Fast Good By");
    testButtonGoodBye.addPressHandler(new PressHandler() {
      @Override
      public void onPress(PressEvent event) {
        debugPanel.add(new HTML(" Fast Good Bye! "));
      }
    });

    TestFastPressElement testButtonFoobar = new TestFastPressElement("Fast Foo Bar");
    testButtonFoobar.addPressHandler(new PressHandler() {
      @Override
      public void onPress(PressEvent event) {
        debugPanel.add(new HTML(" Fast Foo Bar! "));
      }
    });

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

    TestFastPressElement testButton5 = new TestFastPressElement("Fast Clear");
    testButton5.addPressHandler(new PressHandler() {
      @Override
      public void onPress(PressEvent event) {
        debugPanel.clear();
      }
    });

    // Add elements before adding to DOM for performance (?)
    content.add(title);
    content.add(githubLink);
    content.add(new HTML("<br/> You will need to view this in a mobile/toouch-based brwoser to see the difference. the Slow Buttons use a regular clickHandler. Rapidly touch fast buttons like using a calculator and then try with the slow buttons. <br/>"));

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
