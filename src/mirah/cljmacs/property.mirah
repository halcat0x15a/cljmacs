class Property
  include Configuration

  def initialize(key: String, value: dynamic)
    @key = key
    config = get_configuration
    self.value = config.containsKey(key) ? config.getProperty(key) : value
  end

  def value
    @value
  end

  def value_set(value: dynamic): void
    get_configuration.setProperty @key, value
    @value = value
  end
end
