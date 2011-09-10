(ns cljmacs.frame
  (:use [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.events SelectionAdapter]
           [cljmacs Frame]))

(defproperty title "cljmacs")

(defproperty size [400 300])

(defproperty simple false)

(defshortcut close \W ctrl)

(defshortcut quit \Q ctrl alt)

(defn close-tab []
  (let [tab-item (.tab_item (current-frame))]
    (.dispose tab-item)))

(defn quit []
  (.close (.shell (current-frame))))

(defn eval-text
  ([] (eval-text (.text (.command_line (current-frame)))))
  ([text]
     (let [value (try
                   (load-string (.getText text))
                   (catch Exception e e))]
       (.setText text (str value))
       (end-of-line text))))

(defn make-frame [display]
  (Frame. display eval-text))
