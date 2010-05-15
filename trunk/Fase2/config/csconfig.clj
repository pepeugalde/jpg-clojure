(ns config.csconfig)

;;;;;DEFS
;Default port to be used
(def *port*   3003)

;Default host to be used
(def *host*   "localhost")

;Default database file names to be used
(def sfilename "./db/db-1x2.db")
(def cfilename "./db/db-1x2 - copia.db")

;Default ID length
(def IDlength 10)

;Default server ID
(def serverID "SERVER----")

;Default performative length
(def perflength 3)

;Performative's vector
(def performatives {:ok "ok!", :no "no!", :outdated "out", :hi "hi!", :update "upd", :delete "del", :add "add", :refresh "rfr"})
