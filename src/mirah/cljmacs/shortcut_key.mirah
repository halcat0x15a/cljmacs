class ShortcutKey
  def initialize(modifiers: ModifierKey[], key_char: char)
    @modifiers = modifiers
    @key_char = key_char
  end

  def accelerator
    a = 0
    @modifiers.each do |m|
      a += m.key_code
    end
    a += @key_char
  end

  $Override
  def toString
    str = ''
    @modifiers.each do |m|
      str += m.toString
      str += '+'
    end
    str += @key_char
  end
end
