(ns util.interface
    "This namespace contains functions which can display a simple interface for toying with a database."
    (:require clojure.contrib.swing-utils)
    (:use util.dbread util.dbwrite util.dbsearch util.dbget 
          config.interfaceconfig))
    
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
(def filename       "./db/db-1x2.db")
(def database       (read-bin-file filename))
(def datamatrix     (agent 
                        (get-record2d-values 
                          (records-to-array 
                            (filter-non-deleted 
                              (get-records database))
                            (get-num-fields database)))))
  
(defn paintDeleted
  "Paints red deleted records in table"
  []
  (loop [i 0]
        (if (< i (alength @datamatrix))
            (do (if (= true (aget @datamatrix i (get-num-fields database)))
                    (.setBackground (.getRow table i) Color/RED)
                    ())
                (recur (inc i)))
            ())))

(defn printarray
  [lol]
  (loop [i 0]
        (if (< i (alength @datamatrix))
            (do (println (aget @datamatrix i (get-num-fields database)))
                (if (= true (aget @datamatrix i (get-num-fields database)))
                    (.setBackground (.getRow table 0) Color/RED)
                    ())
                (recur (inc i)))
            ())))
;------------------------------------------------------------------
        
(defn interface
  "Displays the interface that will be used in the urlybird project"
  [title]
  (let [frame       (JFrame. title)
  
        hPanel      (JPanel. ) ;Header
        fPanel      (JPanel. ) ;Find
        bPanel      (JPanel. ) ;Buttons
        abPanel     (JPanel. ) ;All buttons
        
        btnShowall  (JButton. "Show all")
        btnAdd      (JButton. "Add new row")
        btnDelete   (JButton. "Delete selected row")
        btnFind     (JButton. "Find")
        
        searchField (JTextField. )
        searchBox   (JComboBox. (to-array (get-col-names database)))
        
        label       (JLabel. "Welcome!")
        imgLabel    (JLabel.)
        
        counter     (ref 0)

        tScrPane    (JScrollPane. table)
          
        model       (proxy [DefaultTableModel]  [@datamatrix (into-array (get-col-names database))])
        
        tListener   (proxy [TableModelListener] []
            (tableChanged [event]   
                (if (= 0 (.getType event));;table changed
                  (if (>  (.length (.getValueAt model
                                                (.getFirstRow event)
                                                (.getColumn event)))
                          (nth (get-field-lengths database) (.getColumn event)))
                      (.setValueAt  model ;;fires another event
                                    (trim-value (.getValueAt model 
                                                             (.getFirstRow event)
                                                             (.getColumn event)) 
                                                (nth (get-field-lengths database) (.getColumn event)))
                                    (.getFirstRow event)
                                    (.getColumn event))
                      (do (update-record-skip-deleted testfilename
                                                      (to-array (get-trimmed-values 
                                                                    (loop  [i (- (get-num-fields database) 1)  result ()] 
                                                                        (if (> i -1)
                                                                            (recur (dec i) (conj result 
                                                                                                 (.getValueAt model 
                                                                                                              (.getSelectedRow table) i)))
                                                                            result))
                                                                 (get-field-lengths database)))
                                                     (get-field-lengths database)
                                                     (.getFirstRow event)
                                                     (get-offset database))
                          (.setText label "Row updated"))))))
                    
        sorter      (proxy [TableRowSorter] [model])
        filter   (proxy [RowFilter]         []
                (include [entry]
                    (.contains (.getValueAt (.getModel entry) 
                                            (.intValue (.getIdentifier entry)) 
                                            (.getSelectedIndex searchBox)) ;;mod to skip
                               (.getText searchField))))
        
        ;;;;;Handlers
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
                         (.addRow model (into-array (vec (repeat (get-num-fields database) ""))))
                         ;;select the new row
                         (.addRowSelectionInterval table (- (.getRowCount table) 1) (- (.getRowCount table) 1))
                         ;;scroll down to see it
                         (.scrollRectToVisible table (.getCellRect table (- (.getRowCount table) 1) (.getColumnCount table) true))
                         ;;write in database
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
                         (.setRowFilter (.getRowSorter table) filter)
                         (.setText label "Filtered rows")))

  ];;;;;;;;;;;;; END LET
    
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
    
    ;;DEBUG
    ;(.setBackground     hPanel  (Color/yellow))
    ;(.setBackground     fPanel  (Color/cyan))
    ;(.setBackground     bPanel  (Color/magenta))
    ;(.setBackground     abPanel (Color/black))
    
    
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