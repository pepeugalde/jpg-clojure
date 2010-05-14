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
"Defines the JTable used by the @databaseref"
(def table          (JTable. ))

"Message/Status label"
(def label          (JLabel. "Welcome!"))

"Defines the file that will be readed as a @databaseref"
(def databaseref       (ref(read-bin-file cfilename)))

"Defines the matrix in which the @databaseref will de displayed"
(def datamatrix     (agent 
                      (get-record2d-values 
                        (records-to-array 
                          (filter-non-deleted 
                            (get-records @databaseref))
                          (get-num-fields @databaseref)))))

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

(defn printarray
"Reads the data base information that will be written in a matrix"
  [lol]
  (loop [i 0]
        (if (< i (alength @datamatrix))
            (do (println (aget @datamatrix i (get-num-fields @databaseref)))
                (if (= true (aget @datamatrix i (get-num-fields @databaseref)))
                    (.setBackground (.getRow table 0) Color/RED)
                    ())
                (recur (inc i)))
            ())))
            
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
        btnCommit   (JButton. "Commit")
        
        searchField (JTextField. )
        searchBox   (JComboBox. (to-array (get-col-names @databaseref)))
        
        imgLabel    (JLabel.)
        
        counter     (ref 0)

        tScrPane    (JScrollPane. table)
          
        model       (proxy [DefaultTableModel]  [@datamatrix (into-array (get-col-names @databaseref))])
        
        tListener   (proxy [TableModelListener] []
            (tableChanged [event]   
                (if (= 0 (.getType event));;table changed
                  (if (>  (.length (.getValueAt model
                                                (.getFirstRow event)
                                                (.getColumn event)))
                          (nth (get-field-lengths @databaseref) (.getColumn event)))
                      (.setValueAt  model ;;fires another event
                                    (trim-value (.getValueAt model 
                                                             (.getFirstRow event)
                                                             (.getColumn event)) 
                                                (nth (get-field-lengths @databaseref) (.getColumn event)))
                                    (.getFirstRow event)
                                    (.getColumn event))
                      (do (update-record-skip-deleted cfilename
                                                      (to-array (get-trimmed-values 
                                                                    (loop  [i (- (get-num-fields @databaseref) 1)  result ()] 
                                                                        (if (> i -1)
                                                                            (recur (dec i) (conj result 
                                                                                                 (.getValueAt model 
                                                                                                              (.getSelectedRow table) i)))
                                                                            result))
                                                                 (get-field-lengths @databaseref)))
                                                     (get-field-lengths @databaseref)
                                                     (.getFirstRow event)
                                                     (get-offset @databaseref))
                          (.setText label "Row updated"))))))
                    
        sorter      (proxy [TableRowSorter] [model])
        
        filter   (proxy [RowFilter]         []
                (include [entry]
                    (.contains (.getValueAt (.getModel entry) 
                                            (.intValue (.getIdentifier entry)) 
                                            (.getSelectedIndex searchBox)) ;;mod to skip
                               (.getText searchField))))
        
        ;;;;;;;;;;;HANDLERS
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
                         ;;add empty row
                         (.addRow model (into-array (vec (repeat (get-num-fields @databaseref) ""))))
                         ;;select the new row
                         (.addRowSelectionInterval table (- (.getRowCount table) 1) (- (.getRowCount table) 1))
                         ;;scroll down to see it
                         (.scrollRectToVisible table (.getCellRect table (- (.getRowCount table) 1) (.getColumnCount table) true))
                         ;;write in @databaseref
                         (write-new-row cfilename
                                  (vec (repeat (get-num-fields @databaseref) "")) (get-field-lengths @databaseref))
                         (.revalidate table)
                         (.setText label "Row added")))

        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (if (= -1 (.getSelectedRow table))
                             (.setText label "No row selected")
                             (do (delete-record-skip-deleted cfilename 
                                                              (.getSelectedRow table) 
                                                              (get-offset @databaseref) 
                                                              (apply + (get-field-lengths @databaseref)))
                                  (.removeRow model (.getSelectedRow table))
                                  (.setText label "Row deleted") ))))

        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (.setRowFilter (.getRowSorter table) filter)
                         (.setText label "Filtered rows")))
                         
        hdlRefresh  (proxy [ActionListener][]
                       (actionPerformed [event]
                         ;do server thingies
                         (.setText label "Refresh!")))
                         
        hdlCommit   (proxy [ActionListener][]
                       (actionPerformed [event]
                         ;do server thingies
                         (.setText label "Commit!")))

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
    (.setModel table model)
    (.setSelectionMode table 0)
    (.setRowSorter table sorter)
    
    ;(.setPreferredSize table (Dimension. tableSX tableSY))
    ;(.setPreferredScrollableViewportSize table (Dimension. tableSX tableSY))
    ;(.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSX tableSY))
    (.setRowSorter table sorter)
    (.addTableModelListener model tListener)
    
    
    ;;;;;;;BUTTON
    (.setPreferredSize btnShowall   (Dimension. btnSX btnSY))
    (.setPreferredSize btnAdd       (Dimension. btnSX btnSY))
    (.setPreferredSize btnDelete    (Dimension. btnSX btnSY))
    (.setPreferredSize btnFind      (Dimension. btnSX btnSY))
    (.setPreferredSize btnRefresh   (Dimension. btnSX btnSY))
    (.setPreferredSize btnCommit    (Dimension. btnSX btnSY))

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
    (.add cbPanel btnCommit)
    
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
    (.addActionListener btnCommit hdlCommit)

    ;(.addTableModelListener model tListener)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame windowSX windowSY)
  )
)

;-------------------CLIENT FUNCTIONS
(defn split-lines
  "Splits s on \\n or \\r\\n."
  [#^String s]
  (seq (.split #"\r?\n" s)))

(defn update-inform
  "Informs that @databaseref has been updated"
  []
  (JOptionPane/showMessageDialog
    nil "Your @databaseref has been updated." "OK"
    JOptionPane/INFORMATION_MESSAGE))
    
(defn react
  "Performs an action accoding to message performative."
  [output [receiver perf content]]
  ;;if message is for me
  (if (= receiver randomID)
      ;;Act according to performative
      (cond (= perf (get performatives :hi))
              (let [sfilecontent (slurp cfilename)]
                 (if (= sfilecontent content)
                     (.setText label "misma version")
                     (.setText label "otra version"))
              )
            (= perf (get performatives :outdated)) 
              (do (update-inform)
                  (println "content: " content)
                  ;;copy new file
                  (rewrite-file cfilename (get-offset @databaseref) content)
                  (dosync (alter databaseref read-bin-file cfilename))
                  (.setText label "@databaseref is outdated!")
                  
                  ;()
              
                  (.setText label "@databaseref updated."))
            (= perf (get performatives :ok))
              (interface "Client")
            (= perf (get performatives :delete)) 
              ((.setText label "Deleting...")
              ())
            (= perf (get performatives :add)) 
              ((.setText label "Adding new row...")
              (write-empty-row cfilename (get-field-lengths @databaseref))
              (.setText label "Row added."))
            (= perf (get performatives :refresh)) 
              ((.setText label "Refreshing...")
              ()
              (.setText label "Refreshing done."))
            (= perf (get performatives :commit)) 
              ((.setText label "Committing...")
              ()
              (.setText label "Commit done."))
            true (.setText label (str "Performative is \"" perf "\"??? I DUNNO WTF TO DO...")))
     ;;else
     (println "None of my business."))
  )

(defn connect
  "Establishes the connection between the client and server"
  []
  (let [socket (Socket. *host* *port*)]
    (with-open [input  (BufferedReader. (InputStreamReader. (.getInputStream socket)))
                output (PrintWriter. (.getOutputStream socket))]

      ;;Attempt connection
      (say output randomID (get performatives :hi) (str (get-records @databaseref)))
      
      ;;wait for response
      (react output (hear input))
      
      
      ;(say output randomID "add" "trolololo lololo lololo")
     )))
             
;----------------------TEST
;(println randomID)
(connect)
;(write-empty-row cfilename (get-field-lengths @databaseref))