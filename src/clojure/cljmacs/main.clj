(ns cljmacs.main
  (:gen-class)
  (:use [clojure.java.io :only (file)]
        [cljmacs.core]
        [cljmacs.shell])
  (:import [org.eclipse.swt.widgets Display]))

(defn load-config []
  (let [home (System/getProperty "user.home")
        path (str home "/" ".cljmacs.clj")]
    (when (.exists (file path))
      (load-file path))))

(defn -main [& args]
  (let [display (Display.)]
    (try
      (let [shell (make-shell display)]
        (load-config)
        (open-shell shell)
        (while (not (.isDisposed shell))
          (when-not (.readAndDispatch display)
            (.sleep display))))
      (finally (.dispose display)))))
