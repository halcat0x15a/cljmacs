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
  (proxy [Widget] [frame]
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

(defun browser-menu [frame]
  (let [shell (.shell frame)]
    (create-menu shell (.getMenuBar shell) "&Browser"
                 (create-item "&Homepage" frame open-homepage) @open-homepage-key)
    (create-item "&Open URL" frame open-url @open-url-key)))
