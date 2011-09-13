// Generated from menu.mirah
package cljmacs;
public class Menu extends java.lang.Object {
  private org.eclipse.swt.widgets.Menu menu;
  public  Menu(cljmacs.Frame frame, java.lang.String text, int index) {
    org.eclipse.swt.widgets.Shell shell = null;
    org.eclipse.swt.widgets.Menu menu_bar = null;
    org.eclipse.swt.widgets.MenuItem menu_item = null;
    shell = frame.shell();
    this.menu = new org.eclipse.swt.widgets.Menu(shell, org.eclipse.swt.SWT.DROP_DOWN);
    menu_bar = shell.getMenuBar();
    menu_item = new org.eclipse.swt.widgets.MenuItem(menu_bar, org.eclipse.swt.SWT.CASCADE, index);
    org.eclipse.swt.widgets.MenuItem temp$1 = menu_item;
    temp$1.setText(text);
    org.eclipse.swt.widgets.MenuItem temp$2 = menu_item;
    temp$2.setMenu(this.menu);
  }
  public org.eclipse.swt.widgets.MenuItem create_item(java.lang.String text, java.lang.Runnable function) {
    org.eclipse.swt.widgets.MenuItem menu_item = null;
    menu_item = this.create_item(function);
    org.eclipse.swt.widgets.MenuItem temp$1 = menu_item;
    temp$1.setText(text);
    return temp$1;
  }
  public org.eclipse.swt.widgets.MenuItem create_item(java.lang.String text, java.lang.Runnable function, cljmacs.ShortcutKey shortcut_key) {
    org.eclipse.swt.widgets.MenuItem menu_item = null;
    menu_item = this.create_item(function);
    org.eclipse.swt.widgets.MenuItem temp$1 = menu_item;
    temp$1.setText(((text + "\t") + shortcut_key.toString()));
    org.eclipse.swt.widgets.MenuItem temp$2 = menu_item;
    temp$2.setAccelerator(shortcut_key.accelerator());
    return temp$2;
  }
  public org.eclipse.swt.widgets.MenuItem create_separator() {
    return new org.eclipse.swt.widgets.MenuItem(this.menu, org.eclipse.swt.SWT.SEPARATOR);
  }
  private org.eclipse.swt.widgets.MenuItem create_item(java.lang.Runnable function) {
    org.eclipse.swt.widgets.MenuItem menu_item = null;
    cljmacs.Listener listener = null;
    menu_item = new org.eclipse.swt.widgets.MenuItem(this.menu, org.eclipse.swt.SWT.PUSH);
    listener = new cljmacs.Listener(function);
    org.eclipse.swt.widgets.MenuItem temp$1 = menu_item;
    temp$1.addSelectionListener(listener);
    return temp$1;
  }
}
