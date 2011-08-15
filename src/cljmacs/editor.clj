(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [clojure.contrib.monads]
        [cljmacs.core])
  (:import [clojure.lang Ref]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]))

(def #^Ref style (ref (bit-or SWT/MULTI SWT/BORDER)))

(def #^Ref saved (ref true))

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
                       (when @saved
                         (dosync
                          (ref-set saved false))))))
                  (.setText string))]
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

(defn save [#^Shell shell]
  (let [text (control shell)]))
    

(defn save-as [#^Shell shell]
  (let [dialog (FileDialog. shell SWT/SAVE)]
    (domonad maybe-m
             [path (.open dialog)
              string (.getText (control shell))]
             (spit path string))))
