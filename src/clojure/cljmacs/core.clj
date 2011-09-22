(ns cljmacs.core
  (:use [clojure.java.io :only (file)])
  (:import [clojure.lang IDeref]
           [org.apache.commons.lang SystemUtils]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.widgets Shell Menu MenuItem]
           [cljmacs Property ModifierKey ShortcutKey Widget]))

(defn create-key [name]
  (str (ns-name *ns*) '. name))

(defn create-property [key value]
  (proxy [Property IDeref] [(create-key key) value]
    (deref [] value)))

(defmacro defproperty [name value]
  `(def ~name (create-property '~name ~value)))

(defn set-property! [property value]
  (.value_set property value))

(def style (partial reduce bit-or))

(defmacro defstyle [name & values]
  `(defproperty ~name (style [~@values])))

(defn set-style! [style & values]
  (set-property! style (style values)))

(def ctrl (ModifierKey/ctrl))

(def alt (ModifierKey/alt))

(def shift (ModifierKey/shift))

(defn create-shortcut-key [character & modifiers]
  (ShortcutKey. character (into-array ModifierKey modifiers)))

(defmacro defshortcut [name character & modifiers]
  `(defproperty ~name (create-shortcut-key ~character ~@modifiers)))

(defn set-shortcut! [shortcut character & modifiers]
  (set-property! shortcut (apply create-shortcut-key character modifiers)))

(defn find-vars [x] (remove nil? (map (comp find-var symbol #(str % \/ x)) (all-ns))))

(defn fun [fn size]
  (with-meta fn {:fun true, :size size}))

(defn fun? [fun]
  (and (fn? fun) (:fun (meta fun))))

(defn run-or-apply [text function arg]
  (doto text
    (.setText "")
    (.setData nil))
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
        (when-let [v (first vars)]
          (when-let [function (var-get v)]
            (when (fun? function)
              (run-or-apply text function frame)))))
      (when (fun? data)
        (run-or-apply text data string)))))

(defmacro defun [name parameter & body]
  `(def ~name (fun (fn ~parameter ~@body) (count '~parameter))))

(def init-file-path (str SystemUtils/USER_HOME "/" ".cljmacs.clj"))

(defun load-cljmacs [frame]
  (let [path init-file-path
        file (file path)]
    (if (.exists file)
      (load-file path)
      (spit file ""))))

(defmacro defwidget [name parameter widget]
  `(defun ~name ~parameter
     (let [widget# (doto ~widget
                     (.create))]
       (doto (.tab_item widget#)
         (.setData (keyword '~name)))
       widget#)))

(defmacro defwidgetm [name parameter id body-fn]
  `(defun ~name ~parameter
     (let [frame# ~(first parameter)]
       (if-let [tab-item# (.. frame# tab_folder getSelection)]
         (if (= (.getData tab-item#) ~id)
           (~body-fn (.getControl tab-item#))
           (message frame# (str (subs (str ~id) 1) " does not exist")))
         (message frame# "no tab")))))

(defn create-menu-item [parent-menu menu string]
  (doto (MenuItem. parent-menu SWT/CASCADE)
    (.setText string)
    (.setMenu menu)))

(defmulti create-menu (fn [widget _] (class widget)))

(defmethod create-menu Shell [shell string]
  (let [menu (Menu. shell SWT/DROP_DOWN)]
    (create-menu-item (.getMenuBar shell) menu string)
    menu))

(defmethod create-menu Menu [parent-menu string]
  (let [menu (Menu. parent-menu)]
    (create-menu-item parent-menu menu string)
    menu))

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
