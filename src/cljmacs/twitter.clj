(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [clojure.contrib.monads]
        [cljmacs.core]
        [cljmacs.browser :only (browser)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory]
           [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Tree TreeItem]))

(def consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(defconfig filename ".access-token" string?)

(defconfig style (bit-or SWT/MULTI SWT/BORDER) integer?)

(defshortcut home-key [ctrl shift] \H)

(defshortcut tweet-key [ctrl shift] \T)

(defshortcut update-key [ctrl shift] \U)

(defn- load-access-token []
  (with-open [ois (ObjectInputStream. (input-stream @filename))]
    (.readObject ois)))

(defn- store-access-token [access-token]
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

(defn auth [pin]
  (dosync
   (let [token (.getOAuthAccessToken @twitter @request-token pin)]
     (store-access-token token)
     (alter twitter #(.setOAuthAccessToken % token)))))

(defn treeitem [tree status]
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

(defn twitter-client
  ([timeline] (twitter-client @twitter timeline))
  ([twitter timeline] (twitter-client (shell) twitter timeline))
  ([shell twitter timeline]
     (let [tabfolder (tabfolder)
           tabitem (doto (CTabItem. tabfolder SWT/CLOSE)
                     (.setText "Home"))
           tree (Tree. tabfolder @style)]
       (doseq [s (reverse timeline)]
         (treeitem tree s))
       (doto tabitem
         (.setControl tree))
       (.setSelection tabfolder tabitem))))

(defn home []
  (let [twitter @twitter]
    (twitter-client twitter (.getHomeTimeline twitter))))

(defn update
  ([] (update @twitter (shell)))
  ([twitter shell]
     (domonad maybe-m
              [tree (control shell)
               item (.getItem tree 0)
               data (.getData item)
               tl (reverse (take-while #(not= data %) (.getHomeTimeline twitter)))]
              (doseq [s tl]
                (.setSelection tree (treeitem tree s))))))

(defn tweet [string]
  (let [twitter @twitter]
    (.updateStatus twitter string)
    (update twitter (shell))))

(def tweet-string "(tweet \"\")")

(defn tweet-text []
  (let [text (text)
        i (inc (.indexOf tweet-string (int \")))]
    (doto text
      (.setText tweet-string)
      (.setFocus)
      (.setSelection i))
    nil))

(defn twittermenu []
  (doto (MenuManager. "T&witter")
    (.add (action "&Home\t" @home-key home))
    (.add (action "&Tweet\t" @tweet-key tweet-text))
    (.add (action "&Update\t" @update-key update))))
