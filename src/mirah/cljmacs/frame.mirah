import org.eclipse.swt.*
import org.eclipse.swt.custom.*
import org.eclipse.swt.events.*
import org.eclipse.swt.layout.*
import org.eclipse.swt.widgets.Menu as SWTMenu
import org.eclipse.swt.widgets.*

class Frame
  implements Macros

  attr_reader :shell
  attr_reader :menu_bar
  attr_reader :tab_folder
  attr_reader :text

  def initialize(display: Display)
    config = Utils.configuration
    @shell = Shell.new display
    @shell.setLayout GridLayout.new 1, false
    @menu_bar = SWTMenu.new @shell, SWT.BAR
    @shell.setMenuBar @menu_bar
    @tab_folder = CTabFolder.new @shell, SWT.BORDER
    @tab_folder.setLayoutData GridData.new SWT.FILL, SWT.FILL, true, true
    @text = Text.new @shell, SWT.SINGLE | SWT.BORDER
    @text.setLayoutData GridData.new SWT.FILL, SWT.END, true, false
  end

  def tab_item
    tab_folder.getSelection
  end

  def control
    tab_item.getControl
  end
end
