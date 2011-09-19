(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [cljmacs.core]
        [cljmacs.browser :only (web-browser)]
        [cljmacs.viewer :only (viewer)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory TwitterStreamFactory UserStreamAdapter TwitterException Paging StatusUpdate Query]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.browser Browser]
           [org.eclipse.swt.custom CTabItem TreeEditor CLabel StyledText]
           [org.eclipse.swt.events SelectionAdapter TreeAdapter MouseAdapter KeyAdapter]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.layout GridLayout GridData]
           [org.eclipse.swt.widgets Tree TreeItem Composite Text]
           [cljmacs Widget Menu]))

(def consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(def function-key "function")

(def query-key "query")

(defproperty filename ".access-token")

(defproperty lines-visible true)

(defstyle twitter-style SWT/MULTI SWT/BORDER SWT/FULL_SELECTION)

(defshortcut home-timeline-key \H ctrl shift)

(defshortcut tweet-key \T ctrl shift)

(defshortcut reply-key \R ctrl shift)

(defshortcut cancel-key \C ctrl shift)

(defshortcut update-status-key \U ctrl shift)

(defshortcut retweet-key \R ctrl alt shift)

(defshortcut fav-key \F ctrl alt shift)

(defshortcut twitter-search-key \S ctrl alt shift)

(defn- load-access-token []
  (with-open [ois (ObjectInputStream. (input-stream @filename))]
    (.readObject ois)))

(defn- store-access-token [access-token]
  (with-open [oos (ObjectOutputStream. (output-stream @filename))]
    (.writeObject oos access-token)))

(def twitter (doto (.getInstance (TwitterFactory.))
               (.setOAuthConsumer consumer-key consumer-secret)))

(declare home-timeline twitter-search)

(defwidget login-browser [frame]
  (proxy [Widget] [frame]
    (create_control [tab-folder tab-item]
      (let [request-token (.getOAuthRequestToken twitter)
            url (.getAuthorizationURL request-token)
            composite (doto (Composite. tab-folder SWT/BORDER)
                        (.setLayout (GridLayout. 1 false)))
            browser (doto (Browser. composite SWT/BORDER)
                      (.setLayoutData (GridData. SWT/FILL SWT/FILL true true))
                      (.setUrl url))
            text (doto (Text. composite (bit-or SWT/SINGLE SWT/BORDER))
                   (.setLayoutData (GridData. SWT/FILL SWT/END true false))
                   (.addSelectionListener (proxy [SelectionAdapter] []
                                            (widgetDefaultSelected [e]
                                              (try
                                                (let [code (.. e widget getText)
                                                      access-token (.getOAuthAccessToken twitter request-token code)]
                                                  (store-access-token access-token)
                                                  (.setOAuthAccessToken twitter access-token)
                                                  (home-timeline frame))
                                                (catch TwitterException _ (message frame "auth failed")))))))]
        (.setText tab-item "OAuth")
        composite))))

(defmacro auth [frame & body]
  `(try
     (.setOAuthAccessToken twitter (load-access-token))
     ~@body
     (catch FileNotFoundException _# (login-browser ~frame))))

(defn make-tree-item [tree status]
  (let [display (.getDisplay tree)
        user (.getUser status)
        screen-name (.getScreenName user)
        name (.getName user)
        text (.getText status)
        date (.getCreatedAt status)
        string (str screen-name \space name \newline text \newline date)
        image (Image. display (input-stream (.getProfileImageURL user)))
        tree-item (TreeItem. tree SWT/NONE 0)]
    (doto tree-item
      (.setData status)
      (.setImage image)
      (.setText string))
    tree-item))

(defn set-tree-item [tree status]
  (let [status (if-let [rt (.getRetweetedStatus status)] rt status)
        item (make-tree-item tree status)
        id (.getInReplyToStatusId status)]
    (when (not= id -1)
      (make-tree-item item (.showStatus twitter id)))
    item))

(defwidgetm update-status [frame] :twitter-client
  (fn [tree]
    (auth frame
      (let [id (.. tree (getItem 0) getData getId)
            function (.getData tree function-key)
            query (doto (.getData tree query-key)
                    (.setSinceId id))
            timeline (reverse (function query))]
        (doseq [status timeline]
          (.setSelection tree (set-tree-item tree status)))))))

(defun tweet [frame status]
  (auth frame
    (.updateStatus twitter status)))

(defn open-entity [e status frame]
  (let [text (.widget e)
        offset (.getCaretOffset text)
        contains (fn [entity] (some #(= % offset) (range (.getStart entity) (.getEnd entity))))]
    (doseq [entity (.getURLEntities status)]
      (when (contains entity)
        (web-browser frame (.getDisplayURL entity))))
    (doseq [entity (.getHashtagEntities status)]
      (when (contains entity)
        (twitter-search frame (str \# (.getText entity)))))))

(defwidget status-info [frame status]
  (proxy [Widget] [frame]
    (create_control [tab-folder tab-item]
      (let [display (.getDisplay tab-folder)
            composite (doto (Composite. tab-folder SWT/BORDER)
                        (.setLayout (GridLayout. 1 false)))
            user (.getUser status)
            screen-name (.getScreenName user)
            name (.getName user)
            label (doto (CLabel. composite SWT/NONE)
                    (.setLayoutData (GridData. SWT/FILL SWT/BEGINNING true false))
                    (.setText (str screen-name \newline name))
                    (.setImage (Image. display (input-stream (.getProfileImageURL user)))))
            styled-text (doto (StyledText. composite SWT/WRAP)
                   (.addMouseListener (proxy [MouseAdapter] []
                                        (mouseDown [e]
                                          (open-entity e status frame))))
                   (.addKeyListener (proxy [KeyAdapter] []
                                      (keyPressed [e]
                                        (when (= (.keyCode e) (int SWT/CR))
                                          (open-entity e status frame)))))
                   (.setLayoutData (GridData. SWT/FILL SWT/FILL true true))
                   (.setBackground (.getSystemColor display SWT/COLOR_WIDGET_BACKGROUND))
                   (.setEditable false)
                   (.setText (.getText status)))
            text (doto (Text. composite (bit-or SWT/SINGLE SWT/BORDER))
                   (.setLayoutData (GridData. SWT/FILL SWT/END true false))
                   (.addSelectionListener (proxy [SelectionAdapter] []
                                            (widgetDefaultSelected [e]
                                              (tweet frame (doto (StatusUpdate. (.. e widget getText))
                                                             (.setInReplyToStatusId (.getId status)))))))
                   (.setText (str \@ screen-name \space)))]
        (.setText tab-item screen-name)
        composite))))

(defwidget twitter-client [frame string function query]
  (proxy [Widget] [frame]
    (create_control [tab-folder tab-item]
      (let [tree (doto (Tree. tab-folder @twitter-style)
                   (.addTreeListener (proxy [TreeAdapter] []
                                       (treeExpanded [e]
                                         (let [item (.. e item (getItem 0))
                                               id (.. item getData getInReplyToStatusId)]
                                           (when (not= id -1)
                                             (make-tree-item item (.showStatus twitter id)))))))
                   (.addSelectionListener (proxy [SelectionAdapter] []
                                            (widgetDefaultSelected [e]
                                              (let [status (.. e item getData)]
                                                (status-info frame status)))))
                   (.setLinesVisible @lines-visible)
                   (.setData function-key function)
                   (.setData query-key query))]
        (.setText tab-item string)
        (doseq [status (reverse (function query))]
          (set-tree-item tree status))
        tree))))

(defmacro timeline-method [frame string method & arg]
  `(twitter-client ~frame ~string #(. twitter ~method ~@arg %) (Paging.)))

(defmacro deftlmethod [name parameter string method]
  `(defun ~name [frame# ~@parameter]
     (auth frame#
       (twitter-client frame# ~string #(. twitter ~method ~@parameter %) (Paging.)))))

(deftlmethod home-timeline [] "Home Timeline" getHomeTimeline)

(deftlmethod mentions [] "Mentions" getMentions)

(deftlmethod retweeted-by-me [] "Retweeted By Me" getRetweetedByMe)

(deftlmethod retweeted-to-me [] "Retweeted To Me" getRetweetedToMe)

(deftlmethod retweets-of-me [] "Retweets Of Me" getRetweetsOfMe)

(deftlmethod retweeted-by-user [name] (str "Retweeted By " name) getRetweetedByUser)

(deftlmethod retweeted-to-user [name] (str "Retweeted To " name) getRetweetedToUser)

(deftlmethod user [name] (str name "Timeline") getUserTimeline)

(defun twitter-search [frame string] :twitter-clinet
  (let [query (Query. string)
        status #(.showStatus twitter (.getId %))]
    (twitter-client frame string #(map status (.. twitter (search %) getTweets)) query)))

(defun user-list [frame name]
  (auth frame
    (let [lists (filter #(= (.getName %) name) (.getAllUserLists twitter (.. twitter getOAuthAccessToken getUserId)))]
      (if (empty? lists)
        (message frame "list does not exist")
        (timeline-method frame name getUserListStatuses (.getId (first lists)))))))

(defun twitter-menu [frame]
  (let [menu (create-menu (.shell frame) "&Twitter")]
    (doto menu
      (create-item "&Tweet" tweet frame @tweet-key)
      (create-item "&Update Status" update-status frame @update-status-key)
      (create-item "&Home Timeline" home-timeline frame @home-timeline-key)
      (create-item "&Mentions" mentions frame))
    (doto (create-menu menu "&Retweets")
      (create-item "Retweeted &By Me" retweets-of-me frame)
      (create-item "Retweeted &To Me" retweeted-to-me frame)
      (create-item "Retweets &Of Me" retweets-of-me frame))
    (doto menu
      (create-item "User &List" user-list frame)
      (create-item)
      (create-item "&Search" twitter-search frame @twitter-search-key))))
