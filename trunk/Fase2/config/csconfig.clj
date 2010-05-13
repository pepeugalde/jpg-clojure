(ns config.csconfig)

;;;;;DEFS
;Default port to be used
(def *port*   3003)

;Default host to be used
(def *host*   "localhost")

;Default database file name to be used
(def filename "./db/db-1x2.db")

;Default ID length
(def IDlength 10)

;Default server ID
(def serverID "SERVER")

;Default performative length
(def perflength 3)

;Performative's vector
(def performatives {:update "upd", :delete "del", :add "add", :refresh "rfr", :commit "cmt"})
