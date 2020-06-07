(ns playground.hamiltonian-path
  (:require [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [clj-async-profiler.core :as prof]
            [criterium.core :as criterium]
            [ubergraph.core :as ug]
            [loom.alg-generic :as gen]
            [loom.graph :as graph]))

;https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4963334/

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Examples

(def example-puzzle
  {::y-len 6
   ::x-len 6
   ::start {::x 2 ::y 1}
   ::obstacles [{::x 0 ::y 2}
                {::x 2 ::y 3}]})

(def example-2-puzzle
  {::y-len 8
   ::x-len 8
   ::start {::x 2 ::y 1}
   ::obstacles [{::x 0 ::y 2}
                {::x 2 ::y 3}]})

(def ages-hard-puzzle
  {::y-len 9
   ::x-len 13
   ::start {::x 12 ::y 4}
   ::obstacles
   [{::x 8 ::y 3}
    {::x 6 ::y 6}
    {::x 6 ::y 3}
    {::x 6 ::y 1}
    {::x 3 ::y 3}]})

(def ages-hard-modified
  {::y-len 8
   ::x-len 10
   ::start {::x 0 ::y 0}
   ::obstacles
   [{::x 7 ::y 3}
    {::x 5 ::y 6}
    {::x 5 ::y 3}
    {::x 5 ::y 1}
    {::x 2 ::y 3}]})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Spec

(s/def ::puzzle
  (s/keys
   :req [::y-len
         ::x-len
         ::start
         ::obstacles]))

(s/def ::y-len
  int?)

(s/def ::x-len
  int?)

(s/def ::start
  (s/keys
   :req [::x ::y]))

(s/def ::obstacles
  (s/coll-of
   (s/keys
    :req [::x ::y])))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Rewrites

(defn my-connected-components
  "Returns the connected components of graph g as a vector of vectors. If g
  is directed, returns the weakly-connected components."
  [g]
  (let [nb (graph/successors g)]
    (first
     (reduce
      (fn [[cc predmap] n]
        (if (contains? predmap n)
          [cc predmap]
          (let [[c pm] (reduce
                        (fn [[c _] [n pm _]]
                          [(conj c n) pm])
                        [[] nil]
                        (gen/bf-traverse nb n :f vector :seen predmap))]
            [(conj cc c) pm])))
      [[] {}]
      (graph/nodes g)))))

(defn my-connected?
  "Returns true if g is connected"
  [g]
  (== (count (first (my-connected-components g))) (count (graph/nodes g))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Initializers

(defn init-graph [{::keys [x-len y-len start obstacles]}]
  (let [obstacle-set (set obstacles)
        nodes (for [x (range x-len)
                    y (range y-len)
                    :let [pair {::x x ::y y}]
                    :when (and
                           (not (obstacle-set pair)))]
                pair)
        edges (set
               (for [a nodes
                     b nodes
                     :let [{xa ::x ya ::y} a
                           {xb ::x yb ::y} b]
                     :when (or
                            (and
                             (= xa (inc xb))
                             (= ya yb))
                            (and
                             (= xa xb)
                             (= ya (inc yb))))]
                 [a b {:color :blue}]))
        with-nodes #(apply ug/add-nodes % nodes)
        with-edges #(apply ug/add-edges % edges)]
    (-> (ug/graph)
        with-nodes
        with-edges
        (ug/set-attrs start {:start true}))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Validators

(defn surplus-node? [graph node]
  (let [saturated-count
        (if (ug/attr graph node :start) 1 2)]
    (->> (ug/out-edges graph node)
         (map #(ug/attr graph % :color))
         (filter #(= % :blue))
         count
         (#(> % saturated-count)))))

(defn surplus-edge? [graph edge]
  (let [src  (ug/src edge)
        dest (ug/dest edge)]
    (and
     (surplus-node? graph src)
     (surplus-node? graph dest))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Getters

(defn get-colored-edges [graph node color]
  (->>
   (ug/out-edges graph node)
   (filter #(= (ug/attr graph % :color) color))))

(defn get-next [edge node]
  (let [src   (ug/src edge)
        dest  (ug/dest edge)
        nodes {src dest
               dest src}]
    (get nodes node)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Impl

(defn delete-surplus-edges-per-edge [graph edge]
  (if (surplus-edge? graph edge)
    (ug/set-attrs graph edge {:color :red})
    graph))

(defn delete-surplus-edges-per-node [graph node]
  (let [edges (get-colored-edges graph node :blue)]
    (if (surplus-node? graph node)
      (reduce delete-surplus-edges-per-edge graph edges)
      graph)))

(defn delete-surplus-edges [graph]
  (let [nodes (ug/nodes graph)]
    (reduce delete-surplus-edges-per-node graph nodes)))

(defn get-remaining-set [graph]
  (->> (ug/nodes graph)
       (filter #(surplus-node? graph %))
       (set)))

(defn reflect-color [color]
  (if (= color :red) :blue :red))

(defn reflect-edge [graph edge]
  (if (= (ug/attr graph edge :color) :blue)
    (ug/set-attrs graph edge {:color :red})
    (ug/set-attrs graph edge {:color :blue})))

(defn reflect-path [graph edges]
  (reduce reflect-edge graph edges))

(defn alt-finished? [{::keys [path current termination-node? termination-edge?]}]
  (let [last-edge (peek path)]
    (and
     (termination-node? current)
     (termination-edge? last-edge))))

(defn next-alt [graph {::keys [path current color] :as context}]
  (let [edges (get-colored-edges graph current color)
        contextualize
        (fn [edge]
          (merge
           context
           {::path (conj path edge)
            ::current (get-next edge current)
            ::color (reflect-color color)}))]
    (map contextualize edges)))

(defn set-init-alt-contexts
  [{::keys [graph starter destructor?]}]
  (let [edges (if destructor?
                (get-colored-edges graph starter :blue)
                (ug/out-edges graph starter))
        contextualize (fn [edge]
            (let [next (get-next edge starter)
                  alt-color (reflect-color (ug/attr graph edge :color))
                  last-color (if destructor?
                               :blue
                               alt-color)
                  termination-edge? (fn [edge]
                                      (= (ug/attr graph edge :color)
                                         last-color))]
              {::path [edge]
               ::current next
               ::termination-edge? termination-edge?
               ::color alt-color}))]
    (map contextualize edges)))

(defn get-alternating
  [{::keys [graph] :as context}]
  (loop [path-opts
         (reduce
          conj clojure.lang.PersistentQueue/EMPTY
          (set-init-alt-contexts context))]
    (let [alt-context (peek path-opts)
          new-opts (pop path-opts)]
      (if (alt-finished? (merge context alt-context))
        (::path alt-context)
        (recur (reduce conj new-opts (next-alt graph alt-context)))))))

(defn get-destructor [{::keys [graph remaining-set]}]
  (let [starter          (first remaining-set)]
    (get-alternating
     {::graph             graph
      ::termination-node? remaining-set
      ::starter           starter
      ::destructor?       true})))

(defn reflect-destructors [graph]
  (loop [remaining-set (get-remaining-set graph) graph graph]
    (let [destructor (get-destructor {::graph graph
                                      ::remaining-set remaining-set})
          new-graph (reflect-path graph destructor)
          new-remaining-set (->> remaining-set
                                 (filter #(surplus-node? new-graph %))
                                 set)]
      (if (seq new-remaining-set)
        (recur new-remaining-set new-graph)
        new-graph))))

(defn permute-connectors [graph] ;TODO
  graph)

(defn find-hamiltonian-path [puzzle]
  (->> puzzle
       init-graph
       delete-surplus-edges
       reflect-destructors
       permute-connectors))

;; (let [g (init-graph example-puzzle)]
;;     (reflect-destructors (delete-surplus-edges g)))

(comment
  (let [viz
        #(ug/viz-graph % {:save {:filename "visual.png" :format :png}})]
    (->> example-puzzle
         init-graph
         delete-surplus-edges
         viz)))
