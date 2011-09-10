import org.eclipse.swt.*
import org.eclipse.swt.layout.*
import org.eclipse.swt.widgets.*
import org.eclipse.swt.events.*

class CommandLine
  implements Macros

  attr_reader :text

  def initialize(shell: Shell, eval_fn: Runnable)
    @text = Text.new shell, SWT.SINGLE | SWT.BORDER
    @text.setLayoutData GridData.new SWT.FILL, SWT.END, true, false
    @text.addSelectionListener do
      def widgetSelected(e: SelectionEvent)
      end
      def widgetDefaultSelected(e: SelectionEvent)
        eval_fn.run
      end
    end
  end
end
