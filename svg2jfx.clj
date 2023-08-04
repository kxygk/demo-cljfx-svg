(ns
    svg2jfx
  "Make a GUI node from an SVG"
  (:require [cljfx.api            :as fx]
            [cljfx.composite      :as com])
  (:import #_[javafx.embed.swing JFXPanel]
           com.kitfox.svg.SVGCache
           [afester.javafx.svg SvgLoader]))

(set! *warn-on-reflection* true)


;; ## Converting SVGs to JavaFX objects
;; Takes a string and turns it into an input stream
(defn string->stream
  ([^java.lang.String s] (string->stream s "UTF-8"))
  ([^java.lang.String s
    ^java.lang.String encoding]
   (-> s
       (.getBytes encoding)
       (java.io.ByteArrayInputStream.))))

;; 
(defn svg2batik-fn
  "Use the FranzXaver library to turn an SVG
  into a JavaFX compatible Group Node
  ..  (which shows up as a picture)
  This is using Batik under the hood somehow"
  [^java.lang.String svg-xml-string
   scale-x
   scale-y]
  (println
    "Yo"
    " scale x: "
    scale-x
    " scale y: "
    scale-y)
  (fn
    []
    (let [^javafx.scene.Group
          img (.loadSvg
                (SvgLoader.)
                ^java.io.ByteArrayInputStream
                (string->stream
                  svg-xml-string))]
      (.setScaleX
        img
        scale-x)
      (.setScaleY
        img
        scale-y)
      img)))

(defn
  batik-load
  [svg-string]
  (.loadSvg
    (SvgLoader.)
    ^java.io.ByteArrayInputStream
    (string->stream
      svg-string)))

(defn
  batik-scale
  [^javafx.scene.Group batik-group
   scale-x
   scale-y]
  (.setScaleX
    batik-group
    scale-x)
  (.setScaleY
    batik-group
    scale-y)
  batik-group)  

(defn
  xml
  "Converts an SVG string to a JavaFX Group-like object
  `:svg` for the svg string
  `:width` for an optional width parameter
  NOTE: The library seems to work weird with widths
  if you specify the display width with `:width`
  then the `<svg>` tag shouldn't specify its own height/width"
  [{:keys [fx/context
           svg
           scale-x
           scale-y]}]
  {:fx/type fx/ext-instance-factory
   :create  (svg2batik-fn
              svg
              scale-x
              scale-y)
   #_(girod2jfx-fn
       svg
       width)})
#_#_params (LoaderParameters.) ;; seemingly unused


(defn-
  salamander-buffered-image
  "How to turn an SVG XML string to an image
   Uses `svgSalamander`"
  [svg-xml-str
   width
   height]
  (let [universe       (com.kitfox.svg.SVGCache/getSVGUniverse)
        uri            (.loadSVG
                         universe
                         (java.io.StringReader.
                           svg-xml-str)
                         "unused-placeholder")
        diagram        (.getDiagram
                         universe
                         uri)
        buffered-image (java.awt.image.BufferedImage.
                         width
                         height
                         java.awt.image.BufferedImage/TYPE_4BYTE_ABGR)
        graphics-2d    (.createGraphics
                         buffered-image)]
    (.setRenderingHint
      graphics-2d
      java.awt.RenderingHints/KEY_ANTIALIASING
      java.awt.RenderingHints/VALUE_ANTIALIAS_ON)
    (.render
      diagram
      graphics-2d)
    (.removeDocument
      universe
      uri)
    buffered-image))

#_
(javax.imageio.ImageIO/write
  (salamander-buffered-image
    (slurp
      "small-map-notext.svg")
    100
    100)
  "jpg"
  (java.io.File.
    "buff.jpg"))

(defn-
  salamander2jfx-image
  "How to turn an SVG XML string to an image
   Uses `svgSalamander`"
  [svg-xml-str
   width]
  (->
    svg-xml-str
    (salamander-buffered-image
      width
      width) ;;height
    ((fn
       [buff-img]
       (println
         "Writing image to disk")
       (javax.imageio.ImageIO/write
         ^java.awt.image.BufferedImage buff-img
         "jpg"
         (java.io.File.
           "buff.jpg"))
       buff-img))
    (javafx.embed.swing.SwingFXUtils/toFXImage
      nil)))



(defn
  xml2img
  [{:keys [fx/context
           svg
           width]}]
  {:fx/type :scroll-pane
   ;;     :fit-to-width true
   ;;     :fit-to-height true
   :content {:fx/type :image-view
             ;;             :fit-height width
             :image   (salamander2jfx-image
                        svg
                        width)
             #_       (girod2jfx-image
                        svg
                        width)}})

#_
{:fx/type  :stack-pane
 ;;   :preserve-ratio true
 :children []
 :style    {:-fx-background-repeat   "stretch" 
            :-fx-background-position "center center";
            :-fx-background-image    (let [#_#_params (LoaderParameters.)
                                           image      (->
                                                        ^String svg
                                                        (SVGLoader/load))]
                                       (.snapshot
                                         image
                                         "jpg"
                                         (java.io.File.
                                           "test.txt"))
                                       (spit "test.svg" svg)
                                       (.toImage
                                         image))}}

#_
(set! (.
        params
        applyViewportPosition)
      false)
#_#_    (set! (.
                params
                width)
              100)
(set! (.
        params
        centerImage)
      true)

#_
(def props
  (merge
    fx.shape/props
    (composite/props SVGPath
                     :content [:setter lifecycle/scalar]
                     :fill-rule [:setter lifecycle/scalar :coerce (coerce/enum FillRule)
                                 :default :non-zero])))

#_
(def lifecycle
  (lifecycle/annotate
    (composite/describe SVGPath
                        :ctor []
                        :props props)
    :svg-path))
