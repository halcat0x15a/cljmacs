(ns cljmacs.tab
  (:use [cljmacs.core :only (defshortcut ctrl alt shell tabitem action)])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabFolder2Adapter]
           [org.eclipse.swt.layout GridData]
           [org.eclipse.swt.widgets Shell]))

(defshortcut close-key [ctrl] \W)

(defshortcut quit-key [ctrl alt] \Q)

(defn #^CTabFolder tabfolder [#^Shell shell]
  (doto (CTabFolder. shell SWT/BORDER)
    (.addCTabFolder2Listener
     (proxy [CTabFolder2Adapter] []))
    (.setLayoutData (GridData. SWT/FILL, SWT/FILL, true, true))))

(defn close-tab [#^Shell shell]
  (.dispose (tabitem shell)))

(defn quit [#^Shell shell]
  (.close shell))

(defn #^MenuManager tabmenu [#^Shell shell]
  (doto (MenuManager. "&Tab")
    (.add (action "&Close\t" @close-key #(close-tab shell)))
    (.add (action "&Quit\t" @quit-key #(.close shell)))))

(defn #^MenuManager helpmenu [#^Shell shell]
  (MenuManager. "&Help"))
