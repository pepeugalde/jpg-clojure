(ns interface
    (:require clojure.contrib.swing-utils)
    (:use dbread dbwrite dbsearch dbget)
)
(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane JTextField JComboBox RowFilter ImageIcon)
        '(javax.swing.table DefaultTableModel TableRowSorter)
        '(javax.swing.event TableModelListener)
        '(java.awt Rectangle)
        '(java.awt.event ActionListener)
        '(java.util Collections)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
)

;;;;;DEFS
(def table          (JTable. ))
(def filename       "db-1x2.db")
(def testfilename   "db-1x2.db")
(def database       (read-bin-file filename))
(def datamatrix     (agent 
                        (get-record2d-values 
                          (records-to-array 
                            (filter-non-deleted 
                              (get-records database)) 
                          (get-num-fields database)))))


(defn find-data
  "Finds specified data" 
  [sstring column list] 
  (filter #(= ((keyword column) %) sstring) list))
  
(defn paintDeleted
  "Paints red deleted records in table"
  [table]
  (loop [i 0]
        (if (< i (alength @datamatrix))
            (do (if (= true (aget @datamatrix i (get-num-fields database)))
                    (.setBackground (.getRow table i) Color/RED)
                    ())
                (recur (inc i)))
            ())
  )
)


        
(defn interface
  "Displays the interface that will be used in the urlybird project"
  [title]
  (let [windowSX     800
        windowSY     600
        labelH       25
        btnSX        150
        btnSY        40
        topY         80
        tableSX      (- windowSX (+ 30 btnSX))
        tableSY      (- windowSY labelH topY)
        
        frame       (JFrame. title)
        hPanel      (JPanel. )
        fPanel      (JPanel. )
        bPanel      (JPanel. )
        abPanel     (JPanel. )
        
        btnShowall  (JButton. "Show all")
        btnAdd      (JButton. "Add new row")
        ;btnUpdate   (JButton. "Update selected row")
        btnDelete   (JButton. "Delete selected row")
        btnFind     (JButton. "Find")
        
        searchField (JTextField. )
        searchBox   (JComboBox. (to-array (get-col-names database)))
        
        label       (JLabel. "Something")
        imgLabel    (JLabel.)
        
        counter     (ref 0)

        tScrPane    (JScrollPane. table)
          
        model       (proxy [DefaultTableModel]  [@datamatrix (into-array (get-col-names database))])
        
        tListener   (proxy [TableModelListener] []
            (tableChanged [event]   
               (if (= 0 (.getType event))
                   (do  ;(println "r: " (.getFirstRow event) " c: " (.getColumn event))
                        ; (.setValueAt  model
                                     ; (trim-value (.getValueAt model 
                                                              ; (.getFirstRow event)
                                                              ; (.getColumn event)) 
                                                 ; (nth (get-field-lengths database) (.getColumn event)))
                                     ; (.getFirstRow event)
                                     ; (.getColumn event))
                        
                                     
                        (update-record-skip-deleted testfilename
                                                   (to-array (get-trimmed-values (loop  [i (- (get-num-fields database) 1)  result ()] 
                                                                                      (if (> i -1)
                                                                                          (recur (dec i) (conj result (.getValueAt model (.getSelectedRow table) i)))
                                                                                          result))
                                                                                (get-field-lengths database)))
                                                   (get-field-lengths database)
                                                   (.getFirstRow event)
                                                   (get-offset database))
                        ; (println "r: " (.getFirstRow event) " c: " (.getColumn event))
                         (.setText label "Row updated"))
                    ())))
                    
        sorter      (proxy [TableRowSorter]     [model])
        colFilter   (proxy [RowFilter]          []
             (include [entry filters]
                (loop [i 0  result true]
                    (if (< i (count filters))
                        (if (and result (not (= "" (nth filter i))))
                            (recur (inc i) (.startsWith (.toString (.getValue entry i)) (nth filters i)))
                            ())
                        (result)))))
        
        
        ;;;;;Handlers
        hdlShowall    (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         
                         (.setText label 
                              (str(.getSelectedColumn table)))))

        hdlAdd        (proxy [ActionListener][]
                       (actionPerformed [event]
                         (.setText label "Adding row...")
                         (.addRow model (into-array (vec (repeat (get-num-fields database) ""))))
                         (write-new-row testfilename
                                  (vec (repeat (get-num-fields database) "")) (get-field-lengths database))
                         (.revalidate table)
                         (.setText label "Row added")))

        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (if (= -1 (.getSelectedRow table))
                             (.setText label "No row selected")
                             (do (delete-record-skip-deleted testfilename 
                                                              (.getSelectedRow table) 
                                                              (get-offset database) 
                                                              (apply + (get-field-lengths database)))
                                  (.removeRow model (.getSelectedRow table))
                                  (.setText label "Row deleted") ))))

        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         ;(print (find-data  (.getText searchField) "location" (get-records database)))
                         ;(.setRowFilter sorter (.regexFilter colFilter (str "^" (.getText searchField)) (to-array [0])))
                         (. sorter setRowFilter (RowFilter/regexFilter ".*foo.*" 0))
                         (.setText label 
                                "Filtered rows")))
                                ;(get (nth (get-records database) (rem @counter (get-num-fields database))) :rate) )))]
  ]
                                
    ;;;;;;;;;;;;; END LET
    
;;;;;;;FRAME
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setLayout frame (new BorderLayout))
    
;;;;;;;PANEL
    (.setLayout         hPanel (new BorderLayout))
    (.setPreferredSize  hPanel (Dimension. windowSX topY))
    
    (.setLayout         fPanel (new FlowLayout))
    (.setPreferredSize  fPanel (Dimension. (+ 20 btnSX) (+ 20 (* 3 btnSY))))
    
    (.setLayout         bPanel (new FlowLayout))
    (.setPreferredSize  bPanel (Dimension. (+ 20 btnSX) (- tableSY (+ 20(* btnSY 3)))))
    
    (.setLayout         abPanel (new FlowLayout))
    (.setPreferredSize  abPanel (Dimension. (+ 20 btnSX) tableSY))
    
    ;(.setBackground     hPanel  (Color/yellow))
    ;(.setBackground     fPanel  (Color/cyan))
    ;(.setBackground     bPanel  (Color/magenta))
    ;(.setBackground     abPanel (Color/black))
    
    
;;;;;;;TABLE
    (.setModel table model)
    ;(.setRowSorter table colFilter)
    ;(.setPreferredSize table (Dimension. tableSX tableSY))
    ;(.setPreferredScrollableViewportSize table (Dimension. tableSX tableSY))
    ;(.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSX tableSY))
    ;(.setAutoCreateRowSorter table true)
    (.setRowSorter table sorter)
    ;(.setModel sorter model)
    (.addTableModelListener model tListener)
    
    
;;;;;;;BUTTON
    (.setPreferredSize btnShowall   (Dimension. btnSX btnSY))
    (.setPreferredSize btnAdd       (Dimension. btnSX btnSY))
    ;(.setPreferredSize btnUpdate    (Dimension. btnSX btnSY))
    (.setPreferredSize btnDelete    (Dimension. btnSX btnSY))
    (.setPreferredSize btnFind      (Dimension. btnSX btnSY))

      ;;enable
    ;(.setEnabled btnAdd false)
    
    
;;;;;;;TEXTFIELD
    (.setPreferredSize searchField  (Dimension. btnSX btnSY))
    
;;;;;;;COMBOBOX
    (.setPreferredSize searchBox    (Dimension. btnSX btnSY))
    
;;;;;;;LABEL
    (.setPreferredSize label        (Dimension. windowSX 25))
    (.setPreferredSize imgLabel (Dimension. windowSX topY))
    (.setIcon imgLabel (ImageIcon. "urly.jpg"))
    
;;;;;;;ADDS
    (.add hPanel imgLabel BorderLayout/WEST)
    
    (.add fPanel BorderLayout/NORTH searchField)
    (.add fPanel BorderLayout/CENTER searchBox)
    (.add fPanel BorderLayout/SOUTH btnFind)
    
    (.add bPanel btnShowall)
    (.add bPanel btnAdd)
    ;(.add bPanel btnUpdate)
    (.add bPanel btnDelete)
    
    (.add abPanel BorderLayout/NORTH fPanel)
    (.add abPanel BorderLayout/SOUTH bPanel)
    
    (.add frame BorderLayout/NORTH hPanel)
    (.add frame BorderLayout/CENTER tScrPane)
    (.add frame BorderLayout/EAST abPanel)
    (.add frame BorderLayout/PAGE_END label)
    
    ;;;ACTION LISTENERS
    (.addActionListener btnShowall hdlShowall)
    (.addActionListener btnAdd hdlAdd)
    ;(.addActionListener btnUpdate hdlUpdate)
    (.addActionListener btnDelete hdlDelete)
    (.addActionListener btnFind hdlFind)

    ;(.addTableModelListener model tListener)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame windowSX windowSY)
    
  )
)

(interface "Fase 1")