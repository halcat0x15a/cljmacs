(ns cljmacs.test.configuration
  (:use [clojure.test])
  (:import [cljmacs Configuration]))

(deftest configuration
  (let [config (Configuration/get_configuration)]
    (testing "configuration"
      (testing "each has equal hashcode"
        (is (= (.hashCode config) (.hashCode (Configuration/get_configuration)))))
      (testing "should be auto save"
        (is (.isAutoSave config))))))
