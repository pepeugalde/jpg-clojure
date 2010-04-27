(ns main.init
    (:gen-class)
    (:use util.interface))

(defn -main
  ([] 
      (println "CLIENT GOES HERE"))
  ([arg]
      (cond (= arg "alone")  (interface "Ronery mode ;__;")
            (= arg "server") (println "SERVER GOES HERE")
            true             (println "ERROR! Valid args: \"\", \"alone\" and \"server\".")))
  ([a b & args]
      (println "ERROR! Valid args: \"\", \"alone\" and \"server\"."))
)