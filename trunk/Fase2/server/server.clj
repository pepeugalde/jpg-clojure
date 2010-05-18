(ns server.server
    "This namespace contains functions for a simple server that 
    receives instructions."
    (:use util.dbread util.dbwrite util.dbget util.csutils
          config.csconfig))

    (use 'clojure.contrib.duck-streams)
    
(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter InputStreamReader BufferedReader))
        

;---------------DEFS
;;Each time a client connects, it sends an id
;;The server stores the id to send updates
(def clientlist  (ref #{}))

"Defines the file that will be readed as a database"
(def sdatabaseref (ref (read-bin-file sfilename)))

"Defines a reference to the content"
(def filecontent (ref (slurp sfilename)))

;---------------FUNCTIONS
(defn myagent-action
"Defines an agent's action"
  [_ socket content]
  (with-open [input  (.getInputStream socket)
              output (PrintWriter. (.getOutputStream socket))]
    (.print output content)))
 
(defn reload-db
  "Reloads the database from a file"
  []
  (dosync (alter sdatabaseref (fn[_] (read-bin-file sfilename))))
)
 
(defn sreact
  "Performs an action accoding to message performative."
  [output [sender perf content]]
  ;;If client is new, add it to list
  (dosync (alter clientlist conj sender))
  ;(println "Clients: " @clientlist)
  
  ;;Act according to performative
  (cond (= perf (get performatives :hi))
         ;;compare versions
         (do ;;reload database
             ;(reload-db)
             (if (= content (str (get-records @sdatabaseref)))
                 ;;client has same version
                 (do (println "Client has same version.")
                     ;;tell client
                     (say output sender (get performatives :ok) ""))
                 ;;client has other version
                 (do (println "Client has different version.")
                     (let [sfilecontent (apply str (drop (get-offset @sdatabaseref) (slurp sfilename)))]
                          ;;tell client
                          (say output sender (get performatives :outdated) sfilecontent)))))
                      
        (= perf (get performatives :refresh)) 
           ;;compare versions
          (do ;;reload database
              (reload-db)
              (if  (= content (str (get-records @sdatabaseref)))
                   ;;client has same version
                   (do (println "Client has same version.")
                       ;;tell client
                       (say output sender (get performatives :ok) ""))
                   ;;client has other version
                   (do (println "Client has different version.")
                       (let [sfilecontent (apply str (drop (get-offset @sdatabaseref) (slurp sfilename)))]
                         ;;tell client
                         (say output sender (get performatives :outdated) sfilecontent))))
              (println "Refreshing done."))
                      
        (= perf (get performatives :update)) 
          (do (println "Updating...")
              (println "TO-DO") ;;update
              ;;reload database
              (reload-db)
              ;;tell client
              (say output sender (get performatives :ok) "")
              (println "Update done."))
              
        (= perf (get performatives :delete)) 
          (do (println "Deleting...")
              (println "TO-DO") ;;update
              ;;reload database
              (reload-db)
              ;;tell client
              (say output sender (get performatives :ok) "")
              (println "Delete done."))
              
        (= perf (get performatives :add)) 
          (do (println "Adding new row...")
              (write-empty-row sfilename (get-field-lengths @sdatabaseref))
              ;;reload database
              (reload-db)
              ;;tell client
              (say output sender (get performatives :ok) "")
              (println "Row added."))
        
        true (println (str "Performative is \"" perf "\"??? I DUNNO WTF TO DO..."))))
    
;-------------MAIN SERVER FUNCTION
(defn serve
"Initializes the server"
  []
  (let [content      (slurp sfilename)
        ssocket       (ServerSocket. *port*)
        myagent      (agent nil)]
        
    (loop []
      (println "\nWaiting for connection...")      
      (let [csocket (.accept ssocket)]
        (println "Connection accepted")
        
        ;---HEAR
        (with-open [input  (BufferedReader. (InputStreamReader. (.getInputStream csocket)))
                    output (PrintWriter. (.getOutputStream csocket))]
        
          (sreact output (hear input)))
          
        (recur)))))

;-----------TEST
;(serve)
;(write-empty-row sfilename (get-field-lengths @sdatabaseref))