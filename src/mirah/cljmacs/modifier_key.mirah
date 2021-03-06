import org.eclipse.swt.SWT

class ModifierKey
  def key_code
    @key_code
  end

  $Override
  def toString
    @string
  end

  def self.ctrl
    ModifierKey.new SWT.CTRL, 'Ctrl'
  end

  def self.alt
    ModifierKey.new SWT.ALT, 'Alt'
  end

  def self.shift
    ModifierKey.new SWT.SHIFT, 'Shift'
  end

  private
  def initialize(key_code: int, string: String)
    @key_code = key_code
    @string = string
  end
end
