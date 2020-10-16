(ns simple-iot.topology
  (:require [clojure.tools.logging :refer [info]]
            [jackdaw.streams :as js]
            [integrant.core :as ig]))

(defn build-topology
  "Returns a topology builder function."
  [{:keys [temperature-living-room high-temperature-detected] :as topics}]
  (fn [builder]
    ;; Read the temperature-living-room topic into a KStream
    (-> (js/kstream builder temperature-living-room)

        ;; Filter the KStream for temperatures greater than 30
        (js/filter (fn [[_ measurement]]
                     (<= 30 (:value measurement))))
        ;; Remove all but the :amount and :user-id fields from the message.
        ;; Note that the function passed to map takes and returns a tuple of [key value].
        (js/map (fn [[k measurement]]
                  [k (select-keys measurement [:value :sensor-id])]))
        ;; Write our KStream to the high-temperature-detected
        (js/to high-temperature-detected))
    builder))

(defmethod ig/init-key :streams/topology [_ {:keys [topics] :as config}]
  (info "Building topology...")
  (build-topology topics))

(defmethod ig/halt-key! :streams/topology [_ _])
