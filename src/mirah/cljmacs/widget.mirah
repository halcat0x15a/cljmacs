import org.eclipse.swt.*
import org.eclipse.swt.custom.*
import org.eclipse.swt.widgets.*

abstract class Widget
  def initialize(tab_folder: CTabFolder)
    @tab_folder = tab_folder
  end

  def tab_item
    @tab_item
  end

  def control
    @control
  end

  def create: void
    @tab_item = CTabItem.new @tab_folder, SWT.CLOSE
    @control = create_control @tab_folder, @tab_item
    tab_item.setControl @control
    @tab_folder.setSelection @tab_item
  end

  abstract def create_control(tab_folder: CTabFolder, tab_item: CTabItem): Control
  end
end
