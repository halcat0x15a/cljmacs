(ns cljmacs.frame
  (:use [cljmacs.core]
        [cljmacs.menu])
  (:import [clojure.lang Ref]
           [org.eclipse.swt SWT SWTError]
           [org.eclipse.swt.custom CTabFolder CTabItem CTabFolder2Adapter]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.layout GridLayout GridData]
           [org.eclipse.swt.widgets Display Shell Text]))

(def #^Ref title (ref "cljmacs" :validator string?))

(def #^Ref size (ref [400 300] :validator vector?))

(defn eval-text [#^Text text]
  (let [s (try
            (load-string (.getText text))
            (catch Exception e e))]
    (doto text
      (.setText (str s))
      (.setSelection (.getCharCount text)))
    nil))

(defn new-frame []
  (let [display (Display.)]
    (try
      (let [[width height] @size
            shell (doto (Shell. display)
                    (.setLayout (GridLayout. 1 false))
                    (.setText @title)
                    (.setSize width height))
            folder (doto (CTabFolder. shell SWT/BORDER)
                     (.addCTabFolder2Listener
                      (proxy [CTabFolder2Adapter] []))
                     (.setLayoutData (GridData. SWT/FILL, SWT/FILL, true, true)))
            text (doto (Text. shell (bit-or SWT/SINGLE SWT/BORDER))
                   (.addSelectionListener
                    (proxy [SelectionAdapter] []
                      (widgetDefaultSelected [e]
                        (eval-text (.widget e)))))
                   (.setLayoutData (GridData. SWT/FILL, SWT/END, true, false)))]
        (doto shell
          (.setMenuBar (menubar shell))
          (.open))
        (while (not (.isDisposed shell))
          (when-not (.readAndDispatch display)
            (.sleep display))))
      (finally
       (.dispose display)))))
