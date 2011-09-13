(ns cljmacs.main
  (:gen-class)
  (:use [clojure.java.io :only (file)]
        [cljmacs.core]
        [cljmacs.frame])
  (:import [org.apache.commons.lang SystemUtils]
           [org.eclipse.swt.widgets Display Shell]
           [cljmacs Frame]))

(defn load-cljmacs []
  (let [path (str SystemUtils/USER_HOME "/" ".cljmacs.clj")]
    (when (.exists (file path))
      (load-file path))))

(defn -main [& args]
  (let [display (Display.)]
    (try
      (let [frame (make-frame display)
            shell (.shell frame)]
        (load-cljmacs)
        (.open shell)
        (while (not (.isDisposed shell))
          (when-not (.readAndDispatch display)
            (.sleep display))))
      (finally (.dispose display)))))
