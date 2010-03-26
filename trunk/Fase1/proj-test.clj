(ns proj-test
 (:use clojure.test dbread dbwrite dbsearch interface))
 
(def filenm       "db-1x2 - copia.db")
(def testfilenm   "db-1x2 - copia.db")
(def datab       (read-bin-file filenm))

 ;;Loading File
 (deftest loadfile
 (is (thrown? Exception (read-bin-file filenm)))) 
 
 ;;SimpleTest
 (deftest probando123
 	 (println(is (= 3 (+ 1 1)))))	
 
 ;;Loading Data from file into MAP 
 (deftest loaddata
 	 (testing "Testing - Loading Data from file into MAP" 
 	 (is (instance? clojure.lang.PersistentArrayMap datab) "Loading Data into Map FAIL!"))
 	)	
 	
 ;;Loading Data from file 
 (deftest loaddata
 	(is (instance? clojure.lang.PersistentArrayMap datab))) 

 ;;Check Flipper
 (deftest inter-fliplr
 	 (let [ tuple (to-array ["a" "b" "c" "d"])
 	 	answer (to-array ["c" "b" "a" "d"])
	 	left 0
	 	right 2
	 	exleftval (aget tuple left)
 	 ]
 	 (is (= (aget answer 1 ) (aget (fliplr tuple left right exleftval) 0)) "Testing Flipper")
 	 
 	 )
 )

 	
 	

(run-tests)

