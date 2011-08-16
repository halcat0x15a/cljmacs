(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [clojure.contrib.monads]
        [cljmacs.core]
        [cljmacs.browser :only (browser)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory Status]
           [twitter4j.auth AccessToken]
           [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabItem]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Tree TreeItem]))

(def #^String consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def #^String consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(defconfig filename ".access-token" string?)

(defconfig style (bit-or SWT/MULTI SWT/BORDER) integer?)

(defshortcut home-key [ctrl shift] \H)

(defshortcut tweet-key [ctrl shift] \T)

(defshortcut update-key [ctrl shift] \U)

(defn- #^AccessToken load-access-token []
  (with-open [ois (ObjectInputStream. (input-stream @filename))]
    (.readObject ois)))

(defn- store-access-token [#^AccessToken access-token]
  (with-open [oos (ObjectOutputStream. (output-stream @filename))]
    (.writeObject oos access-token)))

(def twitter
  (ref
   (let [twitter (doto (.getInstance (TwitterFactory.))
                   (.setOAuthConsumer consumer-key consumer-secret))]
     (try
       (doto twitter
         (.setOAuthAccessToken (load-access-token)))
       (catch FileNotFoundException _ twitter)))))

(def request-token (ref nil))

(defn login []
  (dosync
   (let [token (.getOAuthRequestToken @twitter)
         url (.getAuthorizationURL token)]
     (ref-set request-token token)
     (browser url))))

(defn auth [#^String pin]
  (dosync
   (let [token (.getOAuthAccessToken @twitter @request-token pin)]
     (store-access-token token)
     (alter twitter #(.setOAuthAccessToken % token)))))

(defn #^TreeItem treeitem [tree #^Status status]
  (let [id (.getInReplyToStatusId status)
        user (.getUser status)
        item (doto (TreeItem. tree SWT/NONE 0)
               (.setData status)
               (.setImage (Image. (.getDisplay tree) (input-stream (.getProfileImageURL user))))
               (.setText (.getText status)))]
    (when-let [rt (.getRetweetedStatus status)]
      (treeitem item rt))
    (when (> id 0)
      (treeitem item (.showStatus @twitter id)))
    item))

(defn update
  ([] (update @twitter (shell)))
  ([#^Shell shell] (update @twitter shell))
  ([#^Twitter twitter #^Shell shell]
     (domonad maybe-m
              [tree (control shell)
               item (.getItem tree 0)
               data (.getData item)
               tl (reverse (take-while #(not= data %) (.getHomeTimeline twitter)))]
              (doseq [s tl]
                (.setSelection tree (treeitem tree s))))))

(defn tweet [#^String string]
  (let [twitter @twitter]
    (.updateStatus twitter string)
    (update twitter (shell))))

(def #^String tweet-string "(tweet \"\")")

(defn tweet-text [#^Shell shell]
  (let [text (text shell)
        i (inc (.indexOf tweet-string (int \")))]
    (doto text
      (.setText tweet-string)
      (.setFocus)
      (.setSelection i))
    nil))

(defn twitter-client
  ([] (twitter-client (tabfolder)))
  ([#^Shell shell]
     (let [tabfolder (tabfolder shell)
           twitter @twitter
           tabitem (doto (CTabItem. tabfolder SWT/CLOSE)
                     (.setText "Home"))
           tree (Tree. tabfolder @style)]
       (doseq [s (reverse (.getHomeTimeline twitter))]
         (treeitem tree s))
       (doto tabitem
         (.setControl tree))
       (.setSelection tabfolder tabitem))))

(defn #^MenuManager twittermenu [#^Shell shell]
  (doto (MenuManager. "T&witter")
    (.add (action "&Home\t" @home-key #(twitter-client shell)))
    (.add (action "&Tweet\t" @tweet-key #(tweet-text shell)))
    (.add (action "&Update\t" @update-key #(update shell)))))
