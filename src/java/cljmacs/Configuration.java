// Generated from configuration.mirah
package cljmacs;
public class Configuration extends java.lang.Object {
  private static org.apache.commons.configuration.PropertiesConfiguration config;
  public static org.apache.commons.configuration.PropertiesConfiguration get_configuration() {
    java.lang.String path = null;
    java.io.File file = null;
    if ((Configuration.config == null)) {
      path = ((org.apache.commons.lang.SystemUtils.USER_HOME + "/") + ".cljmacs.properties");
      file = new java.io.File(path);
      try {
        Configuration.config = new org.apache.commons.configuration.PropertiesConfiguration(file);
        org.apache.commons.configuration.PropertiesConfiguration temp$1 = Configuration.config;
        temp$1.setAutoSave(true);
        return temp$1;
      }
      catch (org.apache.commons.configuration.ConfigurationException e$2020) {
        return null;
      }
    }
    else {
      return Configuration.config;
    }
  }
}
