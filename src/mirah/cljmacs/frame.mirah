import org.eclipse.swt.*
import org.eclipse.swt.custom.*
import org.eclipse.swt.layout.*
import org.eclipse.swt.widgets.*

class Frame
  def initialize()
    @display = Display.new
    @shell = Shell.new(@display)
    @shell.setLayout(GridLayout.new(1, false))
    @menu_bar = Menu.new(@shell, SWT.BAR)
    @shell.setMenuBar(@menu_bar)
    @tab_folder = CTabFolder.new(@shell, SWT.BORDER)
    @tab_folder.setLayoutData(GridData.new(SWT.FILL, SWT.FILL, true, true))
    @simple = false
    @tab_folder.setSimple(@simple)
    @text = Text.new(@shell, SWT.SINGLE | SWT.BORDER)
    @text.setLayoutData(GridData.new(SWT.FILL, SWT.END, true, false))
  end
end
