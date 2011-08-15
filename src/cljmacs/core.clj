(ns cljmacs.core
  (:use [clojure.java.io :only (reader)]
        [clojure.contrib.monads])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabItem]
           [org.eclipse.swt.widgets Display Shell Control Text]))

(defn #^Shell shell [] (.getActiveShell (Display/getCurrent)))

(defn #^CTabFolder tabfolder
  ([] (tabfolder (shell)))
  ([#^Shell shell]
     (domonad maybe-m
              [children (.getChildren shell)]
              (first children))))

(defn #^Text text
  ([] (text (shell)))
  ([#^Shell shell]
     (domonad maybe-m
              [children (.getChildren shell)]
              (second children))))

(defn #^CTabItem tabitem
  ([] (tabitem (shell)))
  ([#^Shell shell]
     (domonad maybe-m
              [tabfolder (tabfolder shell)]
              (.getSelection tabfolder))))

(defn #^Control control
  ([] (control (shell)))
  ([#^Shell shell]
     (domonad maybe-m
              [tabitem (tabitem shell)]
              (.getControl tabitem))))
