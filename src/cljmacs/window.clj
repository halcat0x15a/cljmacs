(ns cljmacs.window
  (:use [cljmacs.core :only (defconfig)]
        [cljmacs.tab :only (tabfolder tabmenu helpmenu)]
        [cljmacs.text :only (text)]
        [cljmacs.editor :only (filemenu)]
        [cljmacs.browser :only (browsermenu)]
        [cljmacs.twitter :only (twittermenu)])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT SWTError]
           [org.eclipse.swt.layout GridLayout]
           [org.eclipse.jface.window ApplicationWindow]
           [org.eclipse.swt.widgets Display Shell Composite]))

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
            (let [tabfolder (tabfolder parent)
                  text (text parent)]
              (doto parent
                (.setData "tabfolder" tabfolder)
                (.setData "text" text))))
          (createMenuManager []
            (doto (MenuManager.)
              (.add (filemenu))
              (.add (tabmenu))
              (.add (browsermenu))
              (.add (twittermenu)))))
    (.addMenuBar)
    (.setBlockOnOpen true)
    (.open))
  (.dispose (Display/getCurrent)))
