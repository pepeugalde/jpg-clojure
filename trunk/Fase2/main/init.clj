(ns main.init
    (:gen-class)
    (:use util.interface))

(defn -main
  ([] 
      (println "CLIENT GOES HERE"))
  ([arg]
      ;(if (= arg "alone")
      ;    (interface "Ronery mode ;__;")
      ;    ()))
      (cond (= arg "alone")  (println "1")
            (= arg "server") (println "2")
            true             (println "ERROR! Valid args: \"\", \"alone\" and \"server\".")))
  ([a b & args]
      (println "ERROR! Valid args: \"\", \"alone\" and \"server\"."))
)