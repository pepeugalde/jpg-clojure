(ns client.clientinterface
    "This namespace contains functions used to display a simple 
    interface and connect it with a server."
    (:require clojure.contrib.swing-utils)
    (:use util.dbread util.dbwrite util.dbget util.csutils
          config.interfaceconfig config.csconfig))
          
(use 'clojure.contrib.duck-streams)

(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane JTextField JComboBox RowFilter ImageIcon JOptionPane)
        '(javax.swing.table DefaultTableModel TableRowSorter)
        '(javax.swing.event TableModelListener)
        '(java.awt Rectangle)
        '(java.awt.event ActionListener)
        '(java.util Collections)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
        '(java.net Socket ServerSocket)
        '(java.io PrintWriter InputStreamReader BufferedReader))

;------------------------------FUNCTION DECLARATIONS
(declare react)
(declare connect)
;------------------------------RANDOM ID STRING
"Defines the valid characters that will be used in random-char function"
(def VALID-CHARS
  (map char (concat (range 48 58) ; 0-9
  (range 66 91) ; A-Z
  (range 97 123)))) ; a-z

"Creates a random char"
(defn random-char []
    (nth VALID-CHARS (rand (count VALID-CHARS))))

"Creates a random string"
(defn random-str [length]
    (apply str (take length (repeatedly random-char))))

"Defines the random ID that will be used as a client identifier"
(def randomID (random-str IDlength))

;------------------------------DEFS
"Defines the file that will be readed as a @databaseref"
(def databaseref (ref (read-bin-file cfilename)))

"Defines the matrix in which the @databaseref will de displayed"
(def datamatrix     (ref
                      (get-record2d-values 
                        (records-to-array 
                          (filter-non-deleted 
                            (get-records @databaseref))
                          (get-num-fields @databaseref)))))
                          
"Defines the JTable used by the @databaseref"
(def table       (JTable. ))

"Defines the table model"
(def modelref    (ref (proxy [DefaultTableModel]  [@datamatrix (into-array (get-col-names @databaseref))])))

"Message/Status label"
(def label       (JLabel. "Welcome! Your database is up to date."))

;------------------------------FUNCTIONS
(defn paintDeleted
  "Paints red deleted records in table"
  []
  (loop [i 0]
        (if (< i (alength @datamatrix))
            (do (if (= true (aget @datamatrix i (get-num-fields @databaseref)))
                    (.setBackground (.getRow table i) Color/RED)
                    ())
                (recur (inc i)))
            ())))

(defn printmatrix
  "Reads the data base information that will be written in a matrix"
  []
  (println "alength: " (alength @datamatrix))
  (println "alength 1: " (alength (aget @datamatrix 0)))
  (loop [i 0]
        (if (< i (alength @datamatrix))
            (do (println "matrix " (aget @datamatrix i 0))
                (recur (inc i)))
            ())))

(defn reload
  "Resets the table model when database is updated"
  []
  (dosync (alter databaseref (fn[_] (read-bin-file cfilename)))
          (alter datamatrix  (fn[_] (get-record2d-values 
                                      (records-to-array 
                                        (filter-non-deleted 
                                          (get-records @databaseref))
                                        (get-num-fields @databaseref)))))
          (alter modelref    (fn[_] (proxy [DefaultTableModel]  [@datamatrix (into-array (get-col-names @databaseref))])))
          (.setModel table @modelref))
  (printmatrix)
)
;----------------------------------INTERFACE
(defn interface
  "Displays the interface that will be used in the urlybird project"
  [title]
  (let [frame       (JFrame. title)
        hPanel      (JPanel. ) ;Header
        fPanel      (JPanel. ) ;Find
        bPanel      (JPanel. ) ;Buttons
        cbPanel     (JPanel. ) ;ClientButtons
        abPanel     (JPanel. ) ;All buttons
        
        btnShowall  (JButton. "Show all")
        btnAdd      (JButton. "Add new row")
        btnDelete   (JButton. "Delete selected row")
        btnFind     (JButton. "Find")
        btnRefresh  (JButton. "Refresh")
        
        searchField (JTextField. )
        searchBox   (JComboBox. (to-array (get-col-names @databaseref)))
        
        imgLabel    (JLabel.)
        
        counter     (ref 0)

        tScrPane    (JScrollPane. table)
        
        tListener   (proxy [TableModelListener] []
            (tableChanged [event]   
                (if (= 0 (.getType event));;table changed
                      ;;if length of value is larger than permitted
                  (if (>  (.length (.getValueAt @modelref
                                                (.getFirstRow event)
                                                (.getColumn event)))
                          (nth (get-field-lengths @databaseref) (.getColumn event)))
                      ;;trims value
                      (.setValueAt  @modelref ;;fires another event
                                    (trim-value (.getValueAt @modelref 
                                                             (.getFirstRow event)
                                                             (.getColumn event)) 
                                                (nth (get-field-lengths @databaseref) (.getColumn event)))
                                    (.getFirstRow event)
                                    (.getColumn event))
                      ;attempts server update
                      (connect (get performatives :update) (apply str ("¬")))))))
                    
        sorter      (proxy [TableRowSorter] [@modelref])
        
        filter   (proxy [RowFilter]         []
                (include [entry]
                    (.contains (.getValueAt (.getModel entry) 
                                            (.intValue (.getIdentifier entry)) 
                                            (.getSelectedIndex searchBox)) ;;mod to skip
                               (.getText searchField))))
        
        ;---------HANDLERS
        hdlShowall    (proxy [ActionListener][]
                       (actionPerformed [event]
                         ;;erase filter
                         (.setRowFilter sorter (RowFilter/regexFilter "" (int-array 0)))
                         (.setText label "All rows Displayed")))

        hdlAdd        (proxy [ActionListener][]
                       (actionPerformed [event]
                         (.setText label "Adding row...")
                         ;;erase filter
                         (.setRowFilter sorter (RowFilter/regexFilter "" (int-array 0)))
                         ;;write in database empty row
                         (write-empty-row cfilename (get-field-lengths @databaseref))
                         ;;reload db and table model
                         (reload)
                         ;;scroll down to see it
                         (.scrollRectToVisible table (.getCellRect table (- (.getRowCount table) 1) (.getColumnCount table) true))
                         ;;select the new row
                         (.addRowSelectionInterval table (- (.getRowCount table) 1) (- (.getRowCount table) 1))
                         (.revalidate table)
                         (.setText label "Row added")
                         ;;tell server
                         (connect (get performatives :add) " ")))

        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (if (= -1 (.getSelectedRow table))
                             (.setText label "No row selected")
                             (do (delete-record-skip-deleted cfilename 
                                                              (.getSelectedRow table) 
                                                              (get-offset @databaseref) 
                                                              (apply + (get-field-lengths @databaseref)))
                                  
                                  (.setText label "Row deleted") ))))

        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (.setRowFilter (.getRowSorter table) filter)
                         (.setText label "Filtered rows")))
                         
        hdlRefresh  (proxy [ActionListener][]
                       (actionPerformed [event]
                         ;;ask server if version is updated
                         (connect (get performatives :refresh) (str (get-records @databaseref)))
                         (reload)))
                         

  ];;;;;END LET
    
    ;;;;;;;FRAME
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setLayout frame (new BorderLayout))
    
    ;;;;;;;PANEL
    (.setLayout         hPanel (new BorderLayout))
    (.setPreferredSize  hPanel (Dimension. windowSX topY))
    
    (.setLayout         fPanel (new FlowLayout))
    (.setPreferredSize  fPanel (Dimension. (+ 20 btnSX) (+ 20 (* 3 btnSY))))
    
    (.setLayout         bPanel (new FlowLayout))
    (.setPreferredSize  bPanel (Dimension. (+ 20 btnSX) (+ 30(* btnSY 3))))
    
    (.setLayout         cbPanel (new FlowLayout))
    (.setPreferredSize  cbPanel (Dimension. (+ 20 btnSX) (- tableSY (+ 50(* btnSY 6)))))
    
    (.setLayout         abPanel (new FlowLayout))
    (.setPreferredSize  abPanel (Dimension. (+ 20 btnSX) tableSY))
    
    ;(.setBackground     hPanel  (Color/yellow))
    ;(.setBackground     fPanel  (Color/cyan))
    ;(.setBackground     bPanel  (Color/magenta))
    ;(.setBackground     abPanel (Color/black))
    ;(.setBackground     cbPanel (Color/green))
    
    
    ;;;;;;;TABLE
    (.setModel table @modelref)
    (.setSelectionMode table 0)
    (.setRowSorter table sorter)
    
    ;(.setPreferredSize table (Dimension. tableSX tableSY))
    ;(.setPreferredScrollableViewportSize table (Dimension. tableSX tableSY))
    ;(.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSX tableSY))
    (.setRowSorter table sorter)
    (.addTableModelListener @modelref tListener)
    
    
    ;;;;;;;BUTTON
    (.setPreferredSize btnShowall   (Dimension. btnSX btnSY))
    (.setPreferredSize btnAdd       (Dimension. btnSX btnSY))
    (.setPreferredSize btnDelete    (Dimension. btnSX btnSY))
    (.setPreferredSize btnFind      (Dimension. btnSX btnSY))
    (.setPreferredSize btnRefresh   (Dimension. btnSX btnSY))

      ;;enable
    ;(.setEnabled btnAdd false)
    
    ;;;;;;;TEXTFIELD
    (.setPreferredSize searchField  (Dimension. btnSX defaultboxh))
    
    ;;;;;;;COMBOBOX
    (.setPreferredSize searchBox    (Dimension. btnSX defaultboxh))
    
    ;;;;;;;LABEL
    (.setPreferredSize label        (Dimension. windowSX defaultboxh))
    (.setPreferredSize imgLabel (Dimension. windowSX topY))
    (.setIcon imgLabel (ImageIcon. "./img/urly.jpg"))
    
    ;;;;;;;ADDS
    (.add hPanel imgLabel BorderLayout/WEST)
    
    (.add fPanel BorderLayout/NORTH searchField)
    (.add fPanel BorderLayout/CENTER searchBox)
    (.add fPanel BorderLayout/SOUTH btnFind)
    
    (.add bPanel btnShowall)
    (.add bPanel btnAdd)
    (.add bPanel btnDelete)
    
    (.add cbPanel btnRefresh)
    
    (.add abPanel BorderLayout/NORTH fPanel)
    (.add abPanel BorderLayout/CENTER bPanel)
    (.add abPanel BorderLayout/SOUTH cbPanel)
    
    (.add frame BorderLayout/NORTH hPanel)
    (.add frame BorderLayout/CENTER tScrPane)
    (.add frame BorderLayout/EAST abPanel)
    (.add frame BorderLayout/PAGE_END label)
    
    ;;;ACTION LISTENERS
    (.addActionListener btnShowall hdlShowall)
    (.addActionListener btnAdd hdlAdd)
    (.addActionListener btnDelete hdlDelete)
    (.addActionListener btnFind hdlFind)
    (.addActionListener btnRefresh hdlRefresh)

    ;(.addTableModelListener model tListener)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame windowSX windowSY)
  )
)

;-----------------------------------------CLIENT FUNCTIONS
(defn split-lines
  "Splits s on \\n or \\r\\n."
  [#^String s]
  (seq (.split #"\r?\n" s)))

(defn update-inform
  "Informs if database has been updated"
  [flag]
  (JOptionPane/showMessageDialog
    nil (if flag "Your database has been updated." "Database is up to date") "OK"
    JOptionPane/INFORMATION_MESSAGE))
    
(defn react
  "Performs an action accoding to message performative."
  [myperf output [receiver perf content]]
  ;;if message is for me
  (if (= receiver randomID)
      ;;Act according to my own performative
      (cond (= myperf (get performatives :hi))
                (cond (= perf (get performatives :outdated)) 
                        (do (interface "Client")
                            ;;copy new file
                            (rewrite-file cfilename (get-offset @databaseref) content)
                            ;;reload database and model
                            (reload)
                            (.setText label "Database updated.")
                            (update-inform true))
                      (= perf (get performatives :ok))
                        (do (interface "Client"))
                      
                      true (.setText label "WRONG PERFORMATIVE RECEIVED! EXPECTED :outdated OR :ok"))

            (= myperf (get performatives :refresh))
                (cond (= perf (get performatives :outdated)) 
                        (do ;;copy new file
                            (println "1")
                            (rewrite-file cfilename (get-offset @databaseref) content)
                            (println "2")
                            (reload)
                            (println "4")
                            (.setText label "Database updated.")
                            (update-inform true))
                      (= perf (get performatives :ok))
                        (do (update-inform false)
                            (.setText label "Database is up to date."))
                      
                      true (.setText label "WRONG PERFORMATIVE RECEIVED! EXPECTED :outdated OR :ok"))

            (= myperf (get performatives :update))
                (cond (= perf (get performatives :ok))
                        (.setText label "Row Updated")
                      (= perf (get performatives :no)) 
                        (.setText label "=(")
                      
                      true (.setText label "WRONG PERFORMATIVE RECEIVED! EXPECTED :ok OR :no"))

            
            (= myperf (get performatives :add))
                (cond (= perf (get performatives :ok))
                        (do (reload)
                            (.setText label "Row Added"))
                      (= perf (get performatives :no)) 
                        (.setText label "=(")
                      
                      true (.setText label "WRONG PERFORMATIVE RECEIVED! EXPECTED :ok OR :no"))

                      
            (= myperf (get performatives :delete))
                (cond (= perf (get performatives :ok))
                        (.setText label "Row Deleted")
                      (= perf (get performatives :no)) 
                        (.setText label "=(")
                      
                      true (.setText label "WRONG PERFORMATIVE RECEIVED! EXPECTED :ok OR :no")))
            
     ;;else
     (println "None of my business. " receiver " should be " randomID))
  )

(defn connect
  "Establishes a connection between the client and server"
  [perf content]
  (let [socket (Socket. *host* *port*)]
    (with-open [input  (BufferedReader. (InputStreamReader. (.getInputStream socket)))
                output (PrintWriter. (.getOutputStream socket))]

      ;;Attempt connection
      (say output randomID perf content)
      
      ;;wait for response
      (react perf output (hear input))
      
     )))
             
;----------------------TEST
;(println randomID)
(connect (get performatives :hi) (str (get-records @databaseref)))
;(write-empty-row cfilename (get-field-lengths @databaseref))