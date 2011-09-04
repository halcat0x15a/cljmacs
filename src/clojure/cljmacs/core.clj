(ns cljmacs.core
  (:require [clojure.string])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem]
           [org.eclipse.swt.events SelectionAdapter]
           [cljmacs ModifierKey ShortcutKey Widget ControlProxy Menu MenuProxy Utils]))

(defmacro defconfig [name x]
  (let [config (Utils/configuration)
        key (str (ns-name *ns*) "." name)
        set-property #(.setProperty config key %)
        create-name #(symbol (str % "-" name))]
    (when-not (.containsKey config key)
      (set-property x))
    `(do
       (def ~name (ref ~x))
       (defn ~(create-name "get") [] (deref ~name))
       (defn ~(create-name "set") [value#]
         (dosync
          (~set-property value#)
          (ref-set ~name value#))))))

(defmacro defstyle [name & styles]
  `(defconfig ~name (+ ~@styles)))

(def ctrl (ModifierKey/ctrl))

(def alt (ModifierKey/alt))

(def shift (ModifierKey/shift))

(defmacro defshortcut [name mods char]
  `(defconfig ~name (ShortcutKey. (into-array ModifierKey ~mods) ~char)))

(def current-frame (ref nil))

(defmacro defwidget [name params body]
  `(defn ~name ~params
     (let [tab-folder# (.tab_folder @current-frame)
           control# (proxy [ControlProxy] []
                     (control [tab-folder# tab-item#]
                       (~body tab-folder# tab-item#)))]
       (Widget. tab-folder# control#))))

(defmacro defmenu [name string body]
  `(defn ~name
     ([] (~name 0))
     ([index#]
        (let [menu# (proxy [MenuProxy] []
                      (create [menu#]
                        (~body menu#)))]
          (Menu. @current-frame index# ~string menu#)))))

(defn message [string]
  (let [text (.getText @current-frame)
        display (.getDisplay text)]
    (.setText text (str \" string \"))))
