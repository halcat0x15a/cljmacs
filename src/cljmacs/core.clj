(ns cljmacs.core
  (:use [clojure.java.io :only (reader)]
        [clojure.contrib.monads])
  (:import [org.eclipse.jface.action Action]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabItem]
           [org.eclipse.swt.widgets Display Shell Control Text]))

(defmacro defconfig [#^String name x validator]
  `(def ~name (atom ~x :validator ~validator)))

(defstruct modify-key :code :str)

(defstruct shortcut-key :mods :char)

(def ctrl (struct modify-key SWT/CONTROL "Ctrl"))

(def alt (struct modify-key SWT/ALT "Alt"))

(def shift (struct modify-key SWT/SHIFT "Shift"))

(defn #^String shortcut-key-str [shortcut-key]
  (str (interpose "+" (map :str (conj (:mods shortcut-key) (:char shortcut-key))))))

(defn #^int accelerator [shortcut-key]
  (+ (reduce + (map :code (:mods shortcut-key))) (int (:char shortcut-key))))

(defn shortcut-key? [shortcut-key]
  (and (vector? (:mods shortcut-key)) (char? (:char shortcut-key))))

(defmacro defshortcut [name mods char]
  `(def ~name (ref (struct cljmacs.core/shortcut-key ~mods ~char) :validator cljmacs.core/shortcut-key?)))

(defn action [#^String string shortcut-key f]
  (doto (proxy [Action] []
          (run []
            (f)))
    (.setText (str string (shortcut-key-str shortcut-key)))
    (.setAccelerator (accelerator shortcut-key))))

(defn #^Shell shell [] (.getActiveShell (Display/getCurrent)))

(defn #^CTabFolder tabfolder
  ([] (tabfolder (shell)))
  ([#^Shell shell]
     (.getData shell "tabfolder")))

(defn #^Text text
  ([] (text (shell)))
  ([#^Shell shell]
     (.getData shell "text")))

(defn #^CTabItem tabitem
  ([] (tabitem (shell)))
  ([#^Shell shell]
     (.getSelection (tabfolder shell))))

(defn #^Control control
  ([] (control (shell)))
  ([#^Shell shell]
     (.getControl (tabitem shell))))
