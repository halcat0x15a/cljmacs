(ns cljmacs.frame
  (:use [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.layout GridLayout GridData]
           [org.eclipse.swt.widgets Shell Menu Text]
           [cljmacs Frame]))

(defproperty title "cljmacs")

(defproperty size [400 300])

(defproperty simple false)

(defshortcut close \W ctrl)

(defshortcut quit \Q ctrl alt)

(defun close-tab [frame]
  (.. frame tab_fodler getSelection dispose))

(defun quit [frame]
  (.. frame shell close))

(defn make-frame [display]
  (let [[width height] @size
        shell (doto (Shell. display)
                (.setLayout (GridLayout. 1 false))
                (.setText @title)
                (.setSize width height))
        menu-bar (Menu. shell SWT/BAR)
        tab-folder (doto (CTabFolder. shell SWT/BORDER)
                     (.setLayoutData (GridData. SWT/FILL SWT/FILL true true))
                     (.setSimple @simple))
        text (doto (Text. shell (bit-or SWT/SINGLE SWT/BORDER))
               (.setLayoutData (GridData. SWT/FILL SWT/END true false)))
        frame (Frame. shell tab-folder text)]
    (.setMenuBar shell menu-bar)
    (.addSelectionListener text (proxy [SelectionAdapter] []
                                  (widgetDefaultSelected [e]
                                    (eval-text frame))))
    frame))
