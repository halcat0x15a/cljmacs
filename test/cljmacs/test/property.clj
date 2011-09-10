(ns cljmacs.test.property
  (:use [clojure.test]
        [cljmacs.core])
  (:import [cljmacs Configuration]))

(deftest property
  (let [config (Configuration/get_configuration)
        property (create-property 'test 100)
        test-key (create-key 'test)]
    (testing "property"
      (testing "has value that is 100"
        (is (= (.value property) 100)))
      (testing "should contain key"
        (is (.containsKey config test-key)))
      (testing "has value that is changed 10"
        (.value_set property 10)
        (is (= (.value property) 10))
        (is (= (.getInt config test-key) 10)))
      (.clearProperty config test-key))))
