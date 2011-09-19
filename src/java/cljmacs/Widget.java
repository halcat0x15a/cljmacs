// Generated from widget.mirah
package cljmacs;
public abstract class Widget extends java.lang.Object {
  private cljmacs.Frame frame;
  private org.eclipse.swt.custom.CTabItem tab_item;
  private org.eclipse.swt.widgets.Control control;
  public  Widget(cljmacs.Frame frame) {
    this.frame = frame;
  }
  public void create() {
    org.eclipse.swt.custom.CTabFolder tab_folder = null;
    tab_folder = this.frame.tab_folder();
    this.tab_item = new org.eclipse.swt.custom.CTabItem(tab_folder, org.eclipse.swt.SWT.CLOSE);
    this.control = this.create_control(tab_folder, this.tab_item);
    org.eclipse.swt.custom.CTabItem temp$1 = this.tab_item;
    temp$1.setControl(this.control);
    org.eclipse.swt.custom.CTabFolder temp$2 = tab_folder;
    temp$2.setSelection(this.tab_item);
  }
  public org.eclipse.swt.widgets.Control control() {
    return this.control;
  }
  public org.eclipse.swt.custom.CTabItem tab_item() {
    return this.tab_item;
  }
  public org.eclipse.swt.widgets.Control create_control(org.eclipse.swt.custom.CTabFolder tab_folder, org.eclipse.swt.custom.CTabItem tab_item) {
    return null;
  }
}
