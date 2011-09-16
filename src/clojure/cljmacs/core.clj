(ns cljmacs.core
  (:require [clojure.string])
  (:import [clojure.lang IDeref]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.widgets Menu MenuItem]
           [cljmacs Property ModifierKey ShortcutKey Widget]))

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

(defn find-vars [x] (remove nil? (map (comp find-var symbol #(str % \/ x)) (all-ns))))

(defn fun [fn size]
  (with-meta fn {:fun true, :size size}))

(defn fun? [fun]
  (and (fn? fun) (:fun (meta fun))))

(defn run-or-apply [text function arg]
  (.setText text "")
  (let [size (:size (meta function))]
    (if (= size 1)
      (function arg)
      (doto text
        (.setData (fun (partial function arg) (dec size)))
        (.setFocus)))))

(defn eval-text [frame]
  (let [text (.text frame)
        data (.getData text)
        string (.getText text)]
    (if (nil? data)
      (let [vars (find-vars (symbol string))]
        (when-let [function (var-get (first vars))]
          (when (fun? function)
            (run-or-apply text function frame))))
      (when (fun? data)
        (run-or-apply text data string)))))

(defmacro defun [name parameter & body]
  `(def ~name (fun (fn ~parameter ~@body) (count '~parameter))))

(defmacro defwidget [name parameter widget]
  `(defun ~name ~parameter
     (doto ~widget
       (.create))))

(defmacro create-menu [parent menu string & items]
  `(let [menu# (Menu. ~parent SWT/DROP_DOWN)]
     (doto (MenuItem. ~menu SWT/CASCADE)
       (.setText ~string)
       (.setMenu menu#))
     (doto menu# ~@items)))

(defn create-item
  ([menu] (MenuItem. menu SWT/SEPARATOR))
  ([menu string function frame]
     (doto (MenuItem. menu SWT/PUSH)
       (.addSelectionListener (proxy [SelectionAdapter] []
                                (widgetSelected [e]
                                  (run-or-apply (.text frame) function frame))))
       (.setText string)))
  ([menu string function frame shortcut]
     (doto (create-item menu string function frame)
       (.setText (str string \tab shortcut))
       (.setAccelerator (.accelerator shortcut)))))

(defun message [frame string]
  (let [text (.text frame)]
    (.setText text string)
    (.. text getDisplay (timerExec 1000 #(.setText text "")))))

(defn end-of-line [text]
  (.setSelection text (.getCharCount text)))
