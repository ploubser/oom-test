(ns oom-test.core
  (:require [clojure.java.io :as io]
            [ring.util.io :as ringio]))

(defn piped-instream
  [data mapping-fn]
  (ringio/piped-input-stream
    #(with-open [writer (io/make-writer % {:encoding "UTF-8"})]
      (try
        (let [local-mapping-fn mapping-fn] ; seems redundant, however, when mapping-fn is used directly it's vulnerable to OOM
          (doseq [item data]
            (.write writer (local-mapping-fn item))))
        (catch Throwable e
          (.printStackTrace e))))))

(defn -main
  []

  (println "Starting export...")

  (io/copy (piped-instream (range 100000000)
                  (fn [i]
                    (str "item-" i "\n")))
           (io/file "/tmp/oom-test.txt"))

  (shutdown-agents)

  (println "Done"))

