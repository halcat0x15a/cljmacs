// Generated from frame.mirah
package cljmacs;
public class Frame extends java.lang.Object {
  private org.eclipse.swt.widgets.Shell shell;
  private org.eclipse.swt.custom.CTabFolder tab_folder;
  private org.eclipse.swt.widgets.Text text;
  public  Frame(org.eclipse.swt.widgets.Shell shell, org.eclipse.swt.custom.CTabFolder tab_folder, org.eclipse.swt.widgets.Text text) {
    this.shell = shell;
    this.tab_folder = tab_folder;
    this.text = text;
  }
  public org.eclipse.swt.widgets.Shell shell() {
    return this.shell;
  }
  public org.eclipse.swt.custom.CTabFolder tab_folder() {
    return this.tab_folder;
  }
  public org.eclipse.swt.widgets.Text text() {
    return this.text;
  }
  public org.eclipse.swt.custom.CTabItem tab_item() {
    return this.tab_folder().getSelection();
  }
  public org.eclipse.swt.widgets.Control control() {
    org.eclipse.swt.custom.CTabItem tab_item = null;
    tab_item = this.tab_item();
    return (tab_item != null) ? (tab_item.getControl()) : (null);
  }
}
