// Generated from modifier_key.mirah
package cljmacs;
public class ModifierKey extends java.lang.Object {
  private int key_code;
  private java.lang.String string;
  public int key_code() {
    return this.key_code;
  }
  @java.lang.Override()
  public java.lang.String toString() {
    return this.string;
  }
  public static cljmacs.ModifierKey ctrl() {
    return new cljmacs.ModifierKey(org.eclipse.swt.SWT.CTRL, "Ctrl");
  }
  public static cljmacs.ModifierKey alt() {
    return new cljmacs.ModifierKey(org.eclipse.swt.SWT.ALT, "Alt");
  }
  public static cljmacs.ModifierKey shift() {
    return new cljmacs.ModifierKey(org.eclipse.swt.SWT.SHIFT, "Shift");
  }
  private  ModifierKey(int key_code, java.lang.String string) {
    this.key_code = key_code;
    this.string = string;
  }
}
