(ns cljmacs.test.configuration
  (:use [clojure.test])
  (:import [cljmacs Configuration]))

(deftest configuration
  (let [config (Configuration/get_configuration)]
    (testing "configuration"
      (testing "should be identical"
        (is (identical? config (Configuration/get_configuration))))
      (testing "should be auto save"
        (is (.isAutoSave config))))))
