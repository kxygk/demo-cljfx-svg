(ns
    state
  "Program and GUI state"
  (:require [clojure.java.io :as io]
            [cljfx.api       :as fx]
            [clojure.core.cache :as cache]))

(def
  *selections
  (atom
    (fx/create-context
      {:window-width      1080.0
       :row-height        360}
      #(cache/lru-cache-factory % :threshold 1000))))

;; (fx/sub-val
;;   *selections
;;   :shoreline-file)

(defn
  row-height
  [context]
  (fx/sub-val
    context
    :row-height))

(defn
  window-width
  [context]
  (fx/sub-val
    context
    :window-width))
