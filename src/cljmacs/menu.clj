(ns cljmacs.menu
  (:use [cljmacs.core]
        [cljmacs.shortcut-key]
        [cljmacs.tab :only (close-tab)]
        [cljmacs.editor :only (new-file open save save-as)]
        [cljmacs.browser :only (browser)]
        [cljmacs.twitter :only (twitter-client tweet-text update)])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.events SelectionAdapter]
           [org.eclipse.swt.widgets Shell Menu MenuItem]))

(defn- #^Menu menu [#^Shell shell]
  (Menu. shell SWT/DROP_DOWN))

(defn- #^MenuItem parent-menuitem [#^Menu menubar #^Menu menu #^String s]
  (doto (MenuItem. menubar SWT/CASCADE)
    (.setText s)
    (.setMenu menu)))

(defn- #^MenuItem menuitem [#^Menu menu #^String string shortcut-key f]
  (doto (MenuItem. menu SWT/PUSH)
    (.setText (str string (shortcut-key-str shortcut-key)))
    (.setAccelerator (accelerator shortcut-key))
    (.addSelectionListener
     (proxy [SelectionAdapter] []
       (widgetSelected [e]
         (f))))))

(defn- #^MenuItem filemenu [#^Shell shell #^Menu menubar]
  (let [filemenu (menu shell)]
    (parent-menuitem menubar filemenu "&File")
    (menuitem filemenu "&New File\t" @new-file-key #(new-file shell))
    (menuitem filemenu "&Open\t" @open-key #(open shell))
    (menuitem filemenu "&Save\t" @save-key #(save-as shell))
    (menuitem filemenu "&Quit\t" @quit-key #(.close shell))
    filemenu))

(defn- #^MenuItem tabmenu [#^Shell shell #^Menu menubar]
  (let [tabmenu (menu shell)]
    (parent-menuitem menubar tabmenu "&Tab")
    (menuitem tabmenu "&Close\t" @close-key #(close-tab shell))
    tabmenu))

(defn #^Menu browsermenu [#^Shell shell #^Menu menubar]
  (let [browsermenu (menu shell)]
    (parent-menuitem menubar browsermenu "&Browser")
    (menuitem browsermenu "&Homepage\t" @homepage-key #(browser shell))
    browsermenu))

(defn- #^Menu twittermenu [#^Shell shell #^Menu menubar]
  (let [twittermenu (menu shell)]
    (parent-menuitem menubar twittermenu "T&witter")
    (menuitem twittermenu "&Home\t" @home-key #(twitter-client shell))
    (menuitem twittermenu "&Tweet\t" @tweet-key #(tweet-text shell))
    (menuitem twittermenu "&Update\t" @update-key #(update shell))
    twittermenu))

(defn- #^Menu helpmenu [#^Shell shell #^Menu menubar]
  (let [helpmenu (menu shell)
        help (parent-menuitem menubar helpmenu "&Help")]
    helpmenu))

(defn #^Menu menubar [#^Shell shell]
  (let [menubar (Menu. shell SWT/BAR)]
    (filemenu shell menubar)
    (tabmenu shell menubar)
    (browsermenu shell menubar)
    (twittermenu shell menubar)
    (helpmenu shell menubar)
    menubar))
