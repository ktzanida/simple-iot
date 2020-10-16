(ns user
  (:require [clojure.tools.namespace.repl :refer :all]))

(disable-unload!)

(defn dev []
  (require 'dev)
  (in-ns 'dev))


(defn fix
  []
  (let [res (refresh-all)]
    (when (instance? Exception res)
      (throw res))))
