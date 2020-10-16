(ns simple-iot.client
  (:require [clojure.spec.alpha :as s]
            [clojure.set :as set]
            [clojure.tools.logging :refer [info]]
            [jackdaw.client :as jc]
            [jackdaw.admin :as ja]
            [jackdaw.serdes.edn :as jse]
            [integrant.core :as ig])
  (:import [org.apache.kafka.common.serialization Serdes]))

(defn- add-serde [topic]
  (merge {:key-serde   (jse/serde)
          :value-serde (jse/serde)}
         topic))

(defn- format-topics [conf]
  (update conf :topics-metadata #(reduce-kv
                                  (fn [m k v] (assoc m k (add-serde v)))
                                  {}
                                  %)))

(defn- topics->kafka [admin-client topics]
  (let [kafka-topics (ja/list-topics admin-client)
        app-topics   (map #(select-keys % [:topic-name]) topics)
        t->k         (set/difference (set app-topics) (set kafka-topics))]
    (when-not (empty? t->k)
      (filter #(t->k (select-keys % [:topic-name])) topics))))

(defn create-topics
  "Create topics using the AdminClient interface"
  [{:keys [kafka-config topics-metadata] :as conf}]
  (with-open [ac (ja/->AdminClient kafka-config)]
    (when-let [topics (topics->kafka ac (vals topics-metadata))]
      (ja/create-topics! ac topics))))

(defn publish-temperature!
  "Publish a temperature message to `temperature-living-room` topic.
  TODO: we need a spec check for m here."
  [m {:keys [kafka-config topics-metadata]}]
  (let [temperature-living-room (:temperature-living-room topics-metadata)
        id                      (uuid)]
    (with-open [producer (jc/producer kafka-config {:key-serde   (jse/serde)
                                                    :value-serde (jse/serde)})]
      @(jc/produce! producer temperature-living-room id (merge m {:id id})))))


(defmethod ig/init-key :kafka/config [_ config]
  (info "Passing config params...")
  config)
(defmethod ig/halt-key! :kafka/config [_ _])

(defmethod ig/init-key :client/topics [_ config]
  (info "Creating topics...")
  (let [config (format-topics config)]
    (create-topics config)
    (:topics-metadata config)))

(defmethod ig/halt-key! :client/topics [_ _])

(comment
  (publish-temperature! {:value     23.6
                         :sensor-id 1} conf))
