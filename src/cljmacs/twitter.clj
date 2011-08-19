(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [cljmacs.core]
        [cljmacs.browser :only (browser)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory TwitterException StatusUpdate]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem]
           [org.eclipse.swt.events SelectionAdapter TreeListener]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Tree TreeItem]))

(def consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(defconfig filename ".access-token" string?)

(defstyle twitter-style SWT/MULTI SWT/BORDER)

(defshortcut home-key [ctrl shift] \H)

(defshortcut tweet-key [ctrl shift] \T)

(defshortcut update-key [ctrl shift] \U)

(defshortcut retweet-key [ctrl shift] \R)

(defshortcut fav-key [ctrl shift] \F)

(def twitter
  (let [twitter (doto (.getInstance (TwitterFactory.))
          (.setOAuthConsumer consumer-key consumer-secret))]
    (try
      (doto twitter
        (.setOAuthAccessToken (load-access-token)))
      (catch FileNotFoundException _
        (login)
        twitter))))

(defn- load-access-token []
  (with-open [ois (ObjectInputStream. (input-stream @filename))]
    (.readObject ois)))

(defn- store-access-token [access-token]
  (with-open [oos (ObjectOutputStream. (output-stream @filename))]
    (.writeObject oos access-token)))

(def request-token (ref nil))

(defn login []
  (dosync
   (let [token (.getOAuthRequestToken twitter)
         url (.getAuthorizationURL token)]
     (ref-set request-token token)
     (browser url))))

(defn pin []
  (dosync
   (let [token @request-token
         text (text)
         string (.getText text)]
     (ref-set request-token nil)
     (store-access-token (.getOAuthAccessToken twitter token string)))))

(defn treeitem [tree status]
  (doto (TreeItem. tree SWT/NONE 0)
    (.setData status)
    (.setImage (Image. (.getDisplay tree) (input-stream (.getProfileImageURL (.getUser status)))))
    (.setText (.getText status))))

(defn set-treeitem [tree status]
  (let [item (treeitem tree status)
        id (.getInReplyToStatusId status)]
    (when-let [rt (.getRetweetedStatus status)]
      (treeitem item rt))
    (when (not= id -1)
      (treeitem item (.showStatus (twitter) id)))
    item))

(defn update
  ([] (update @shell))
  ([shell]
     (let [tree (.. (tab-folder) getSelection getControl)
           item (.getItem tree 0)
           data (.getData item)
           tl (reverse (take-while #(not= data %) (.getHomeTimeline twitter)))]
       (doseq [s tl]
         (.setSelection tree (set-treeitem tree s))))))

(defn tweet []
  (let [shell @shell
        text (text shell)
        id (.getData text "status_id")
        status (StatusUpdate. (.getText text))]
    (when id
      (.setInReplyToStatusId status id))
    (.updateStatus twitter status)
    (.setText text "")
    (update)))

(defwidget twitter-client [string timeline]
  (fn [tab-folder tab-item]
     (let [tree (doto (Tree. tab-folder @twitter-style)
                  (.addSelectionListener (proxy [SelectionAdapter] []
                                           (widgetDefaultSelected [e]
                                             (let [status (.getData (.item e))
                                                   user (.getUser status)
                                                   name (.getName user)
                                                   text (text)]
                                               (doto text
                                                 (.setData "status_id" (.getId status))
                                                 (.setText name)
                                                 (.setSelection (.getCharCount text)))))))
                  (.addTreeListener (proxy [TreeListener] []
                                      (treeExpanded [e]
                                        (let [item (.getItem (.item e) 0)
                                              status (.getData item)
                                              id (.getInReplyToStatusId status)]
                                          (when (not= id -1)
                                            (treeitem item (.showStatus twitter id))))))))]
       (doseq [status (reverse timeline)]
         (set-treeitem tree status))
       [tree string])))

(defn home []
  (twitter-client "Home" (.getHomeTimeline twitter)))

(defmacro doitems [meth]
  `(let [tree# (.. (tab-folder) getSelection getControl)
         twitter# (twitter)
         items# (reverse (.getSelection tree#))]
     (doseq [item# items#]
       (. twitter# ~meth (.getId (.getData item#))))))

(defn retweet [] (doitems retweetStatus))

(defn fav [] (doitems createFavorite))

(defmenu twitter-menu "T&witter"
  (fn [menu]
    (make-menu-item menu "&Home" home @home-key)
    (make-menu-item menu "&Tweet" tweet @tweet-key)
    (make-menu-item menu "&Update" update @update-key)
    (make-menu-item menu "&Retweet" retweet @retweet-key)
    (make-menu-item menu "&Fav" fav @fav-key)
    (make-separator menu)
    (make-menu-item menu "&Login" login)
    (make-menu-item menu "&PIN" pin)))
