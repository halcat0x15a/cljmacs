(ns cljmacs.test.property
  (:use [clojure.test])
  (:import [cljmacs Configuration Property]))

(deftest property
  (let [config (Configuration/get_configuration)
        key "test"
        property (Property. key 100)]
      (testing "value should be 100"
        (is (= (.value property) 100)))
      (testing "should contain key"
        (is (.containsKey config key)))
      (testing "value should set 10"
        (.value_set property 10)
        (is (= (.value property) 10))
        (is (= (.getInt config key) 10)))
      (.clearProperty config key)))
