(ns ml.core
  (:require [base]
            [libpython-clj.python :refer [py. py.. py.-] :as py]
            [libpython-clj.require :refer [require-python]]))

(require-python '[numpy  :as np]
                '[pandas :as pd]
                '[kaggle.api :as kg])

;; kaggle.api.authenticate()
;; kaggle.api.dataset_download_files('ruchi798/tv-shows-on-netflix-prime-video-hulu-and-disney', path='../resources', unzip=True)

;; (kg/authenticate)
(def api (kg/KaggleApi))
(py. api authenticate)
(py.
 api
 dataset_download_files
 "ruchi798/tv-shows-on-netflix-prime-video-hulu-and-disney"
 :path
 "./resources"
 :unzip
 true)


(comment
  (def test-ary (np/array [[1 2][3 4]]))
  (def a 5))
