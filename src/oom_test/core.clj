(ns oom-test.core
  (:require [clojure.java.io :as io]
            [ring.util.io :as ringio]
            [clojure.pprint :as pp]))

(defn piped-instream-test-1
  [data mapping-fn]
  (ringio/piped-input-stream
    #(with-open [writer (io/make-writer % {:encoding "UTF-8"})]
      (try
        (doseq [item data]
          (.write writer (mapping-fn item)))
        (catch Throwable e
          (.printStackTrace e))))))

(defn piped-instream-test-2
  [data mapping-fn]
  (let [data-fn (fn [] data)]
    (ringio/piped-input-stream
      #(with-open [writer (io/make-writer % {:encoding "UTF-8"})]
        (try
          (doseq [item (data-fn)]
            (.write writer (mapping-fn item)))
          (catch Throwable e
            (.printStackTrace e)))))))

(defn piped-instream-test-3
  [data-fn mapping-fn]
  (ringio/piped-input-stream
    #(with-open [writer (io/make-writer % {:encoding "UTF-8"})]
      (try
        (doseq [item (data-fn)]
          (.write writer (mapping-fn item)))
        (catch Throwable e
          (.printStackTrace e))))))

(defn processing-fn
  [i]
  (str "item-" i "\n"))

(defn -main
  [& [args]]

  (println "Starting export...")

  (case args
    "1" (do ;; data as sequence - always ooms
          (io/copy (piped-instream-test-1 (range 100000000)
                                          processing-fn)
                   (io/file "/tmp/oom-test.txt")))

    "2" (do ;; data as sequence, wrapped in piped-instream - always ooms
          (io/copy (piped-instream-test-2 (range 100000000)
                                          processing-fn)
                   (io/file "/tmp/oom-test.txt")))

    "3" (do ;; data as sequence, wrapped before piped-instream - always ooms
          (let [lazy-sec (range 100000000)]
            (io/copy (piped-instream-test-3 (fn [] lazy-sec)
                                            processing-fn)
                     (io/file "/tmp/oom-test.txt"))))

    "4" (do ;; data as function  - doesn't oom
          (io/copy (piped-instream-test-3 (fn [] (range 100000000))
                                          processing-fn)
                   (io/file "/tmp/oom-test.txt")))
    (println "Valid test cases are 1, 2, 3 or 4"))

  (shutdown-agents)

  (println "Done"))

