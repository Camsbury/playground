(ns ml.core
  (:require [base]
            [libpython-clj.python :refer [py. py.. py.-] :as py]
            [libpython-clj.require :refer [require-python]]))

(require-python '[numpy  :as np]
                '[pandas :as pd]
                '[kaggle.api :as kg])

(defn download_kaggle_dataset [name]
  (let [api (kg/KaggleApi)]
    (py. api authenticate)
    (py. api dataset_download_files name
         :path (str "./resources/" name) :unzip true)))



(comment
  (let [name "ruchi798/tv-shows-on-netflix-prime-video-hulu-and-disney"
        path (str "./resources/" name "/tv_shows.csv")]
    (download_kaggle_dataset name)
    (def df (pd/read_csv path)))
  (py.- df columns))
