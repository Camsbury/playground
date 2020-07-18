(ns web.dummy
  (:require [web.style :refer [styles]]
            [hiccup.core :refer [html]]))

(def dummy-html
  (html
   [:html
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Hello, CSS"]
     [:style styles]]
    [:body
     [:h1 "Hello, CSS, number 2!"]]]))
