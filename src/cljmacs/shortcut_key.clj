(ns cljmacs.shortcut-key
  (:import [clojure.lang Ref PersistentStructMap]
           [org.eclipse.swt SWT]))

(defstruct modify-key :code :str)

(defstruct shortcut-key :mods :char)

(def #^PersistentStructMap ctrl (struct modify-key SWT/CONTROL "Ctrl"))

(def #^PersistentStructMap alt (struct modify-key SWT/ALT "Alt"))

(def #^PersistentStructMap shift (struct modify-key SWT/SHIFT "Shift"))

(defn #^String shortcut-key-str [#^PersistentStructMap shortcut-key]
  (str (interpose "+" (map :str (:mods shortcut-key))) "+" (:char shortcut-key)))

(defn #^int accelerator [#^PersistentStructMap shortcut-key]
  (+ (reduce + (map :code (:mods shortcut-key))) (int (:char shortcut-key))))

(defn shortcut-key? [shortcut-key]
  (and (vector? (:mods shortcut-key)) (char? (:char shortcut-key))))

(def #^Ref new-file-key (ref (struct shortcut-key [ctrl] \N) :validator shortcut-key?))

(def #^Ref open-key (ref (struct shortcut-key [ctrl] \O) :validator shortcut-key?))

(def #^Ref save-key (ref (struct shortcut-key [ctrl] \S) :validator shortcut-key?))

(def #^Ref quit-key (ref (struct shortcut-key [ctrl alt] \Q) :validator shortcut-key?))

(def #^Ref close-key (ref (struct shortcut-key [ctrl] \W) :validator shortcut-key?))

(def #^Ref homepage-key (ref (struct shortcut-key [ctrl alt] \H) :validator shortcut-key?))

(def #^Ref home-key (ref (struct shortcut-key [ctrl shift] \H) :validator shortcut-key?))

(def #^Ref tweet-key (ref (struct shortcut-key [ctrl shift] \T) :validator shortcut-key?))

(def #^Ref update-key (ref (struct shortcut-key [ctrl shift] \U) :validator shortcut-key?))
