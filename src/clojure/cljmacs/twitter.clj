(ns cljmacs.twitter
  (:use [clojure.java.io :only (input-stream output-stream)]
        [cljmacs.core]
        [cljmacs.browser :only (browser)])
  (:import [java.io FileNotFoundException ObjectInputStream ObjectOutputStream]
           [twitter4j Twitter TwitterFactory TwitterStreamFactory UserStreamAdapter TwitterException Paging StatusUpdate Query]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabItem TreeEditor StyledText StyleRange VerifyKeyListener]
           [org.eclipse.swt.events SelectionAdapter TreeAdapter MouseAdapter]
           [org.eclipse.swt.graphics Image]
           [org.eclipse.swt.widgets Tree TreeItem]
           [cljmacs Widget Menu]))

(def consumer-key "74PPTX7J76NwkE9YN2VmWg")

(def consumer-secret "i1O7q8yLU4rR3bsykEfd4BmXIrLBQCKn3M4UeQhw")

(defproperty filename ".access-token")

(defstyle twitter-style SWT/MULTI SWT/BORDER SWT/FULL_SELECTION)

(defshortcut home-timeline-key \H ctrl shift)

(defshortcut tweet-key \T ctrl shift)

(defshortcut reply-key \R ctrl shift)

(defshortcut cancel-key \C ctrl shift)

(defshortcut update-key \U ctrl shift)

(defshortcut retweet-key \R ctrl alt shift)

(defshortcut fav-key \F ctrl alt shift)

(defshortcut search-key \S ctrl alt shift)

(defn- load-access-token []
  (with-open [ois (ObjectInputStream. (input-stream @filename))]
    (.readObject ois)))

(defn- store-access-token [access-token]
  (with-open [oos (ObjectOutputStream. (output-stream @filename))]
    (.writeObject oos access-token)))

(def twitter (doto (.getInstance (TwitterFactory.))
               (.setOAuthConsumer consumer-key consumer-secret)))

(defn login [frame]
  (let [request-token (.getOAuthRequestToken twitter)
        url (.getAuthorizationURL request-token)]
    (.. frame text (setData request-token))
    (browser frame url)
    nil))

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
  (let [item (make-tree-item tree status)
        id (.getInReplyToStatusId status)]
    (when-let [rt (.getRetweetedStatus status)]
      (make-tree-item item rt))
    (when (not= id -1)
      (make-tree-item item (.showStatus twitter id)))
    item))

(defwidget twitter-client [frame string function query]
  (proxy [Widget] [frame]
    (create_control [tab-folder tab-item]
      (try
        (.setOAuthAccessToken twitter (load-access-token))
        (let [tree (doto (Tree. tab-folder @twitter-style)
                     (.addTreeListener (proxy [TreeAdapter] []
                                         (treeExpanded [e]
                                           (let [item (.. e item (getItem 0))
                                                 id (.. item getData getInReplyToStatusId)]
                                             (when (not= id -1)
                                               (make-tree-item item (.showStatus twitter id)))))))
                     (.setData "function" function)
                     (.setData "query" query))]
          (.setText tab-item string)
          (doseq [status (reverse (function query))]
            (set-tree-item tree status))
          tree)
        (catch FileNotFoundException e (login frame))))))

(defmacro deftlmethod [name params string method]
  `(defun ~name [frame# ~@params]
     (twitter-client frame# ~string #(. twitter ~method ~@params %) (Paging.))))

(deftlmethod home-timeline [] "Home Timeline" getHomeTimeline)

(deftlmethod mentions [] "Mentions" getMentions)

(deftlmethod retweeted-by-me [] "Retweeted By Me" getRetweetedByMe)

(deftlmethod retweeted-to-me [] "Retweeted To Me" getRetweetedToMe)

(deftlmethod retweets-of-me [] "Retweets Of Me" getRetweetsOfMe)

(deftlmethod retweeted-by-user [name] (str "Retweeted By " name) getRetweetedByUser)

(deftlmethod retweeted-to-user [name] (str "Retweeted To " name) getRetweetedToUser)

(deftlmethod user [name] name getUserTimeline)

(deftlmethod user-list [name] name getUserListStatuses)

(defun search [frame string]
  (let [query (Query. string)
        status #(.showStatus twitter (.getId %))]
    (twitter-client frame string #(map status (.. twitter (search %) getTweets)) query)))

(defn auth [frame]
  (let [text (.text frame)
        request-token (.getData text)
        access-token (.getOAuthAccessToken twitter request-token (.getText text))]
    (store-access-token access-token)
    (.setOAuthAccessToken twitter access-token)
    (home-timeline frame)))

(defmenu twitter-menu [frame]
  (doto (Menu. frame "&Twitter")
    (.create_item "&Home Timeline" (menu-run-or-apply frame home-timeline) @home-timeline-key)
    (.create_item "&Mentions" (menu-run-or-apply frame mentions))
    (.create_item "Retweeted &By Me" (menu-run-or-apply frame retweets-of-me))
    (.create_item "Retweeted &To Me" (menu-run-or-apply frame retweeted-to-me))
    (.create_item "Retweets &Of Me" (menu-run-or-apply frame retweets-of-me))
    (.create_separator)
    (.create_item "&Login" (menu-run-or-apply frame login))
    (.create_item "&Auth" (menu-run-or-apply frame auth))))
