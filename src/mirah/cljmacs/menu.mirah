import org.eclipse.swt.*
import org.eclipse.swt.widgets.Menu as SWTMenu
import org.eclipse.swt.widgets.MenuItem as SWTMenuItem
import org.eclipse.swt.widgets.*

class Menu

  def initialize(frame: Frame, index: int, text: String, proxy: MenuProxy)
    shell = frame.shell
    menu = SWTMenu.new shell, SWT.DROP_DOWN
    menu_bar = frame.menu_bar
    menu_item = SWTMenuItem.new menu_bar, SWT.CASCADE, index
    menu_item.setText text
    menu_item.setMenu menu
    proxy.create(menu)
  end
end

interface MenuProxy do
  def create(menu: SWTMenu): void;end
end
