import org.eclipse.swt.*
import org.eclipse.swt.widgets.Menu as SWTMenu
import org.eclipse.swt.widgets.MenuItem as SWTMenuItem
import org.eclipse.swt.widgets.*

class Menu
  def create(frame: Frame, index: int, text: String): void
    shell = frame.shell
    menu = SWTMenu.new shell, SWT.DROP_DOWN
    menu_bar = shell.getMenuBar
    menu_item = SWTMenuItem.new menu_bar, SWT.CASCADE, index
    menu_item.setText text
    menu_item.setMenu menu
    createMenu(menu)
  end
  def createMenu(menu: SWTMenu): void
  end
end
