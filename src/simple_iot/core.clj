(ns simple-iot.core
  (:gen-class)
  (:require [clojure.spec.alpha :as s]
            [clojure.tools.logging :refer [info]]
            [simple-iot.topology]
            [jackdaw.admin :as ja]
            [jackdaw.streams :as js]
            [jackdaw.serdes.edn :as jse]
            [integrant.core :as ig]
            [simple-iot.system :as system])
  (:import [org.apache.kafka.common.serialization Serdes]))

(defn start-app
  "Starts the stream processing application."
  [{:keys [kafka-config topology] :as conf}]
  (let [builder (js/streams-builder)
        topo    (topology builder)
        app     (js/kafka-streams topo kafka-config)]
    (js/start app)
    (info "simple-iot is up")
    app))

(defn stop-app
  "Stops the stream processing application."
  [app]
  (js/close app)
  (info "simple-iot is down"))


(defmethod ig/init-key :app/config [_ config]
  (info "Starting simple iot app...")
  (start-app config))

(defmethod ig/halt-key! :app/config [_ app]
  (info "Starting simple iot app...")
  (stop-app app))
