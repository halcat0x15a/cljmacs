(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [cljmacs.core])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]))

(defconfig style (bit-or SWT/MULTI SWT/BORDER) integer?)

(defshortcut new-file-key [ctrl] \N)

(defshortcut open-key [ctrl] \O)

(defshortcut save-key [ctrl] \S)

(defshortcut save-as-key [ctrl shift] \S)

(defn editor
  ([path] (editor (shell) path))
  ([shell path]
     (let [tabfolder (tabfolder shell)
           a (println tabfolder)
           tabitem (CTabItem. tabfolder SWT/CLOSE)
           name (if (nil? path)
                  "Undefined"
                  path)
           string (if (nil? path)
                    ""
                    (slurp path))
           text (doto (StyledText. tabfolder @style)
                  (.addVerifyListener
                   (proxy [VerifyListener] []
                     (verifyText [e]
                       (let [text (.widget e)]
                         (when (.getData text "saved")
                           (.setData text "saved" false))))))
                  (.setText string)
                  (.setData "saved" true)
                  (.setData "path" path))]
       (doto tabitem
         (.setText name)
         (.setControl text))

       (.setSelection tabfolder tabitem))))

(defn new-file [] (editor nil))

(defn open []
  (let [shell (shell)
        dialog (FileDialog. shell SWT/OPEN)]
    (if-let [path (.open dialog)]
      (editor shell path))))

(defn- save-text [text path]
  (when-not (.getData text "saved")
    (spit path (.getText text))
    (.setData text "saved" true)))

(defn save-as []
  (let [shell (shell)
        dialog (FileDialog. shell SWT/SAVE)]
    (if-let [path (.open dialog)]
      (let [tabitem (tabitem shell)
            text (.getControl tabitem)]
        (save-text text path)
        (.setData text "path" path)
        (.setText tabitem path)))))

(defn save []
  (let [shell (shell)
        text (control shell)]
    (if-let [path (.getData text "path")]
      (save-text text path)
      (save-as shell))))

(defn filemenu []
  (doto (MenuManager. "&File")
    (.add (action "&New File\t" new-file @new-file-key))
    (.add (action "&Open\t" open @open-key))
    (.add (action "&Save\t" save @save-key))
    (.add (action "&Save As\t" save-as @save-as-key))))
