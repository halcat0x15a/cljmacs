(ns cljmacs.test.shortcut-key
  (:use [cljmacs.core]
        [clojure.test])
  (:import [org.eclipse.swt SWT]
           [cljmacs ModifierKey ShortcutKey]))

(deftest shortcut-key
  (let [shortcut-key (create-shortcut-key \S ctrl shift)]
    (testing "shortcut key"
      (testing "has accelerator"
        (is (= (+ SWT/CTRL SWT/SHIFT (int \S)) (.accelerator shortcut-key))))
      (testing "has string"
        (is (= "Ctrl+Shift+S" (.toString shortcut-key)))))))
