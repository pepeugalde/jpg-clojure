(ns server.server
    (:use config.csconfig))

(use 'clojure.contrib.duck-streams)
(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter))

;---------------FUNCTIONS
(defn myagent-action
  [_ socket content]
  (with-open [input  (.getInputStream socket)
              output (PrintWriter. (.getOutputStream socket))]
    (.print output content)))


    
;-------------MAIN SERVER FUNCTION
(defn serve
  []
  (let [content      (slurp filename)
        server       (ServerSocket. *port*)
        myagent      (agent nil)]
        
    (loop []
      (println "Waiting for connection...")      
      (let [socket (.accept server)]
        (send-off myagent myagent-action socket content)
        (println "Connection accepted"))
      (recur))))

;-----------TEST
;(serve)
