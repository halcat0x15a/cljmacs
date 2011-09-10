(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]
           [cljmacs MenuItem Editor]))

(defstyle editor-style SWT/MULTI SWT/BORDER)

(defshortcut new-file-key \N ctrl)

(defshortcut open-key \O ctrl)

(defshortcut save-key \S ctrl)

(defshortcut save-as-key \S ctrl shift)

(defshortcut cut-key \X ctrl)

(defshortcut copy-key \C ctrl)

(defshortcut paste-key \V ctrl)

(defn current-editor [] (Editor/current-editor))

(defwidget new-editor [path]
  (fn [tab-folder tab-item]
    (let [name (if (nil? path)
                 "Undefined"
                 path)
          string (if (nil? path)
                   ""
                   (slurp path))
          editor (Editor. tab-folder path string)
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
    (prn "hello")
    (spit path (.getText (.text editor)))
    (.saved_set editor true)))

(defn save-as []
  (let [dialog (FileDialog. (.shell (current-frame)) SWT/SAVE)]
    (if-let [path (.open dialog)]
      (if (.isFile (file path))
        (let [tabitem (.tab_item (current-frame))
              editor (current-editor)]
          (save-text editor path)
          (.path_set editor path)
          (.setText tabitem path))
        (message (str path " is not file."))))))

(defn save []
  (let [editor (current-editor)]
    (if-let [path (.path editor)]
      (save-text editor path)
      (save-as))))

(defmacro defaction [action]
  `(defn ~action []
     (.. (current-editor) text ~action)))

(defaction cut)

(defaction copy)

(defaction paste)

(defmenu file-menu "&File"
  (fn [menu]
    (make-menu-item menu "&New File" new-file new-file-key)
    (make-menu-item menu "&Open" open open-key)
    (make-menu-item menu "&Save" save save-key)
    (make-menu-item menu "Save &As" save-as save-as-key)))

(defmenu edit-menu "&Edit"
  (fn [menu]
    (make-menu-item menu "&Cut" cut cut-key)
    (make-menu-item menu "&Copy" copy copy-key)
    (make-menu-item menu "&Paste" paste paste-key)))
