(ns cljmacs.core
  (:use [clojure.java.io :only (reader)])
  (:import [org.eclipse.jface.action Action]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.widgets Display]))

(defmacro defconfig [name x validator]
  `(def ~name (atom ~x :validator ~validator)))

(defstruct modify-key :code :str)

(defstruct shortcut-key :mods :char)

(def ctrl (struct modify-key SWT/CONTROL "Ctrl"))

(def alt (struct modify-key SWT/ALT "Alt"))

(def shift (struct modify-key SWT/SHIFT "Shift"))

(defn shortcut-key-str [shortcut-key]
  (str (interpose "+" (map :str (conj (:mods shortcut-key) (:char shortcut-key))))))

(defn accelerator [shortcut-key]
  (+ (reduce + (map :code (:mods shortcut-key))) (int (:char shortcut-key))))

(defn shortcut-key? [shortcut-key]
  (and (vector? (:mods shortcut-key)) (char? (:char shortcut-key))))

(defmacro defshortcut [name mods char]
  `(def ~name (ref (struct cljmacs.core/shortcut-key ~mods ~char) :validator cljmacs.core/shortcut-key?)))

(defn action [string shortcut-key f]
  (doto (proxy [Action] []
          (run []
            (f)))
    (.setText (str string (shortcut-key-str shortcut-key)))
    (.setAccelerator (accelerator shortcut-key))))

(defn shell [] (.getActiveShell (Display/getCurrent)))

(defn tabfolder
  ([] (tabfolder (shell)))
  ([shell]
     (.getData shell "tabfolder")))

(defn text
  ([] (text (shell)))
  ([shell]
     (.getData shell "text")))

(defn tabitem
  ([] (tabitem (shell)))
  ([shell]
     (.getSelection (tabfolder shell))))

(defn control
  ([] (control (shell)))
  ([shell]
     (.getControl (tabitem shell))))
