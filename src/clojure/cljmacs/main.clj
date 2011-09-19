(ns cljmacs.main
  (:gen-class)
  (:use [cljmacs.core]
        [cljmacs.frame]
        [cljmacs.editor :only (file-menu edit-menu)]
        [cljmacs.browser :only (browser-menu)]
        [cljmacs.twitter :only (twitter-menu)])
  (:import [org.eclipse.swt.widgets Display]))

(defn -main [& args]
  (let [display (Display.)]
    (try
      (let [frame (make-frame display)
            shell (.shell frame)]
        (load-cljmacs frame)
        (file-menu frame)
        (edit-menu frame)
        (browser-menu frame)
        (twitter-menu frame)
        (.open shell)
        (while (not (.isDisposed shell))
          (when-not (.readAndDispatch display)
            (.sleep display))))
      (finally (.dispose display)))))
