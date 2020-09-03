(ns python-clj.core
  (:require [base]
            [libpython-clj.python :refer [py. py.-] :as py]
            [libpython-clj.require :refer [require-python]]))

(require-python
 '[builtins          :as pyb]
 '[operator          :as op]
 '[numpy             :as np]
 '[pandas            :as pd]
 '[scipy.stats       :as stats]
 '[matplotlib.pyplot :as plt])
