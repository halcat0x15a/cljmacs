// Generated from property.mirah
package cljmacs;
public class Property extends java.lang.Object {
  private java.lang.String key;
  private java.lang.Object value;
  public static void main(java.lang.String[] argv) {
  }
  public  Property(java.lang.String key, java.lang.Object value) {
    org.apache.commons.configuration.PropertiesConfiguration config = null;
    this.key = key;
    config = cljmacs.Configuration.get_configuration();
    cljmacs.Property temp$1 = this;
    temp$1.value_set(config.containsKey(key) ? (config.getProperty(key)) : (value));
  }
  public java.lang.Object value() {
    return this.value;
  }
  public void value_set(java.lang.Object value) {
    org.apache.commons.configuration.PropertiesConfiguration temp$1 = cljmacs.Configuration.get_configuration();
    temp$1.setProperty(this.key, value);
    this.value = value;
  }
}
