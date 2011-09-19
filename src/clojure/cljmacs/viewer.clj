(ns cljmacs.viewer
  (:use [clojure.java.io :only (input-stream)]
        [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.events PaintListener]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Canvas]
           [cljmacs Widget]))

(defwidget viewer [frame url]
  (proxy [Widget] [frame]
    (create_control [tab-folder tab-item]
      (let [display (.getDisplay tab-folder)
            image (Image. display (input-stream url))
            canvas (doto (Canvas. tab-folder SWT/NONE)
                     (.addPaintListener (proxy [PaintListener] []
                                          (paintControl [e]
                                            (let [gc (.gc e)]
                                              (.drawImage gc image 0 0))))))]
        canvas))))