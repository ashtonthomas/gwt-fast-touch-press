package io.ashton.fastpress.client.fast;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;

/**
 * Event though we ignore clickEvents in FastPressElement given a touchEnd,
 * If we update the ui very quickly after a FastPressElement.onPress(pressEvent),
 * The browser may still generate a clickEvent where the fastPressElement was.
 *
 * If there is a different element that handles clickEvents, it fire on the incorrect
 * element (since it had to wait 300ms)
 *
 * We can use the FastPressClickBuster to eliminate this problem.
 *
 * @author ashton
 *
 */
public class FastPressClickBuster {
  private static ArrayList<Press> busters = new ArrayList<FastPressClickBuster.Press>();


  public static void preventDelayedGhostClick(int x, int y){

    final Press press = new Press(x, y);

    busters.add(press);

    Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {
      @Override
      public boolean execute() {
        busters.remove(press);
        return false;
      }
    }, 1000);

  }

  /**
   * -341,189
   * -330,194
   *
   * -170,272
   * @param click
   */
  public static void bustClick(ClickEvent click){
    for(Press p:busters){

      Window.alert("CHECK ("+p.getX()+","+p.getY()+") -- ("+click.getClientX()+","+click.getClientY()+")");

      if(click.getClientX() == p.getX() && click.getClientY() == p.getY()){
        click.preventDefault();
        click.stopPropagation();
        Window.alert("BUSTED A CLICK!");
      }

      if(click.getClientX() == p.getX()){
        click.preventDefault();
        click.stopPropagation();
        Window.alert("** TEMP -- **BUSTED A CLICK!");
      }
    }
  }

  private static class Press {
    private int x;
    private int y;
    private long timestamp;

    protected Press(int x, int y){
      this.x = x;
      this.y = y;
      Date now = new Date();
      timestamp = now.getTime();
    }

    public int getX(){
      return x;
    }

    public int getY(){
      return y;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
      result = prime * result + x;
      result = prime * result + y;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Press other = (Press) obj;
      if (timestamp != other.timestamp)
        return false;
      if (x != other.x)
        return false;
      if (y != other.y)
        return false;
      return true;
    }


  }

}
