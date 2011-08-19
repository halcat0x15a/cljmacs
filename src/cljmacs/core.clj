(ns cljmacs.core
  (:require [clojure.string])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.widgets Menu MenuItem]))

(defn boolean? [b] (or (true? b) (false? b)))

(defmacro defconfig [name x validator]
  `(def ~name (atom ~x :validator ~validator)))

(defmacro defstyle [name & styles]
  `(defconfig ~name (+ ~@styles) integer?))

(defstruct modify-key :code :str)

(defstruct shortcut-key :mods :char)

(def ctrl (struct modify-key SWT/CONTROL "Ctrl"))

(def alt (struct modify-key SWT/ALT "Alt"))

(def shift (struct modify-key SWT/SHIFT "Shift"))

(defn str-shortcut-key [shortcut-key]
  (clojure.string/join (interpose "+" (conj (vec (map :str (:mods shortcut-key))) (:char shortcut-key)))))

(defn accelerator [shortcut-key]
  (apply + (conj (map :code (:mods shortcut-key)) (int (:char shortcut-key)))))

(defn shortcut-key? [shortcut-key]
  (and (vector? (:mods shortcut-key)) (char? (:char shortcut-key))))

(defmacro defshortcut [name mods char]
  `(defconfig ~name (struct shortcut-key ~mods ~char) shortcut-key?))

(def shell (ref nil))

(defn tab-folder [] (first (.getChildren @shell)))

(defn text [] (second (.getChildren @shell)))

(defn make-tab-item [tab-folder] (CTabItem. tab-folder SWT/CLOSE))

(defmacro defwidget [name params body]
  `(defn ~name ~params
     (let [tab-folder# (tab-folder)
           tab-item# (make-tab-item tab-folder#)
           [control# name#] (~body tab-folder# tab-item#)]
       (doto tab-item#
         (.setControl control#)
         (.setText name#))
       (.setSelection tab-folder# tab-item#))))

(defn make-menu [shell menu-bar string index]
  (let [menu (Menu. shell SWT/DROP_DOWN)]
    (doto (MenuItem. menu-bar SWT/CASCADE index)
      (.setText string)
      (.setMenu menu))
    menu))

(defmacro defmenu [name string body]
  `(defn ~name [index#]
     (let [shell# @shell
           menu-bar# (.getMenuBar shell#)
           menu# (make-menu shell# menu-bar# ~string index#)]
       (~body menu#)
       menu#)))

(defn make-menu-item
  ([menu function]
    (doto (MenuItem. menu SWT/PUSH)
      (.addSelectionListener (proxy [SelectionAdapter] []
                                (widgetSelected [_]
                                  (function))))))
  ([menu text function]
     (doto (make-menu-item menu function)
       (.setText text)))
  ([menu text function key]
     (doto (make-menu-item menu function)
       (.setText (str text \tab (str-shortcut-key key)))
       (.setAccelerator (accelerator key)))))

(defn make-separator [menu] (MenuItem. menu SWT/SEPARATOR))
