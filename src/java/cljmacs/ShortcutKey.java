// Generated from shortcut_key.mirah
package cljmacs;
public class ShortcutKey extends java.lang.Object {
  private cljmacs.ModifierKey[] modifiers;
  private char key_char;
  public static void main(java.lang.String[] argv) {
  }
  public  ShortcutKey(char key_char, cljmacs.ModifierKey[] modifiers) {
    this.modifiers = modifiers;
    this.key_char = key_char;
  }
  public int accelerator() {
    int a = 0;
    int __xform_tmp_1 = 0;
    cljmacs.ModifierKey[] __xform_tmp_2 = null;
    cljmacs.ModifierKey m = null;
    a = 0;
    __xform_tmp_1 = 0;
    __xform_tmp_2 = this.modifiers;
    label1:
    while ((__xform_tmp_1 < __xform_tmp_2.length)) {
      m = __xform_tmp_2[__xform_tmp_1];
      label2:
       {
        a = (a + m.key_code());
      }
      __xform_tmp_1 = (__xform_tmp_1 + 1);
    }
    return (a = (a + this.key_char));
  }
  @java.lang.Override()
  public java.lang.String toString() {
    java.lang.String str = null;
    int __xform_tmp_3 = 0;
    cljmacs.ModifierKey[] __xform_tmp_4 = null;
    cljmacs.ModifierKey m = null;
    str = "";
    __xform_tmp_3 = 0;
    __xform_tmp_4 = this.modifiers;
    label1:
    while ((__xform_tmp_3 < __xform_tmp_4.length)) {
      m = __xform_tmp_4[__xform_tmp_3];
      label2:
       {
        str = (str + m.toString());
        str = (str + "+");
      }
      __xform_tmp_3 = (__xform_tmp_3 + 1);
    }
    return (str = (str + this.key_char));
  }
}
