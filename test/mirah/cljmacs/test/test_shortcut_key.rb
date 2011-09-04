include Java

require 'test/unit'

java_import org.apache.commons.lang3.CharUtils
java_import org.eclipse.swt.SWT
java_import 'cljmacs.ModifierKey'
java_import 'cljmacs.ShortcutKey'

class TestShortcutKey < Test::Unit::TestCase
  def test_accelerator
    char = CharUtils.toChar("F")
    shortcut_key = ShortcutKey.new [ModifierKey.ctrl], char
    assert_equal SWT::CTRL + char, shortcut_key.accelerator
  end

  def test_to_string
    shortcut_key = ShortcutKey.new [ModifierKey.ctrl, ModifierKey.shift], CharUtils.toChar("S")
    assert_equal 'Ctrl+Shift+S', shortcut_key.toString
  end
end
