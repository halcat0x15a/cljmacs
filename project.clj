(defproject cljmacs "0.0.3-SNAPSHOT"
  :description "FIXME: write description"
  :dependencies [[org.clojure/clojure "1.2.1"]
                 [org.clojure/clojure-contrib "1.2.0"]
                 [commons-configuration/commons-configuration "1.6"]
                 [org.twitter4j/twitter4j-core "2.2.4"]
                 [org.twitter4j/twitter4j-stream "2.2.4"]
                 [org.twitter4j/twitter4j-media-support "2.2.4"]
                 [org.eclipse/swt-gtk-linux-x86 "3.5.2"]]
  :dev-dependencies [[swank-clojure "1.2.1"]]
  :source-path "src/clojure"
  :java-source-path "src/java"
  :main cljmacs.main)