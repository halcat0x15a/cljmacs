(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]
           [cljmacs MenuItem]))

(defstyle editor-style SWT/MULTI SWT/BORDER)

(defshortcut new-file-key [ctrl] \N)

(defshortcut open-key [ctrl] \O)

(defshortcut save-key [ctrl] \S)

(defshortcut save-as-key [ctrl shift] \S)

(defwidget editor [path]
  (fn [tab-folder tab-item]
    (let [name (if (nil? path)
                 "Undefined"
                 path)
          string (if (nil? path)
                   ""
                   (slurp path))
          text (doto (StyledText. tab-folder @editor-style)
                 (.addVerifyListener
                  (proxy [VerifyListener] []
                    (verifyText [e]
                      (let [text (.widget e)]
                        (when (.getData text "saved")
                          (.setData text "saved" false))))))
                 (.setText string)
                 (.setData "saved" true)
                 (.setData "path" path))]
      (doto tab-item
        (.setText name))
      text)))

(defn new-file [] (editor nil))

(defn open []
  (let [dialog (FileDialog. (.shell @current-frame) SWT/OPEN)]
    (if-let [path (.open dialog)]
      (editor path))))

(defn- save-text [text path]
  (when-not (.getData text "saved")
    (spit path (.getText text))
    (.setData text "saved" true)))

(defn save-as []
  (let [dialog (FileDialog. (.shell @current-frame) SWT/SAVE)]
    (if-let [path (.open dialog)]
      (if (.isFile (file path))
        (let [tabitem (.tab-item @current-frame)
              text (.getControl tabitem)]
          (save-text text path)
          (.setData text "path" path)
          (.setText tabitem path))
        (message (str path " is not file."))))))

(defn save []
  (let [text (.control @current-frame)]
    (if-let [path (.getData text "path")]
      (save-text text path)
      (save-as))))

(defmenu file-menu "&File"
  (fn [menu]
    (MenuItem. menu "&New File" new-file @new-file-key)
    (MenuItem. menu "&Open" open @open-key)
    (MenuItem. menu "&Save" save @save-key)
    (MenuItem. menu "Save &As" save-as @save-as-key)))
