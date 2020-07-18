(ns web.core
  (:require
   [web.index :refer [index-html]]
   [web.dummy :refer [dummy-html]]
   [stasis.core :as stasis]
   [ring.adapter.jetty :refer [run-jetty]]
   [systemic.core :as sys :refer [defsys]]))

(def pages
  {"/index.html" index-html
   "/dummy.html" dummy-html})

(comment
  (run-jetty (stasis/serve-pages pages) {:port 3000}))
