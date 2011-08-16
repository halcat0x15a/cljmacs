(ns cljmacs.browser
  (:use [cljmacs.core])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabItem]
           [org.eclipse.swt.browser Browser OpenWindowListener ProgressAdapter LocationAdapter TitleListener]))

(def homepage (ref "http://google.com/" :validator string?))

(def style (ref SWT/NONE :validator integer?))

(defshortcut homepage-key [ctrl alt] \H)

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

(defn #^MenuManager browsermenu [#^Shell shell]
  (doto (MenuManager. "&Browser")
    (.add (action "&Homepage\t" @homepage-key #(browser shell)))))
