(ns cljmacs.core
  (:require [clojure.string])
  (:import [clojure.lang IDeref]
           [org.eclipse.swt SWT]
           [cljmacs Property ModifierKey ShortcutKey Widget Menu]))

(defn create-key [name]
  (str (ns-name *ns*) '. name))

(defn create-property [key value]
  (proxy [Property IDeref] [(create-key key) value]
    (deref [] value)))

(defmacro defproperty [name value]
  `(def ~name (create-property '~name ~value)))

(defmacro defstyle [name & styles]
  `(defproperty ~name (+ ~@styles)))

(def ctrl (ModifierKey/ctrl))

(def alt (ModifierKey/alt))

(def shift (ModifierKey/shift))

(defn create-shortcut-key [character & modifiers]
  (ShortcutKey. character (into-array ModifierKey modifiers)))

(defmacro defshortcut [name character & modifiers]
  `(defproperty ~name (create-shortcut-key ~character ~@modifiers)))

(defn run-or-apply [text function arg]
  (let [size (:size (meta function))]
    (if (= size 1)
      (function arg)
      (doto text
        (.setData text (with-meta (partial function arg) {:size (dec size)}))
        (.setFocus)))
    (.setText text "")))

(defn menu-run-or-apply [frame function]
  #(run-or-apply (.text frame) function frame))

(defmacro defun [name parameter body]
  `(def ~name (with-meta (fn ~parameter ~body) {:size (count '~parameter)})))

(defmacro defwidget [name parameter widget]
  `(defun ~name ~parameter
     (doto ~widget
       (.create))))

(defmacro defmenu [name parameter menu]
  `(defun ~name ~parameter ~menu))

(defn message [text string]
  (.setText text string))

(defn end-of-line [text]
  (.setSelection text (.getCharCount text)))

(defn find-vars [x] (remove nil? (map (comp find-var symbol #(str % \/ x)) (all-ns))))
