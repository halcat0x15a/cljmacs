import org.eclipse.swt.*
import org.eclipse.swt.custom.*
import org.eclipse.swt.widgets.*

class Widget
  def create(tab_folder: CTabFolder)
    tab_item = CTabItem.new tab_folder, SWT.CLOSE
    control = createControl(tab_folder, tab_item)
    tab_item.setControl(control)
    tab_folder.setSelection tab_item
  end
  def createControl(tab_folder: CTabFolder, tab_item: CTabItem): Control
  end
end
