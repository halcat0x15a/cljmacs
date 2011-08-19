(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [clojure.string :only (join)]
        [cljmacs.core]
        [cljmacs.browser :only (browser)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory TwitterException StatusUpdate]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem TreeEditor StyledText StyleRange]
           [org.eclipse.swt.events SelectionAdapter TreeAdapter]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Tree TreeItem]))

(def consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(defconfig filename ".access-token" string?)

(defstyle twitter-style SWT/MULTI SWT/BORDER SWT/FULL_SELECTION)

(defshortcut home-key [ctrl shift] \H)

(defshortcut tweet-key [ctrl shift] \T)

(defshortcut update-key [ctrl shift] \U)

(defshortcut retweet-key [ctrl shift] \R)

(defshortcut fav-key [ctrl shift] \F)

(defn- load-access-token []
  (with-open [ois (ObjectInputStream. (input-stream @filename))]
    (.readObject ois)))

(defn- store-access-token [access-token]
  (with-open [oos (ObjectOutputStream. (output-stream @filename))]
    (.writeObject oos access-token)))

(def twitter
  (let [twitter (doto (.getInstance (TwitterFactory.))
          (.setOAuthConsumer consumer-key consumer-secret))]
    (try
      (doto twitter
        (.setOAuthAccessToken (load-access-token)))
      (catch FileNotFoundException _ twitter))))

(def request-token (ref nil))

(defn login []
  (dosync
   (let [token (.getOAuthRequestToken twitter)
         url (.getAuthorizationURL token)]
     (ref-set request-token token)
     (browser url))))

(defn pin [code]
  (dosync
   (let [access-token (.getOAuthAccessToken twitter @request-token code)]
     (ref-set request-token nil)
     (store-access-token access-token)
     (alter twitter #(.setOAuthAccessToken %)))))

(defn make-color [color]
  (let [display (.getDisplay @shell)
        parse #(Integer/parseInt % 16)
        [r g b] (map (comp parse join) (partition 2 color))]
    (Color. display r g b)))

(defn make-styled-text [parent tree-item]
  (doto (StyledText. parent SWT/NONE)
    (.setEditable false)
    (.setText (.getText tree-item))))

(defn make-tree-item [tree status]
  (let [display (.getDisplay tree)
        user (.getUser status)
        screen-name (.getScreenName user)
        name (.getName user)
        text (.getText status)
        string (str screen-name \space name \newline text)
        image (Image. display (input-stream (.. status getUser getProfileImageURL)))]
    (doto (TreeItem. tree SWT/NONE 0)
      (.setData status)
      (.setImage image)
      (.setText string))))

(defn set-tree-item [tree status]
  (let [item (make-tree-item tree status)
        id (.getInReplyToStatusId status)]
    (when-let [rt (.getRetweetedStatus status)]
      (make-tree-item item rt))
    (when (not= id -1)
      (make-tree-item item (.showStatus twitter id)))
    item))

(defn update []
  (let [tree (.. (tab-folder) getSelection getControl)
        item (.getItem tree 0)
        data (.getData item)
        tl (reverse (take-while #(not= data %) (.getHomeTimeline twitter)))]
    (doseq [s tl]
      (.setSelection tree (set-tree-item tree s)))))

(defn tweet []
  (let [text (text)
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
                 (.addTreeListener (proxy [TreeAdapter] []
                                     (treeExpanded [e]
                                       (let [item (.getItem (.item e) 0)
                                             status (.getData item)
                                             id (.getInReplyToStatusId status)]
                                         (when (not= id -1)
                                           (make-tree-item item (.showStatus twitter id))))))))
          tree-editor (TreeEditor. tree)]
      (set! (.grabHorizontal tree-editor) true)
      (.addSelectionListener tree (proxy [SelectionAdapter] []
                                    (widgetSelected [e]
                                      (if-let [editor (.getEditor tree-editor)]
                                        (.dispose editor))
                                      (let [item (.item e)
                                            styled-text (make-styled-text tree item)]
                                        (.setEditor tree-editor styled-text item)))))
      (doseq [status (reverse timeline)]
        (set-tree-item tree status))
      [tree string])))

(defn home []
  (twitter-client "Home" (.getHomeTimeline twitter)))

(defmacro doitems [meth]
  `(let [tree# (.. (tab-folder) getSelection getControl)
         twitter# twitter
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
    (make-menu-item menu "&Fav" fav @fav-key)))
