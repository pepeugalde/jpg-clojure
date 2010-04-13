(ns proj-test
 (:use clojure.test dbread dbwrite dbsearch interface))
 
(def filenm       "db-1x2.db")
(def testfilenm   "db-1x2.db")
(def datab        (read-bin-file filenm))

 ;;Loading File
(deftest loadfile
 (is (thrown? Exception (read-bin-file filenm)))) 
 
 ;;SimpleTest
(deftest probando123
 	 (is (= 3 
          (+ 1 
             1))
       "MATH FAIL"))
 
 ;;Loading Data from file into MAP 
(deftest loaddata
 	 (testing "Testing - Loading Data from file into MAP" 
 	 (is (instance? clojure.lang.PersistentArrayMap 
                  datab) 
       "Loading Data into Map FAIL!")))	
 	
 ;;Loading Data from file 
(deftest load-data
 	(is (instance? clojure.lang.PersistentArrayMap 
                 datab)
      "Database should be a map!"))

 ;;Check Flipper
(deftest inter-fliplr
 	 (let [tuple  (to-array ["a" "b" "c" "d"])
        answer  (to-array ["c" "b" "a" "d"])
        left    0
        right   2
        exleftval (aget tuple left)
       ]
     (is (= (aget answer 1 ) 
            (aget (fliplr tuple left right exleftval) 0))
         "Flipper FAIL!")))

 ;;Check Trims
(deftest trim-val-test
   (is (= (trim-value "roflolmao" 6)
          "roflol")
       "trim-value not trimming!"))
(deftest get-trimmed-val-test
   (is (= (get-trimmed-values ["abcde" "fghij" "klmno"][4 5 3]) 
          ["abcd" "fghij" "klm"])
       "FAILED to get trimmed values!"))

(run-tests)

