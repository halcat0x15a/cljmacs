// Generated from util.mirah
package cljmacs;
@duby.anno.Extensions(macros={@duby.anno.Macro(name="attr_reader", signature="(Ljava/lang/Object;Ljava/lang/Object;)V", class="cljmacs.Util$Extension1"), @duby.anno.Macro(name="attr_writer", signature="(Ljava/lang/Object;Ljava/lang/Object;)V", class="cljmacs.Util$Extension2")})
public class Util extends java.lang.Object {
  private static org.apache.commons.configuration.PropertiesConfiguration config;
  private static org.apache.commons.configuration.PropertiesConfiguration create_config() {
    java.lang.String home = null;
    java.lang.String path = null;
    java.io.File file = null;
    org.apache.commons.configuration.PropertiesConfiguration config = null;
    home = java.lang.System.getProperty("user.home");
    path = ((home + "/") + ".cljmacs.properties");
    file = new java.io.File(((home + "/") + ".cljmacs.properties"));
    config = new org.apache.commons.configuration.PropertiesConfiguration(file);
    org.apache.commons.configuration.PropertiesConfiguration temp$1 = config;
    temp$1.setAutoSave(true);
    return temp$1;
  }
  public static org.apache.commons.configuration.PropertiesConfiguration getConfiguration() {
    if ((Util.config == null)) {
      Util.config = cljmacs.Util.create_config();
    }
    return Util.config;
  }
}
