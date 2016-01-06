(defproject oom-test "0.1.0-SNAPSHOT"
  :jvm-opts ["-Xmx128m" "-XX:MaxPermSize=128m"]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.4.0"]]
  :main oom-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
