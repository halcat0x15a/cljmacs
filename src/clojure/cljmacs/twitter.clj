(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [clojure.string :only (join)]
        [cljmacs.core]
        [cljmacs.browser :only (browser)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory TwitterStreamFactory UserStreamAdapter TwitterException Paging StatusUpdate Query]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem TreeEditor StyledText StyleRange VerifyKeyListener]
           [org.eclipse.swt.events SelectionAdapter TreeAdapter MouseAdapter]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Tree TreeItem]))

(def consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(defconfig filename ".access-token" string?)

(defstyle twitter-style SWT/MULTI SWT/BORDER SWT/FULL_SELECTION)

(defshortcut home-key [ctrl shift] \H)

(defshortcut tweet-key [ctrl shift] \T)

(defshortcut reply-key [ctrl shift] \R)

(defshortcut cancel-key [ctrl shift] \C)

(defshortcut update-key [ctrl shift] \U)

(defshortcut retweet-key [ctrl alt shift] \R)

(defshortcut fav-key [ctrl alt shift] \F)

(defshortcut search-key [ctrl alt shift] \S )

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

(def twitter-stream
  (let [access-token (.getOAuthAccessToken twitter)
        factory (TwitterStreamFactory.)
        user-stream (proxy [UserStreamAdapter] []
                      (.onStatus [s]
                        (println s)))
        twitter-stream (doto (.getInstance factory)
                         (.addListener user-stream)
                         (.setOAuthConsumer consumer-key consumer-secret))]
    (if access-token
      (doto twitter-stream
        (.setOAuthAccessToken access-token))
      twitter-stream)))

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

(def search)

(defn make-styled-text [parent tree-item]
  (let [status (.getData tree-item)
        url-entities (.getURLEntities status)
        hashtag-entities (.getHashtagEntities status)
        text (.getText tree-item)]
    (letfn [(open [e]
              (let [widget (.widget e)
                    offset (- (.getCaretOffset widget) (.getOffsetAtLine widget 1))]
                (letfn [(entity-fn [entities f]
                          (doseq [entity entities]
                            (let [start (inc (.getStart entity))
                                  end (.getEnd entity)]
                              (when (some #(= % offset) (range start end))
                                (f entity)))))]
                  (entity-fn url-entities #(browser (str (.getURL %))))
                  (entity-fn hashtag-entities #(search (.getText %))))))]
      (doto (StyledText. parent SWT/NONE)
        (.addMouseListener (proxy [MouseAdapter] []
                             (mouseDown [e]
                               (open e))))
        (.addVerifyKeyListener (proxy [VerifyKeyListener] []
                                 (verifyKey [e]
                                   (open e))))
        (.setEditable false)
        (.setText text)))))

(comment
(defn set-icon [tree-item status]
  (let [display (.getDisplay @shell)
        retweet? (.isRetweetedByMe status)
        favorited? (.isFavorited status)
        set-icon #(.setText tree-item (str (.getText tree-item) \newline %))]
    (cond (and retweet? favorited?) (set-icon "RT☆")
          retweet? (set-icon "RT")
          favorited? (set-icon "☆"))))
)

(defn make-tree-item [tree status]
  (let [display (.getDisplay tree)
        user (.getUser status)
        screen-name (.getScreenName user)
        name (.getName user)
        text (.getText status)
        url (.getProfileImageURL user)
        string (str screen-name \space name \newline text)
        image (Image. display (input-stream url))
        tree-item (TreeItem. tree SWT/NONE 0)]
    (doto tree-item
      (.setData status)
      (.setImage image)
      (.setText string))
    tree-item))

(defn set-tree-item [tree status]
  (let [item (make-tree-item tree status)
        id (.getInReplyToStatusId status)]
    (when-let [rt (.getRetweetedStatus status)]
      (make-tree-item item rt))
    (when (not= id -1)
      (make-tree-item item (.showStatus twitter id)))
    item))

(defn update []
  (let [tree (control)
        id (.. tree (getItem 0) getData getId)
        function (.getData tree "function")
        query (doto (.getData tree "query")
                (.setSinceId id))
        timeline (reverse (function query))]
    (doseq [status timeline]
      (.setSelection tree (set-tree-item tree status)))))

(defn cancel []
  (doto (text)
    (.setText "")
    (.setData "status_id" nil)))

(defn tweet
  ([]
     (let [text (text)
           string (.getText text)]
       (tweet string)))
  ([string]
     (if-let [string string]
       (let [text (text)
             status (StatusUpdate. string)]
         (if-let [id (.getData text "status_id")]
           (.setInReplyToStatusId status id))
         (.updateStatus twitter status)
         (cancel)
         (update)))))

(defn reply []
  (let [tree (control)
        text (text)
        item (first (.getSelection tree))
        status (.getData item)
        id (.getId status)
        user (.getUser status)
        name (.getScreenName user)
        string (str \@ name \space)]
    (doto text
      (.setData "status_id" id)
      (.setText string)
      (.setSelection (.getCharCount text))
      (.setFocus))
    nil))

(defwidget twitter-client [string function query]
  (fn [tab-folder tab-item]
    (let [tree (doto (Tree. tab-folder @twitter-style)
                 (.addTreeListener (proxy [TreeAdapter] []
                                     (treeExpanded [e]
                                       (let [item (.. e item (getItem 0))
                                             id (.. item getData getInReplyToStatusId)]
                                         (when (not= id -1)
                                           (make-tree-item item (.showStatus twitter id)))))))
                 (.setData "function" function)
                 (.setData "query" query))
          tree-editor (TreeEditor. tree)]
      (set! (.grabHorizontal tree-editor) true)
      (.addSelectionListener tree (proxy [SelectionAdapter] []
                                    (widgetSelected [e]
                                      (if-let [editor (.getEditor tree-editor)]
                                        (.dispose editor)))
                                    (widgetDefaultSelected [e]
                                      (let [item (.item e)
                                            styled-text (make-styled-text tree item)]
                                        (.setEditor tree-editor styled-text item)
                                        (.deselect tree item)))))
      (doseq [status (reverse (function query))]
        (set-tree-item tree status))
      [tree string])))

(defmacro timeline-method [name method & arg]
  `(twitter-client ~name #(. twitter ~method ~@arg %) (Paging.)))

(defn home []
  (timeline-method "Home" getHomeTimeline))

(defn mentions []
  (timeline-method "Mentions" getMentions))

(defn retweeted-by-me []
  (timeline-method "Retweeted By Me" getRetweetedByMe))

(defn retweeted-to-me []
  (timeline-method "Retweeted To Me" getRetweetedToMe))

(defn retweets-of-me []
  (timeline-method "Retweets Of Me" getRetweetsOfMe))

(defn retweeted-by-user [name]
  (timeline-method (str "Retweeted By " name) getRetweetedByUser name))

(defn retweeted-to-user [name]
  (timeline-method (str "Retweeted To " name) getRetweetedToUser name))

(defn user [name]
  (timeline-method name getUserTimeline name))

(defn search
  ([] (let [text (text)]
        (search (.getText text))))
  ([string]
     (let [query (Query. string)
           status #(.showStatus twitter (.getId %))]
       (twitter-client string #(map status (.. twitter (search %) getTweets)) query))))

(defn user-list [name id]
  (timeline-method name getUserListStatuses id))

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
    (make-separator menu)
    (make-menu-item menu "&Tweet" tweet @tweet-key)
    (make-menu-item menu "&Reply" reply @reply-key)
    (make-menu-item menu "&Cancel" cancel @cancel-key)
    (make-menu-item menu "&Update" update @update-key)
    (make-separator menu)
    (make-menu-item menu "R&etweet" retweet @retweet-key)
    (make-menu-item menu "&Fav" fav @fav-key)
    (make-separator menu)
    (make-menu-item menu "&Search" search @search-key)
    (make-separator menu)
    (make-menu-item menu "&Mentions" mentions)
    (make-menu-item menu "&Retweeted By Me" retweeted-by-me)
    (make-menu-item menu "&Retweeted To Me" retweeted-to-me)
    (make-menu-item menu "&Retweets Of Me" retweets-of-me)
    (make-separator menu)
    (if-let [access-token (.getOAuthAccessToken twitter)]
      (let [id (.getUserId access-token)
            lists (.getAllUserLists twitter id)]
        (doseq [list lists]
          (let [name (.getName list)
                id (.getId list)]
            (make-menu-item menu name #(user-list name id))))))))
