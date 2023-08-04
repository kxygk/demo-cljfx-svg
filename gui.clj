(ns
    gui
  "Imergination GUI tree"
  (:require [clojure.java.io :as io]
            [cljfx.api       :as fx]
            [cljfx.ext.list-view :as fx.ext.list-view]
            svg2jfx)
  (:gen-class :main true))`

(set!
  *warn-on-reflection*
  true)


(defn
  svg
  "The actual workmap svg (as a JFX component"
  [{:keys [fx/context
           svg-group]}]
  {:fx/type fx/ext-instance-factory
   :create  (fn []
              svg-group)})


(def
  circle-svg
  (-> "circle.svg"
      slurp))

(def
  circle-group
    (svg2jfx/batik-load circle-svg))


(defn
  root
  "The start of the GUI tree
  (using the `cljfx` naming convention)"
  [{:keys [fx/context]}]
  {:fx/type :stage
   :showing true
   :title   "Imergination"
   :scene   {:fx/type          :scene
             :root
             {:fx/type      :scroll-pane
              :fit-to-width true
              :content      {:fx/type  :v-box
                             :children [{:fx/type            :grid-pane
                                         :column-constraints [{:fx/type       :column-constraints
                                                               :percent-width 100/2}
                                                              {:fx/type       :column-constraints
                                                               :percent-width 100/2}]
                                         
                                         :row-constraints    (repeat
                                                               2 ;; number of rows of stuff going on.. hardcoded :(
                                                               {:fx/type    :row-constraints
                                                                :max-height 500})
                                         :children [{:fx/type          svg
                                                     :svg-group        (svg2jfx/batik-load circle-svg)
                                                     :grid-pane/row    1
                                                     :grid-pane/column 0}
                                                   
                                                    {:fx/type          svg
                                                     :svg-group        (svg2jfx/batik-load circle-svg)
                                                     :grid-pane/row    1
                                                     :grid-pane/column 1}
                                                    #_#_#_#_
                                                    {:fx/type          datapreview
                                                     :grid-pane/row    2
                                                     :grid-pane/column 0}
                                                    {:fx/type          datapreview
                                                     :grid-pane/row    2
                                                     :grid-pane/column 1}
                                                    {:fx/type          datapreview
                                                     :grid-pane/row    3
                                                     :grid-pane/column 0}
                                                    {:fx/type          datapreview
                                                     :grid-pane/row    3
                                                     :grid-pane/column 1}]}]}}}})

#_
(core/renderer)
