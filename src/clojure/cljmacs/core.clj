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

(defmacro defun [name params body]
  `(def ~name (with-meta (fn ~params ~body) {:frame true})))

(defmacro defwidget [name params widget]
  `(defun ~name ~params
     (doto ~widget
       (.create))))

(defmacro defmenu [name params menu]
  `(defun ~name ~params ~menu))

(defn message [text string]
  (.setText text string))

(defn end-of-line [text]
  (.setSelection text (.getCharCount text)))

(defn find-vars [x] (remove nil? (map (comp find-var symbol #(str % \/ x)) (all-ns))))
