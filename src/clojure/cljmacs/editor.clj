(ns cljmacs.editor
  (:use [clojure.java.io :only (reader file)]
        [cljmacs.core])
  (:import [org.eclipse.swt SWT]
           [org.eclipse.swt.custom StyledText]
           [org.eclipse.swt.events VerifyListener]
           [org.eclipse.swt.widgets FileDialog]
           [cljmacs Widget]))

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
  (proxy [Widget] [frame]
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

(defun new-file [frame]
  (editor frame nil)
  (message frame "new file"))

(defun open [frame]
  (let [dialog (FileDialog. (.shell frame) SWT/OPEN)]
    (if-let [path (.open dialog)]
      (editor frame path))))

(defn- save-text [text path]
  (when-not (.getData text saved-key)
    (spit path (.getText text))
    (.setData text saved-key true)))

(defun save-as [frame]
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
            (message frame (str path " is not file.")))
          (save))))))

(defun save [frame]
  (let [text (.getData (.tab_folder frame) "widget")]
    (if-let [path (.getData text path-key)]
      (save-text text path)
      (save-as frame))))

(defmacro defaction [name action]
  `(defun ~name [frame#]
     (.. frame# tab_folder (getData "widget") control ~action)))

(defaction cut cut)

(defaction copy copy)

(defaction paste paste)

(defaction select-all selectAll)

(defun file-menu [frame]
  (let [shell (.shell frame)]
    (create-menu shell (.getMenuBar shell) "&File"
                 (create-item "&New File" new-file frame @new-file-key)
                 (create-item "&Open" open frame @open-key)
                 (create-item)
                 (create-item "&Save" save frame @save-key)
                 (create-item "Save &As" save-as frame @save-as-key))))

(defun edit-menu [frame]
  (let [shell (.shell frame)]
    (create-menu shell (.getMenuBar shell) "&Edit"
                 (create-item "&Cut" cut @cut-key)
                 (create-item "&Copy" copy @copy-key)
                 (create-item "&Paste" paste @paste-key)
                 (create-item)
                 (create-item "&Select All" select-all @select-all-key))))
