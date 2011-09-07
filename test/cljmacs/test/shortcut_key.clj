(ns cljmacs.test.shortcut-key
  (:use [cljmacs.core]
        [clojure.test])
  (:import [org.eclipse.swt SWT]
           [cljmacs ModifierKey ShortcutKey]))

(deftest accelerator
  (let [shortcut-key (ShortcutKey. (into-array ModifierKey [(ModifierKey/ctrl)]) \F)]
    (is (+ SWT/CTRL (int \F)) (.accelerator shortcut-key))))

(deftest to-string
  (let [shortcut-key (ShortcutKey. (into-array ModifierKey [(ModifierKey/ctrl) (ModifierKey/shift)]) \S)]
    (is "Ctrl+Shift+S" (.toString shortcut-key))))
