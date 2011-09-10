(ns cljmacs.core
  (:require [clojure.string])
  (:import [org.eclipse.swt SWT]
           [cljmacs Property ModifierKey ShortcutKey Frame Widget Menu]))

(defn create-key [name]
  (str (ns-name *ns*) '. name))

(defn create-property [key value]
  (Property. (create-key key) value))

(defmacro defproperty
  ([name value]
     `(defproperty ~name ~value '~name))
  ([name value key]
     `(def ~name (create-property ~key ~value))))

(defmacro defstyle [name & styles]
  `(defproperty ~name (+ ~@styles) (str 'style '. '~name)))

(def ctrl (ModifierKey/ctrl))

(def alt (ModifierKey/alt))

(def shift (ModifierKey/shift))

(defn create-shortcut-key [character & modifiers]
  (ShortcutKey. character (into-array ModifierKey modifiers)))

(defmacro defshortcut [name character & modifiers]
  `(defproperty ~name (create-shortcut-key ~character ~@modifiers)))

(defn current-frame [] (Frame/current_frame))

(defmacro defwidget [name params body]
  `(defn ~name ~params
     (let [tab-folder# (.tab_folder (current-frame))
           widget# (proxy [Widget] []
                     (createControl [tab-folder# tab-item#]
                       (~body tab-folder# tab-item#)))]
       (doto widget#
         (.create tab-folder# control#)))))

(defmacro defmenu [name string body]
  `(defn ~name
     ([] (~name 0))
     ([index#]
        (let [menu# (proxy [Menu] []
                      (createMenu [menu#]
                        (~body menu#)))]
          (doto menu#
            (.create (current-frame) index# ~string))))))

(defn end-of-line
  ([]
     (end-of-line (.control (current-frame))))
  ([text]
     (.setSelection text (.getCharCount text))))
