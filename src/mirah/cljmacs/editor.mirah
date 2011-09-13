import org.eclipse.swt.custom.*
/*
class Editor
  def initialize(tab_folder: CTabFolder, tab_item: CTabItem, path: String, string: String)
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
    @tab_item = tab_item
  end

  def self.current_editor
    @@editor
  end

  def save(path: String)
    @saved = true
    @path = path
    @tab_item.setText(path)
  end
end
*/
