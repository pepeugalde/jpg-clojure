(ns server.server
    (:use config.csconfig))

    (use 'clojure.contrib.duck-streams)
(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter InputStreamReader BufferedReader))

;---------------FUNCTIONS
(defn myagent-action
  [_ socket content]
  (with-open [input  (.getInputStream socket)
              output (PrintWriter. (.getOutputStream socket))]
    (.print output content)
    ))

(defn say
  [socket content]
  (println "Saying: " content)
  (with-open [output (PrintWriter. (.getOutputStream socket))]
    (.print output content)))
    
(defn whaaat
  [string]
  (cond (= string "update")))
    
;-------------MAIN SERVER FUNCTION
(defn serve
  []
  (let [content      (slurp filename)
        server       (ServerSocket. *port*)
        myagent      (agent nil)]
        
    (loop []
      (println "\nWaiting for connection...")      
      (let [socket (.accept server)
           ]
        (println "Connection accepted")
        ;(println "agent before " @myagent)
        ;(send-off myagent myagent-action socket content)
        ;(println "agent after " @myagent)
        
        ;---HEARING LOOP
        (with-open [input  (BufferedReader. (InputStreamReader. (.getInputStream socket)));(.getInputStream socket)
                    output (PrintWriter. (.getOutputStream socket))];(.getOutputStream socket)]
          (let [recmsg ""]
              (loop []
                (let [c (.read input)]
                  (when (not= c -1)
                    (concat recmsg (char c)) ;;falta meterlo a un string
                    (recur))))
                
          (println "HHH")
      
      
      
             )
        
        
        )
      (recur))))

;-----------TEST
;(serve)
