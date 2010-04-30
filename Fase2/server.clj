;;; Ejemplo de uso de sockets. 
;;; Script servidor.

(use 'clojure.contrib.duck-streams)

(import '(java.net Socket ServerSocket)
        '(java.io PrintWriter))

(def *port* 3003)

(defn accion-de-agente
  [_ socket contenido]
  (with-open [input  (.getInputStream socket)
              output (PrintWriter. (.getOutputStream socket))]
    (.print output contenido)))

(defn sirve
  []  
  (let [nombre-archivo "db-1x2.db"
        contenido      (slurp nombre-archivo)
        servidor       (ServerSocket. *port*)
        agente         (agent nil)]
    (loop []
      (println "Esprando conexión...")      
      (let [socket (.accept servidor)]  
        (send-off agente accion-de-agente socket contenido))        
      (recur))))

      (sirve)
