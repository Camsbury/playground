(ns user
  (:require [base]
            [wing.repl]))

(comment
  (wing.repl/sync-libs!) ; refresh your deps - doesn't remove from classpath
  (wing.repl/add-lib!) ; dynamically add lib
  )

