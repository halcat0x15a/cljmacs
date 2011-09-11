(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]
           [cljmacs Editor]))

(defstyle editor-style SWT/MULTI SWT/BORDER)

(defshortcut new-file-key \N ctrl)

(defshortcut open-key \O ctrl)

(defshortcut save-key \S ctrl)

(defshortcut save-as-key \S ctrl shift)

(defshortcut cut-key \X ctrl)

(defshortcut copy-key \C ctrl)

(defshortcut paste-key \V ctrl)

(defshortcut select-all-key \A ctrl)

(defn current-editor [] (Editor/current-editor))

(defwidget new-editor [path]
  (fn [tab-folder tab-item]
    (let [name (if (nil? path)
                 "Undefined"
                 path)
          string (if (nil? path)
                   ""
                   (slurp path))
          editor (Editor. tab-folder tab-item path string)
          text (.text editor)]
      (doto tab-item
        (.setText name))
      text)))

(defn new-file [] (new-editor nil))

(defn open []
  (let [dialog (FileDialog. (.shell (current-frame)) SWT/OPEN)]
    (if-let [path (.open dialog)]
      (new-editor path))))

(defn- save-text [editor path]
  (when-not (.saved editor)
    (spit path (.getText (.text editor)))
    (.save editor)))

(defn save-as []
  (let [dialog (FileDialog. (.shell (current-frame)) SWT/SAVE)]
    (if-let [path (.open dialog)]
      (let [file (file path)
            editor (current-editor)
            save (fn []
                   (save-text editor path)
                   (.path_set editor path))]
        (if (.exists file)
          (if (.isFile file)
            (save)
            (message (str path " is not file.")))
          (save))))))

(defn save []
  (let [editor (current-editor)]
    (if-let [path (.path editor)]
      (save-text editor path)
      (save-as))))

(defmacro defaction [name action]
  `(defn ~name []
     (.. (current-editor) text ~action)))

(defaction cut cut)

(defaction copy copy)

(defaction paste paste)

(defaction select-all selectAll)

(defmenu file-menu "&File"
  (fn [menu]
    (make-menu-item menu "&New File" new-file new-file-key)
    (make-menu-item menu "&Open" open open-key)
    (make-separator menu)
    (make-menu-item menu "&Save" save save-key)
    (make-menu-item menu "Save &As" save-as save-as-key)))

(defmenu edit-menu "&Edit"
  (fn [menu]
    (make-menu-item menu "&Cut" cut cut-key)
    (make-menu-item menu "&Copy" copy copy-key)
    (make-menu-item menu "&Paste" paste paste-key)
    (make-separator menu)
    (make-menu-item menu "&Select All" select-all select-all-key)))
