(ns cljmacs.browser
  (:use [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.browser Browser OpenWindowListener ProgressAdapter LocationAdapter TitleListener]
           [cljmacs Widget Menu]))

(defstyle browser-style SWT/NONE)

(defproperty homepage "http://google.com/")

(defshortcut open-homepage-key \H ctrl alt)

(defshortcut open-url-key \O ctrl alt)

(defwidget browser [frame url]
  (proxy [Widget] [(.tab_folder frame)]
    (create_control [tab-folder tab-item]
      (let [text (.text frame)]
        (doto (Browser. tab-folder @browser-style)
          (.addLocationListener (proxy [LocationAdapter] []
                                  (changed [e]
                                    (message text (.location e)))))
          (.addTitleListener (proxy [TitleListener] []
                               (changed [e]
                                 (doto tab-item
                                   (.setText (.title e))))))
          (.addProgressListener (proxy [ProgressAdapter] []))
          (.setUrl url))))))

(defun open-homepage [frame]
  (browser frame @homepage))

(defun open-url [frame]
  (let [browser (.. frame control)
        url (.. frame text getText)]
    (.setUrl browser url)))

(defmenu browser-menu [frame]
  (doto (Menu. frame "&Browser" 0)
    (.create_item "&Homepage" #(open-homepage frame) @open-homepage-key)
    (.create_item "&Open URL" #(open-url frame) @open-url-key)))
