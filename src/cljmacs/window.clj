(ns cljmacs.window
  (:use [cljmacs.core :only (defconfig)]
        [cljmacs.tab :only (tabfolder tabmenu helpmenu)]
        [cljmacs.text]
        [cljmacs.editor :only (filemenu)]
        [cljmacs.browser :only (browsermenu)]
        [cljmacs.twitter :only (twittermenu)])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT SWTError]
           [org.eclipse.swt.layout GridLayout]
           [org.eclipse.jface.window ApplicationWindow]
           [org.eclipse.swt.widgets Display Shell]))

(defconfig title "cljmacs" string?)

(defconfig size [400 300] vector?)

(defn window []
  (doto (proxy [ApplicationWindow] [nil]
          (configureShell [shell]
            (let [[width height] @size]
              (doto shell
                (.setText @title)
                (.setSize width height))
              (proxy-super configureShell shell)))
          (createContents [parent]
            (.setLayout parent (GridLayout. 1 false))
            (tabfolder parent)
            (text parent)
            parent)
          (createMenuManager []
            (let [shell (proxy-super getShell)]
              (doto (MenuManager.)
                (.add (filemenu shell))
                (.add (tabmenu shell))
                (.add (browsermenu shell))
                (.add (twittermenu shell))))))
    (.addMenuBar)
    (.setBlockOnOpen true)
    (.open))
  (.dispose (Display/getCurrent)))
