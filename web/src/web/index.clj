(ns web.index
  (:require [web.style :refer [styles]]
            [hiccup.core :refer [html]]))

(def index-html
  (html
   [:html
    [:head
     [:meta {:charset "UTF-8"}]
     [:title "Hello, CSS"]
     [:style styles]]
    [:body
     [:h1 "Hello, CSS"]
     [:p "CSS let's us style shit. Link: " [:a {:href "dummy.html"} "another page"] " in this bitch"]]]))

