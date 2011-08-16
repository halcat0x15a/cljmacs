(ns cljmacs.browser
  (:use [cljmacs.core])
  (:import [org.eclipse.jface.action MenuManager]
           [org.eclipse.swt SWT]
           [org.eclipse.swt.custom CTabFolder CTabItem]
           [org.eclipse.swt.browser Browser OpenWindowListener ProgressAdapter LocationAdapter TitleListener]))

(defconfig homepage "http://google.com/" string?)

(defconfig style SWT/NONE integer?)

(defshortcut homepage-key [ctrl alt] \H)

(defn browser
  ([] (browser (shell)))
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

(defn #^MenuManager browsermenu []
  (doto (MenuManager. "&Browser")
    (.add (action "&Homepage\t" @homepage-key browser))))
