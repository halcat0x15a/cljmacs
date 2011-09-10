class Property
  implements Macros

  attr_reader :value

  def initialize(key: String, value: dynamic)
    @key = key
    value_set value
  end

  def value=(value: dynamic): void
    Configuration.get_configuration.setProperty @key, value
    @value = value
  end
end
