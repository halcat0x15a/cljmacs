import java.io.*
import org.apache.commons.lang3.*
import org.apache.commons.configuration.*

class Utils
  def self.configuration
    if @config == nil
      @config = create_config
    end
    @config
  end

  private
  def self.create_config
    path = SystemUtils.USER_HOME + "/" + ".cljmacs.properties"
    file = File.new(path)
    config = PropertiesConfiguration.new(file)
    config.setAutoSave(true)
  end
end
