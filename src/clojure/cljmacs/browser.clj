(ns cljmacs.browser
  (:use [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.browser Browser OpenWindowListener ProgressAdapter LocationAdapter TitleListener]
           [cljmacs Widget Menu]))

(defstyle browser-style SWT/NONE)

(defproperty homepage "http://google.com/")

(defproperty search-query "http://www.google.com/search?&q=")

(defproperty javascript-enabled true)

(defshortcut back-key \B ctrl alt)

(defshortcut forward-key \F ctrl alt)

(defshortcut refresh-key \R ctrl alt)

(defshortcut stop-key \S ctrl alt)

(defshortcut open-homepage-key \H ctrl alt)

(defshortcut web-search-key \S ctrl alt shift)

(defwidget web-browser [frame url]
  (proxy [Widget] [frame]
    (create_control [tab-folder tab-item]
      (let [text (.text frame)]
        (doto (Browser. tab-folder @browser-style)
          (.addLocationListener (proxy [LocationAdapter] []
                                  (changed [e]
                                    (message frame (.location e)))))
          (.addTitleListener (proxy [TitleListener] []
                               (changed [e]
                                 (doto tab-item
                                   (.setText (.title e))))))
          (.addProgressListener (proxy [ProgressAdapter] []))
          (.setJavascriptEnabled @javascript-enabled)
          (.setUrl url))))))

(defmacro defbrowserm [method]
  `(defwidgetm ~method [frame#] :web-browser
     (fn [browser#]
       (. browser# ~method))))

(defbrowserm back)

(defbrowserm forward)

(defbrowserm refresh)

(defbrowserm stop)

(defun open-homepage [frame]
  (web-browser frame @homepage))

(defun web-search [frame string]
  (web-browser frame (str @search-query string)))

(defun browser-menu [frame]
  (doto (create-menu (.shell frame) "&Browser")
    (create-item "&Homepage" open-homepage frame @open-homepage-key)
    (create-item)
    (create-item "&Search" web-search frame @web-search-key)))
