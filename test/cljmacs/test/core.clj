(ns cljmacs.test.core
  (:use [clojure.test]
        [cljmacs.core])
  (:import [clojure.lang IDeref]
           [cljmacs Configuration Property]))

(deftest key-creation
  (let [key (create-key 'test)]
    (testing "key"
      (testing "should be user.test"
        (is (= key "user.test"))))))

(deftest property-creation
  (let [config (Configuration/get_configuration)
        property (create-property 'test "foo")
        instanceof? #(isa? (class property) %)
        key (create-key 'test)]
    (testing "property"
      (testing "should be instance of Property"
        (is (instanceof? Property)))
      (testing "should be instance of IDeref"
        (is (instanceof? IDeref))))
    (.clearProperty config key)))

(def hogehoge "hogehoge")

(deftest vars-finding
  (let [vars (find-vars 'hogehoge)]
    (testing "vars"
      (testing "should be seq"
        (is (seq? vars)))
      (testing "should be var every time"
        (is (every? var? vars)))
      (testing "count should be 1"
        (is (= (count vars) 1)))
      (testing "first should be string hogehoge"
        (is (= (var-get (first vars)) "hogehoge"))))))

(deftest definition
  (testing "widget"))
