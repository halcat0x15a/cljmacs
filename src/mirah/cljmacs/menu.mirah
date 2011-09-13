import org.eclipse.swt.*
import org.eclipse.swt.events.*
import org.eclipse.swt.widgets.Menu as SWTMenu
import org.eclipse.swt.widgets.*

class Menu
  def initialize(frame: Frame, text: String, index: int)
    shell = frame.shell
    @menu = SWTMenu.new shell, SWT.DROP_DOWN
    menu_bar = shell.getMenuBar
    menu_item = MenuItem.new menu_bar, SWT.CASCADE, index
    menu_item.setText text
    menu_item.setMenu @menu
  end

  def create_item(text: String, function: Runnable)
    menu_item = create_item function
    menu_item.setText text
  end

  def create_item(text: String, function: Runnable, shortcut_key: ShortcutKey)
    menu_item = create_item function
    menu_item.setText text + "\u0009" + shortcut_key.toString
    menu_item.setAccelerator shortcut_key.accelerator
  end

  def create_separator
    MenuItem.new @menu, SWT.SEPARATOR
  end

  private
  def create_item(function: Runnable)
    menu_item = MenuItem.new @menu, SWT.PUSH
    listener = Listener.new function
    menu_item.addSelectionListener listener
  end

  class Listener < SelectionAdapter
    def initialize(function: Runnable)
      @function = function
    end

    def widgetSelected(e: SelectionEvent)
      @function.run
    end
  end
end
