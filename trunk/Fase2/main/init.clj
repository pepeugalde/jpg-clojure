(ns main.init
    (:gen-class)
    (:use config.csconfig server.server client.clientinterface  util.interface))

(defn -main
  ([] 
     (initclient))
  ([arg]
      (cond (= arg "alone")  (interface "Ronery mode ;__;")
      	      (= arg "server")(serve)
             true             (println "ERROR! Valid args: \"\", \"alone\" and \"server\".")))
  ([a b & args]
      (println "ERROR! Valid args: \"\", \"alone\" and \"server\"."))
)