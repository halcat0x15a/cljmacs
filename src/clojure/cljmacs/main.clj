(ns cljmacs.main
  (:gen-class)
  (:use [clojure.java.io :only (file)]
        [cljmacs.core]
        [cljmacs.frame])
  (:import [org.apache.commons.lang SystemUtils]
           [org.eclipse.swt.widgets Display Shell]
           [cljmacs Frame]))

(defn load-cljmacs []
  (let [path (str SystemUtils/USER_HOME "/" ".cljmacs.clj")
        file (file path)]
    (if (.exists file)
      (load-file path)
      (slurp file ""))))

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
