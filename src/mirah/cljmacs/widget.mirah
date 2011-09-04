import org.eclipse.swt.*
import org.eclipse.swt.custom.*
import org.eclipse.swt.widgets.*

class Widget
  def initialize(tab_folder: CTabFolder, proxy: ControlProxy)
    tab_item = CTabItem.new tab_folder, SWT.CLOSE
    control = proxy.control(tab_folder, tab_item)
    tab_item.setControl(control)
    tab_folder.setSelection tab_item
  end
end

interface ControlProxy do
  def control(tab_folder: CTabFolder, tab_item: CTabItem): Control; end
end
