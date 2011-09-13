import java.io.*
import org.apache.commons.lang.*
import org.apache.commons.configuration.*

class Configuration
  def self.get_configuration
    if @config == nil
      path = SystemUtils.USER_HOME + "/" + ".cljmacs.properties"
      file = File.new(path)
      begin
        @config = PropertiesConfiguration.new(file)
        @config.setAutoSave(true)
      rescue ConfigurationException => e
        # error
      end
    else
      @config
    end
  end
end
