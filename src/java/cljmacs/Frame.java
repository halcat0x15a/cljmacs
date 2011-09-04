// Generated from frame.mirah
package cljmacs;
public class Frame extends java.lang.Object {
  private static java.util.List SIZE;
  private static java.lang.String TITLE;
  private org.eclipse.swt.widgets.Shell shell;
  private org.eclipse.swt.widgets.Menu menu_bar;
  private org.eclipse.swt.custom.CTabFolder tab_folder;
  private org.eclipse.swt.widgets.Text text;
  public  Frame(org.eclipse.swt.widgets.Display display) {
    org.apache.commons.configuration.PropertiesConfiguration config = null;
    java.util.List size = null;
    this.SIZE = java.util.Collections.unmodifiableList(java.util.Arrays.asList(400, 300));
    this.TITLE = "cljmacs";
    config = cljmacs.Util.getConfiguration();
    this.shell = new org.eclipse.swt.widgets.Shell(display);
    org.eclipse.swt.widgets.Shell temp$1 = this.shell;
    temp$1.setLayout(new org.eclipse.swt.layout.GridLayout(1, false));
    org.eclipse.swt.widgets.Shell temp$2 = this.shell;
    temp$2.setText(config.getString("title", this.TITLE));
    size = config.getList("size", this.SIZE);
    org.eclipse.swt.widgets.Shell temp$3 = this.shell;
    temp$3.setSize(((java.lang.Integer)(size.get(0))).intValue(), ((java.lang.Integer)(size.get(1))).intValue());
    this.menu_bar = new org.eclipse.swt.widgets.Menu(this.shell, org.eclipse.swt.SWT.BAR);
    org.eclipse.swt.widgets.Shell temp$4 = this.shell;
    temp$4.setMenuBar(this.menu_bar);
    this.tab_folder = new org.eclipse.swt.custom.CTabFolder(this.shell, org.eclipse.swt.SWT.BORDER);
    org.eclipse.swt.custom.CTabFolder temp$5 = this.tab_folder;
    temp$5.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.SWT.FILL, org.eclipse.swt.SWT.FILL, true, true));
    org.eclipse.swt.custom.CTabFolder temp$6 = this.tab_folder;
    temp$6.setSimple(config.getBoolean("cljmacs.frame.simple"));
    this.text = new org.eclipse.swt.widgets.Text(this.shell, (org.eclipse.swt.SWT.SINGLE | org.eclipse.swt.SWT.BORDER));
    org.eclipse.swt.widgets.Text temp$7 = this.text;
    temp$7.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.SWT.FILL, org.eclipse.swt.SWT.END, true, false));
  }
  public org.eclipse.swt.widgets.Shell getShell() {
    return this.shell;
  }
  public org.eclipse.swt.widgets.Menu getMenuBar() {
    return this.menu_bar;
  }
  public org.eclipse.swt.custom.CTabFolder getTabFolder() {
    return this.tab_folder;
  }
  public org.eclipse.swt.widgets.Text getText() {
    return this.text;
  }
  public org.eclipse.swt.custom.CTabItem getTabItem() {
    return this.getTabFolder().getSelection();
  }
  public org.eclipse.swt.widgets.Control getControl() {
    return this.getTabItem().getControl();
  }
}
