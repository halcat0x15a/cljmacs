(ns cljmacs.main
  (:gen-class)
  (:use [clojure.java.io :only (file)]
        [cljmacs.core]
        [cljmacs.window :only (window)]))

(defn- init []
  (let [home (System/getProperty "user.home")
        path (str home "/" ".cljmacs.clj")
        f (file path)]
    (when (.exists f)
      (dosync
       (load-file path)))))

(defn -main [& args]
  (init)
  (window))
