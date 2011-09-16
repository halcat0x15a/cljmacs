(ns cljmacs.test.shortcut-key
  (:use [clojure.test])
  (:import [org.eclipse.swt SWT]
           [cljmacs ModifierKey ShortcutKey]))

(deftest shortcut-key
  (let [shortcut-key (ShortcutKey. \S (into-array ModifierKey [(ModifierKey/ctrl) (ModifierKey/shift)]))]
    (testing "shortcut key"
      (testing "accelerator should be Ctrl+Shift+S"
        (is (= (+ SWT/CTRL SWT/SHIFT (int \S)) (.accelerator shortcut-key))))
      (testing "string should be Ctrl+Shift+S"
        (is (= "Ctrl+Shift+S" (.toString shortcut-key)))))))
