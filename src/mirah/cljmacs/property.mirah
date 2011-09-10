class Property
  implements Macros

  attr_reader :value

  def initialize(key: String, value: dynamic)
    @key = key
    self.value = value
  end

  def value_set(value: dynamic): void
    Configuration.get_configuration.setProperty @key, value
    @value = value
  end
end
