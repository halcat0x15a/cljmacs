import org.eclipse.swt.custom.*
import org.eclipse.swt.widgets.*

class Frame
  def initialize(shell: Shell, tab_folder: CTabFolder, text: Text)
    @shell = shell
    @tab_folder = tab_folder
    @text = text
  end

  def shell
    @shell
  end

  def tab_folder
    @tab_folder
  end

  def text
    @text
  end

  def control
    tab_item = @tab_folder.getSelection
    tab_item.getControl if tab_item != nil
  end
end
