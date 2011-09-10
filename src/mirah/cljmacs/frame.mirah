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
  attr_reader :command_line

  def initialize(display: Display, eval_fn: Runnable)
    raise Exception.new if @frame != nil
    @frame = self
    ns = "cljmacs.frame."
    config = Configuration.get_configuration
    @shell = Shell.new display
    @shell.setLayout GridLayout.new 1, false
    @shell.setText config.getString ns + "title"
    array = config.getStringArray ns + "size"
    size = int[2]
    0.upto(1) { |i| size[i] = Integer.parseInt(array[i]) }
    @shell.setSize size[0], size[1]
    @menu_bar = SWTMenu.new @shell, SWT.BAR
    @shell.setMenuBar @menu_bar
    @tab_folder = CTabFolder.new @shell, SWT.BORDER
    @tab_folder.setLayoutData GridData.new SWT.FILL, SWT.FILL, true, true
    @tab_folder.setSimple config.getBoolean ns + "simple"
    @command_line = CommandLine.new shell, eval_fn
  end

  def self.current_frame
    @frame
  end

  def tab_item
    tab_folder.getSelection
  end

  def control
    tab_item.getControl
  end

end
