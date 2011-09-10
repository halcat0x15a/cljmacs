import java.io.*
import org.apache.commons.lang.*
import org.apache.commons.configuration.*

class Configuration
  def self.get_configuration
    if @config == nil
      path = SystemUtils.USER_HOME + "/" + ".cljmacs.properties"
      file = File.new(path)
      @config = PropertiesConfiguration.new(file)
      @config.setAutoSave(true)
    else
      @config
    end
  end
end
