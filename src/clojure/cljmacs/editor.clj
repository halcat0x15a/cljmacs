(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]
           [cljmacs Widget Menu]))

(defstyle editor-style SWT/MULTI SWT/BORDER)

(defshortcut new-file-key \N ctrl)

(defshortcut open-key \O ctrl)

(defshortcut save-key \S ctrl)

(defshortcut save-as-key \S ctrl shift)

(defshortcut cut-key \X ctrl)

(defshortcut copy-key \C ctrl)

(defshortcut paste-key \V ctrl)

(defshortcut select-all-key \A ctrl)

(def saved-key "saved")

(def path-key "path")

(defwidget editor [frame path]
  (proxy [Widget] [(.tab_folder frame)]
    (create_control [tab-folder tab-item]
      (let [name (if (nil? path)
                   "Undefined"
                   path)
            string (if (nil? path)
                     ""
                     (slurp path))
            text (doto (StyledText. tab-folder @editor-style)
                   (.setText string)
                   (.addVerifyListener (proxy [VerifyListener] []
                                         (verifyText [e]
                                           (.. e widget (setData saved-key false)))))
                   (.setData saved-key true)
                   (.setData path-key path))]
        (doto tab-item
          (.setText name))
        text))))

(defn new-file [frame] (editor frame nil))

(defn open [frame]
  (let [dialog (FileDialog. (.shell frame) SWT/OPEN)]
    (if-let [path (.open dialog)]
      (editor frame path))))

(defn- save-text [text path]
  (when-not (.getData text saved-key)
    (spit path (.getText text))
    (.setData text saved-key true)))

(defn save-as [frame]
  (let [dialog (FileDialog. (.shell frame) SWT/SAVE)]
    (if-let [path (.open dialog)]
      (let [file (file path)
            widget (.getData (.tab_folder frame) "widget") ;error
            text (.control widget)
            save (fn []
                   (save-text editor path)
                   (.setData text path-key path)
                   (.. widget tab_item (setText path)))]
        (if (.exists file)
          (if (.isFile file)
            (save)
            (message (str path " is not file.")))
          (save))))))

(defn save [frame]
  (let [text (.getData (.tab_folder frame) "widget")]
    (if-let [path (.getData text path-key)]
      (save-text text path)
      (save-as frame))))

(defmacro defaction [name action]
  `(defn ~name [frame#]
     (.. frame# tab_folder (getData "widget") control ~action)))

(defaction cut cut)

(defaction copy copy)

(defaction paste paste)

(defaction select-all selectAll)

(defmenu file-menu [frame]
  (doto (Menu. frame "&File" 0)
    (.create_item "&New File" #(new-file frame) @new-file-key)
    (.create_item "&Open" #(open frame) @open-key)
    (.create-separator)
    (.create_item "&Save" #(save frame) @save-key)
    (.create_item "Save &As" #(save-as frame) @save-as-key)))

(defmenu edit-menu [frame]
  (doto (Menu. frame "&Edit" 1)
    (.create_item "&Cut" #(cut frame) @cut-key)
    (.create_item "&Copy" #(copy frame) @copy-key)
    (.create_item "&Paste" #(paste frame) @paste-key)
    (.create-separator)
    (.create_item "&Select All" #(select-all frame) @select-all-key)))
