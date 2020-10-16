(ns simple-iot.main
  (:require [clojure.tools.logging :refer [info]]
            [integrant.core :as ig]
            [simple-iot.system :as system]))

(defn -main [& [config-filename]]
  (info "Starting simple iot...")
  (let [system (ig/init (system/load-config config-filename))]
    (.addShutdownHook
     (Runtime/getRuntime)
     (Thread. #(do
                 (info "Stopping simple-iot gracefully...")
                 (ig/halt! system))))
    (Thread/sleep Long/MAX_VALUE)))
