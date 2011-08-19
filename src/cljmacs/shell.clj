(ns cljmacs.shell
  (:use [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.layout GridLayout GridData]
           [org.eclipse.swt.widgets Shell Menu Text]))

(defconfig title "cljmacs" string?)

(defconfig size [400 300] vector?)

(defconfig simple false boolean?)

(defshortcut close-key [ctrl] \W)

(defshortcut quit-key [ctrl alt] \Q)

(defn close-tab []
  (.dispose (.getSelection (force tab-folder))))

(defn quit []
  (.close @shell))

(defn eval-text [text]
  (doto text
    (.setText (str (try
                     (load-string (.getText text))
                     (catch Exception e e))))
    (.setSelection (.getCharCount text)))
  nil)

(defn make-tab-menu [shell menu-bar]
  (let [menu (make-menu shell menu-bar "&Tab" 0)]
    (make-menu-item menu "&Close\t" close-tab @close-key)
    (make-menu-item menu "&Quit\t" quit @quit-key)
    menu))

(defn make-help-menu [shell menu-bar]
  (let [menu (make-menu shell menu-bar "&Help" 1)]
    menu))

(defn make-tab-folder [shell]
  (doto (CTabFolder. shell SWT/BORDER)
    (.setLayoutData (GridData. SWT/FILL, SWT/FILL, true, true))
    (.setSimple @simple)))

(defn make-text [shell]
  (doto (Text. shell (bit-or SWT/SINGLE SWT/BORDER))
    (.addSelectionListener (proxy [SelectionAdapter] []
                             (widgetDefaultSelected [e]
                               (eval-text (.widget e)))))
    (.setLayoutData (GridData. SWT/FILL, SWT/END, true, false))))

(defn make-shell [display]
  (dosync
   (let [shell (doto (Shell. display)
                 (.setLayout (GridLayout. 1 false)))
         menu-bar (Menu. shell SWT/BAR)]
     (make-tab-menu shell menu-bar)
     (make-help-menu shell menu-bar)
     (make-tab-folder shell)
     (make-text shell)
     (.setMenuBar shell menu-bar)
     (ref-set cljmacs.core/shell shell))))

(defn open-shell [shell]
  (let [[width height] @size]
    (doto shell
      (.setText @title)
      (.setSize width height)
      (.open))))