(ns cljmacs.main
  (:gen-class)
  (:use [clojure.java.io :only (file)]
        [cljmacs.core]
        [cljmacs.window :only (window)])
  (:import [org.eclipse.swt.widgets Display]))

(defn- init []
  (let [home (System/getProperty "user.home")
        path (str home "/" ".cljmacs.clj")]
    (when (.exists (file path))
      (load-file path))))

(defn -main [& args]
  (init)
  (doto (window)
    (.open))
  (.dispose (Display/getCurrent)))
