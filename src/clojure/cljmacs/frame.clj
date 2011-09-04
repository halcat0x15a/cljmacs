(ns cljmacs.frame
  (:use [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.events SelectionAdapter]
           [cljmacs Frame]))

(defconfig title "cljmacs")

(defconfig size [400 300])

(defconfig simple false)

(defshortcut close-key [ctrl] \W)

(defshortcut quit-key [ctrl alt] \Q)

(defn close-tab []
  (.dispose (.tab_item @current-frame)))

(defn quit []
  (.close (.shell @current-frame)))

(defn eval-text [text]
  (let [x (try
            (load-string (.getText text))
            (catch Exception e e))]
    (doto text
      (.setText (str x))
      (.setSelection (.getCharCount text)))
    nil))

(defn make-frame [display]
  (dosync
   (let [frame (Frame. display)
         shell (.shell frame)
         [width height] @size
         tab-folder (.tab_folder frame)
         text (.text frame)]
     (doto shell
       (.setText @title)
       (.setSize width height))
     (doto tab-folder
       (.setSimple @simple))
     (doto text
       (.addSelectionListener (proxy [SelectionAdapter] []
                                (widgetDefaultSelected [e]
                                  (eval-text (.widget e))))))
     (ref-set current-frame frame))))
