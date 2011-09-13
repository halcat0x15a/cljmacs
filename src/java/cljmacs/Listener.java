// Generated from menu.mirah
package cljmacs;
public class Listener extends org.eclipse.swt.events.SelectionAdapter {
  private java.lang.Runnable function;
  public  Listener(java.lang.Runnable function) {
    this.function = function;
  }
  public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
    java.lang.Runnable temp$1 = this.function;
    temp$1.run();
  }
}
