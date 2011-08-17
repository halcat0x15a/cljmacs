(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [clojure.contrib.monads]
        [cljmacs.core]
        [cljmacs.browser :only (browser)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory TwitterException StatusUpdate]
           [org.eclipse.jface.action MenuManager Separator]
           [org.eclipse.jface.dialogs MessageDialog InputDialog]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem]
           [org.eclipse.swt.events SelectionAdapter TreeListener]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Tree TreeItem]))

(def consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(defconfig filename ".access-token" string?)

(defconfig style (bit-or SWT/MULTI SWT/BORDER) integer?)

(defshortcut home-key [ctrl shift] \H)

(defshortcut tweet-key [ctrl shift] \T)

(defshortcut update-key [ctrl shift] \U)

(defshortcut retweet-key [ctrl shift] \R)

(defshortcut fav-key [ctrl shift] \F)

(def twitter (doto (.getInstance (TwitterFactory.))
               (.setOAuthConsumer consumer-key consumer-secret)))

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
     (browser (shell) url))))

(defn pin []
  (let [token @request-token
        dialog (InputDialog. (shell) "PIN Code" nil nil nil)]
    (when (= InputDialog/OK (.open dialog))
      (dosync
       (ref-set request-token nil)
       (store-access-token (.getOAuthAccessToken twitter token (.getValue dialog)))))))

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
  ([] (update (shell)))
  ([shell]
     (let [tree (control shell)
           item (.getItem tree 0)
           data (.getData item)
           tl (reverse (take-while #(not= data %) (.getHomeTimeline twitter)))]
       (doseq [s tl]
         (.setSelection tree (set-treeitem tree s))))))

(defn tweet
  ([] (tweet nil))
  ([id]
     (let [shell (shell)
           text (text shell)
           status (StatusUpdate. (.getText text))]
       (when id
         (.setInReplyToStatusId status id))
       (.updateStatus twitter status)
       (.setText text "")
       (update twitter shell))))

(defn twitter-client
  ([] (twitter-client (.getHomeTimeline twitter)))
  ([timeline] (twitter-client timeline (shell)))
  ([timeline shell]
     (.setOAuthAccessToken twitter (load-access-token))
     (let [tabfolder (tabfolder shell)
           tabitem (doto (CTabItem. tabfolder SWT/CLOSE)
                     (.setText "Home"))
           tree (doto (Tree. tabfolder @style)
                  (.addSelectionListener
                   (proxy [SelectionAdapter] []
                     (widgetDefaultSelected [e]
                       (let [status (.getData (.item e))
                             dialog (InputDialog. shell "Reply" (.getText status) nil nil)]
                         (when (= InputDialog/OK (.open dialog))
                           (tweet (.getId status)))))))
                  (.addTreeListener
                   (proxy [TreeListener] []
                     (treeExpanded [e]
                       (let [item (.getItem (.item e) 0)
                             status (.getData item)
                             id (.getInReplyToStatusId status)]
                         (when (not= id -1)
                           (treeitem item (.showStatus twitter id))))))))]
       (doseq [status (reverse timeline)]
         (set-treeitem tree status))
       (doto tabitem
         (.setControl tree))
       (.setSelection tabfolder tabitem))))

(defmacro doitems [meth]
  `(let [tree# (control)
         twitter# (twitter)
         items# (reverse (.getSelection tree#))]
     (doseq [item# items#]
       (. twitter# ~meth (.getId (.getData item#))))))

(defn retweet [] (doitems retweetStatus))

(defn fav [] (doitems createFavorite))

(defn twittermenu []
  (doto (MenuManager. "T&witter")
    (.add (action "&Home\t" twitter-client @home-key))
    (.add (action "&Tweet\t" tweet @tweet-key))
    (.add (action "&Update\t" update @update-key))
    (.add (action "&Retweet\t" retweet @retweet-key))
    (.add (action "&Fav\t" fav @fav-key))
    (.add (Separator.))
    (.add (action "&Login\t" login))
    (.add (action "&PIN\t" pin))))
