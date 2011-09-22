class Property
  include Configuration

  def initialize(key: String, type: String, value: dynamic)
    @key = key
    config = get_configuration
    self.value = config.containsKey(key) ? config.getProperty(key) : value
  end

  def property(key: String, type: String): dynamic
    config = get_configuration
    if key == :int
      config.getInteger(key, nil)
    else
      config.getProperty(key)
    end
  end

  def value
    @value
  end

  def value_set(value: dynamic): void
    get_configuration.setProperty @key, value
    @value = value
  end
end
