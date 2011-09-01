// Generated from frame.mirah
package cljmacs;
public class Frame extends java.lang.Object {
  private org.eclipse.swt.widgets.Display display;
  private org.eclipse.swt.widgets.Shell shell;
  private org.eclipse.swt.widgets.Menu menu_bar;
  private org.eclipse.swt.custom.CTabFolder tab_folder;
  private org.eclipse.swt.widgets.Text text;
  public  Frame(cljmacs.FrameListener listener) {
    this.display = new org.eclipse.swt.widgets.Display();
    this.shell = new org.eclipse.swt.widgets.Shell(this.display);
    org.eclipse.swt.widgets.Shell temp$1 = this.shell;
    temp$1.setLayout(new org.eclipse.swt.layout.GridLayout(1, false));
    this.menu_bar = new org.eclipse.swt.widgets.Menu(this.shell, org.eclipse.swt.SWT.BAR);
    org.eclipse.swt.widgets.Shell temp$2 = this.shell;
    temp$2.setMenuBar(this.menu_bar);
    this.tab_folder = new org.eclipse.swt.custom.CTabFolder(this.shell, org.eclipse.swt.SWT.BORDER);
    org.eclipse.swt.custom.CTabFolder temp$3 = this.tab_folder;
    temp$3.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.SWT.FILL, org.eclipse.swt.SWT.FILL, true, true));
    this.text = new org.eclipse.swt.widgets.Text(this.shell, (org.eclipse.swt.SWT.SINGLE | org.eclipse.swt.SWT.BORDER));
    org.eclipse.swt.widgets.Text temp$4 = this.text;
    temp$4.addSelectionListener(listener);
    org.eclipse.swt.widgets.Text temp$5 = this.text;
    temp$5.setLayoutData(new org.eclipse.swt.layout.GridData(org.eclipse.swt.SWT.FILL, org.eclipse.swt.SWT.END, true, false));
  }
  public org.eclipse.swt.widgets.Shell quit() {
    org.eclipse.swt.widgets.Shell temp$1 = this.shell;
    temp$1.close();
    return temp$1;
  }
}
