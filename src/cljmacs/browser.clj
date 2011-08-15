(ns cljmacs.browser
  (:use [cljmacs.core])
  (:import [clojure.lang Ref]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabItem]
           [org.eclipse.swt.browser Browser OpenWindowListener ProgressAdapter LocationAdapter TitleListener]))

(def #^Ref homepage (ref "http://google.com/" :validator string?))

(def #^Ref style (ref SWT/NONE :validator integer?))

(defn browser
  ([#^Shell shell] (browser shell @homepage))
  ([#^Shell shell #^String url]
     (let [tabfolder (tabfolder shell)
           tabitem (CTabItem. tabfolder SWT/CLOSE)
           text (text shell)
           browser (doto (Browser. tabfolder @style)
                     (.addLocationListener
                      (proxy [LocationAdapter] []
                        (changed [e]
                          (doto text
                            (.setText (.location e))))))
                     (.addTitleListener
                      (proxy [TitleListener] []
                        (changed [e]
                          (doto tabitem
                            (.setText (.title e))))))
                     (.addProgressListener
                      (proxy [ProgressAdapter] []))
                     (.addOpenWindowListener
                      (proxy [OpenWindowListener] []
                        (open [e]
                          (browser shell (.getUrl (.widget e))))))
                     (.setUrl url))]
       (.setControl tabitem browser)
       (.setSelection tabfolder tabitem))))
