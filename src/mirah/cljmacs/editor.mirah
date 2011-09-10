import org.eclipse.swt.custom.*

class Editor
  implements Macros

  attr_reader :text
  attr_accessor :saved, :boolean
  attr_accessor :path, :String

  def initialize(tab_folder: CTabFolder, path: String, string: String)
    @@editor = self
    @saved = true
    @path = path
    config = Configuration.get_configuration
    key = "cljmacs.editor."
    @text = StyledText.new tab_folder, config.getInt(key + "editor-style")
    @text.setText string
    @text.addVerifyListener do |e|
      Editor.current_editor.saved = false
    end
  end

  def self.current_editor
    @@editor
  end
end
