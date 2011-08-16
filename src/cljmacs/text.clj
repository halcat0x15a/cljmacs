(ns cljmacs.text
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.layout GridData]
           [org.eclipse.swt.widgets Text]))

(defn eval-text [#^Text text]
  (let [s (try
            (load-string (.getText text))
            (catch Exception e e))]
    (doto text
      (.setText (str s))
      (.setSelection (.getCharCount text)))
    nil))

(defn #^Text text [#^Composite shell]
  (doto (Text. shell (bit-or SWT/SINGLE SWT/BORDER))
    (.addSelectionListener
     (proxy [SelectionAdapter] []
       (widgetDefaultSelected [e]
         (eval-text (.widget e)))))
    (.setLayoutData (GridData. SWT/FILL, SWT/END, true, false))))
