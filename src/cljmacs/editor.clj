(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]))

(defstyle editor-style SWT/MULTI SWT/BORDER)

(defshortcut new-file-key [ctrl] \N)

(defshortcut open-key [ctrl] \O)

(defshortcut save-key [ctrl] \S)

(defshortcut save-as-key [ctrl shift] \S)

(defwidget editor [path]
  (fn [tab-folder tabitem]
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
      [text name])))

(defn new-file [] (editor nil))

(defn open []
  (let [dialog (FileDialog. @shell SWT/OPEN)]
    (if-let [path (.open dialog)]
      (editor path))))

(defn- save-text [text path]
  (when-not (.getData text "saved")
    (spit path (.getText text))
    (.setData text "saved" true)))

(defn save-as []
  (let [dialog (FileDialog. @shell SWT/SAVE)]
    (if-let [path (.open dialog)]
      (let [tabitem (.getSelection (tab-folder))
            text (.getControl tabitem)]
        (save-text text path)
        (.setData text "path" path)
        (.setText tabitem path)))))

(defn save []
  (let [text (.getControl (.getSelection (tab-folder)))]
    (if-let [path (.getData text "path")]
      (save-text text path)
      (save-as))))

(defmenu file-menu "&File"
  (fn [menu]
    (make-menu-item menu "&New File" new-file @new-file-key)
    (make-menu-item menu "&Open" open @open-key)
    (make-menu-item menu "&Save" save @save-key)
    (make-menu-item menu "Save &As" save-as @save-as-key)))
