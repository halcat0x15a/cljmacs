(ns cljmacs.browser
  (:use [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.browser Browser OpenWindowListener ProgressAdapter LocationAdapter TitleListener]))

(defconfig homepage "http://google.com/" string?)

(defstyle browser-style SWT/NONE)

(defshortcut open-homepage-key [ctrl alt] \H)

(defshortcut open-url-key [ctrl alt] \O)

(defwidget browser [url]
  (fn [tab-folder tab-item]
    (let [text (text)
          browser (doto (Browser. tab-folder @browser-style)
                    (.addLocationListener (proxy [LocationAdapter] []
                                            (changed [e]
                                              (message (.location e)))))
                    (.addTitleListener (proxy [TitleListener] []
                                         (changed [e]
                                           (doto tab-item
                                             (.setText (.title e))))))
                    (.addProgressListener (proxy [ProgressAdapter] []))
                    (.setUrl url))]
      [browser ""])))

(defn open-homepage []
  (browser @homepage))

(defn open-url []
  (let [browser (.. (tab-folder) getSelection getControl)
        text (text)
        url (.getText text)]
    (.setUrl browser url)))

(defmenu browser-menu "&Browser"
  (fn [menu]
    (make-menu-item menu "&Homepage" open-homepage @open-homepage-key)
    (make-menu-item menu "&Open URL" open-url @open-url-key)))
