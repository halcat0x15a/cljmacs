(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [clojure.contrib.monads]
        [cljmacs.core])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]))

(def style (ref (bit-or SWT/MULTI SWT/BORDER)))

(defshortcut new-file-key [ctrl] \N)

(defshortcut open-key [ctrl] \O)

(defshortcut save-key [ctrl] \S)

(defshortcut save-as-key [ctrl shift] \S)

(defn editor
  ([#^Shell shell] (editor shell nil))
  ([#^Shell shell #^String path]
     (let [tabfolder (tabfolder shell)
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

(defn new-file [#^Shell shell] (editor shell))

(defn open [#^Shell shell]
  (let [dialog (FileDialog. shell SWT/OPEN)]
    (domonad maybe-m
             [path (.open dialog)]
             (editor shell path))))

(defn- save-text [#^StyledText text #^String path]
  (when-not (.getData text "saved")
    (spit path (.getText text))
    (.setData text "saved" true)))

(defn save-as [#^Shell shell]
  (let [dialog (FileDialog. shell SWT/SAVE)]
    (domonad maybe-m
             [path (.open dialog)
              tabitem (tabitem shell)
              text (.getControl tabitem)]
             (do
               (save-text text path)
               (.setData text "path" path)
               (.setText tabitem path)))))

(defn save [#^Shell shell]
  (let [text (control shell)]
    (if-let [path (.getData text "path")]
      (save-text text path)
      (save-as shell))))

(defn #^MenuManager filemenu [#^Shell shell]
  (doto (MenuManager. "&File")
    (.add (action "&New File\t" @new-file-key #(new-file shell)))
    (.add (action "&Open\t" @open-key #(open shell)))
    (.add (action "&Save\t" @save-key #(save shell)))
    (.add (action "&Save As\t" @save-as-key #(save-as shell)))))
