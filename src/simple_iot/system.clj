(ns simple-iot.system
  (:require [integrant.core :as ig]
            [clojure.spec.alpha :as s]
            [simple-iot.client]
            [simple-iot.core]
            [simple-iot.topology]))

(defonce system (atom nil))

(defn load-config [filename]
  (when filename
    (s/assert
     ::config
     (-> filename
         slurp
         ig/read-string))))

(s/def ::config (s/keys :req [:client/topics :app/config
                              :kafka/config :streams/topology]))

(s/def ::kafka-config map?)
(s/def :client/topics (s/keys :un-req [::kafka-config ::topics-metadata]))

(s/def :app/config (s/keys :un-req [::kafka-config
                                    ::topology
                                    ::topics-metadata]))

(s/def ::topology (s/keys :un-req [::topics]))

(s/def ::topics (s/map-of keyword? ::topic-config))
(s/def ::topic-config (s/keys :un-req [::topic-name
                                       ::partition-count
                                       ::replication-factor
                                       ::topic-config]))

(s/def ::topic-name string?)
(s/def ::partition-count int?)
(s/def ::replication-factor int?)
(s/def ::topic-config map?)

(s/def ::topics-metadata ::topics)
