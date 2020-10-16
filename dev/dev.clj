(ns dev
  (:require [clojure.tools.namespace.repl :refer [clear refresh-all] :as tools.ns]
            [integrant.repl :as ig]
            [simple-iot.system :as system]))

(defn go
  []
  (integrant.repl/set-prep! (constantly (system/load-config "resources/config.edn")))
  (ig/go)
  (reset! system/system integrant.repl.state/system)
  :ok)

(defn reset []
  (integrant.repl/set-prep! (constantly (system/load-config "resources/config.edn")))
  (ig/reset))

(defn halt [] (ig/halt))

(defn refresh []
  (tools.ns/refresh))
