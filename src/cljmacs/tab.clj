(ns cljmacs.tab
  (:use [cljmacs.core])
  (:import [org.eclipse.swt.widgets Shell]))

(defn close-tab [#^Shell shell]
  (.dispose (tabitem shell)))
