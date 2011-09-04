import org.eclipse.swt.*
import org.eclipse.swt.events.*
import org.eclipse.swt.widgets.Menu as SWTMenu
import org.eclipse.swt.widgets.MenuItem as SWTMenuItem
import org.eclipse.swt.widgets.*

class MenuItem
  def initialize(menu: SWTMenu, runnable: Runnable)
    @menu_item = SWTMenuItem.new menu, SWT.PUSH
    @menu_item.addSelectionListener do
      def widgetSelected(e: SelectionEvent)
        runnable.run
      end
      def widgetDefaultSelected(e: SelectionEvent)
      end
    end
  end

  def initialize(menu: SWTMenu, text: String, runnable: Runnable)
    initialize(menu, runnable)
    @menu_item.setText text
  end

  def initialize(menu: SWTMenu, text: String, runnable: Runnable, shortcut_key: ShortcutKey)
    initialize(menu, runnable)
    @menu_item.setText text + "\u0009" + shortcut_key.toString
    @menu_item.setAccelerator shortcut_key.accelerator
  end

  def self.separator(menu: SWTMenu)
    SWTMenuItem.new menu, SWT.SEPARATOR
  end
end
