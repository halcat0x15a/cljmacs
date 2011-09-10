(ns cljmacs.test.core
  (:use [clojure.test]
        [cljmacs.core]))

(deftest creation
  (let [key (create-key 'test)]
    (testing "key"
      (testing "should be user.test"
        (is (= key "user.test"))))))