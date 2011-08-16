(ns cljmacs.tab
  (:use [cljmacs.core :only (defshortcut ctrl alt shell tabitem action)])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabFolder2Adapter]
           [org.eclipse.swt.layout GridData]))

(defshortcut close-key [ctrl] \W)

(defshortcut quit-key [ctrl alt] \Q)

(defn tabfolder [parent]
  (doto (CTabFolder. parent SWT/BORDER)
    (.addCTabFolder2Listener
     (proxy [CTabFolder2Adapter] []))
    (.setLayoutData (GridData. SWT/FILL, SWT/FILL, true, true))))

(defn close-tab []
  (.dispose (tabitem)))

(defn quit []
  (.close (shell)))

(defn tabmenu []
  (doto (MenuManager. "&Tab")
    (.add (action "&Close\t" @close-key close-tab))
    (.add (action "&Quit\t" @quit-key quit))))

(defn helpmenu []
  (MenuManager. "&Help"))
