(ns interface
(:use db)

)
(import '(javax.swing JFrame JPanel JButton JLabel JTable JScrollPane)
        '(javax.swing.table AbstractTableModel)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout FlowLayout GridLayout Dimension Color)
)


(defn interface
  "Displays the interface that will be uswd in the urlybird project"
  [title]
  (let [dict        (read-bin-file "db-1x2.db")
        
        frameSizeX  800
        frameSizeY  600
        tableSizeX  550
        tableSizeY  400
        
        frame       (JFrame. title)
        ;tPanel      (JPanel. )  
        bPanel      (JPanel. )  
        
        benjamin    (JButton. "Push me =)")
        btnShowall  (JButton. "Show All")
        btnUpdate   (JButton. "Update")
        btnDelete   (JButton. "Delete")
        btnFind     (JButton. "Find")
        btnLock     (JButton. "Lock Selected")
        btnUnlock   (JButton. "Unlock Selected")
        
        label       (JLabel. "Something")
        
        counter     (ref 0)
        
        table       (JTable. )
        tScrPane    (JScrollPane. table)
        
        model (proxy [AbstractTableModel] [] 
        	(isCellEditable [row col] true)                 ;All cells are editable
        	(getRowCount [] (count (get dict :records)))    ;Gets number of rows from database
        	(getColumnCount [] (get dict :num-fields))      ;Gets number of cols from database
        	;;Sets column names as the string in :fields vector
            (getColumnName [col]                            ;
                    (.toUpperCase (str (first (nth (get dict :fields) col)))))
            ;;Uses column name as keyword to get value in row tuple
            (getValueAt [row col]                           ;
                    (get (nth (get dict :records) row) (keyword (str (first (nth (get dict :fields) col))))))
        )
        
        hdlBenjamin     (proxy [ActionListener][]
                       (actionPerformed [event]
                       	 (dosync (alter counter inc))
                         (.setText label 
                                (str "COL NAME: " (.getColumnName table (rem @counter (get dict :num-fields)))) )))                       
        hdlShowall     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "CLASS: " (class(nth (get dict :records) (rem @counter (get dict :num-fields))))) )))
        hdlUpdate     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "TUPLA: " (nth (get dict :records) (rem @counter(get dict :num-fields)))))))
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "PSEUDO: " (keyword (str(first (nth (get dict :fields) (rem @counter (get dict :num-fields))))))))))
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (get (nth (get dict :records) (rem @counter (get dict :num-fields))) :rate) )))
        hdlLock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "KEYS" (str(class(nth(keys (nth (get dict :records) (rem @counter (get dict :num-fields)) ))0)))))))
                                
                                
        hdlUnlock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))]
    ;;;;;;;;;;;;; END LET
        
    ;;;FRAME
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    (.setLayout frame (new FlowLayout))
    
    ;;;PANEL
    ;(.setLayout tPanel (new FlowLayout))
    (.setLayout bPanel (new GridLayout 10 2 15 15))
    ;(.setBackground tPanel (Color/yellow))
    ;(.setBackground bPanel (Color/magenta))
    
    ;;;TABLE
    (.setModel table model)
    ;(.setAutoResizeMode table JTable/AUTO_RESIZE_OFF)
    ;(.setPreferredSize table (Dimension. tableSizeX tableSizeY))
    (.setPreferredScrollableViewportSize table (Dimension. tableSizeX tableSizeY))
    (.setFillsViewportHeight table true)
    (.setPreferredSize tScrPane (Dimension. tableSizeX tableSizeY))
    
    ;;;BUTTON
    
    ;;;LABEL
    (.setPreferredSize label (Dimension. 500 25))
    
    ;;;ADD
    ;(.add tPanel (.getTableHeader table) BorderLayout/NORTH)
    ;(.add tPanel tScrPane)
    ;(.add tPanel BorderLayout/SOUTH table)
    
    (.add bPanel benjamin)
    (.add bPanel btnShowall)
    (.add bPanel btnUpdate)
    (.add bPanel btnDelete)
    (.add bPanel btnFind)
    (.add bPanel btnLock)
    (.add bPanel btnUnlock)
    
    (.add frame  tScrPane);tPanel)
    (.add frame  bPanel)
    (.add frame  label)
    ;(.add frame BorderLayout/CENTER table)
    
    ;;;ACTION LISTENERS
    (.addActionListener benjamin hdlBenjamin)
    (.addActionListener btnShowall hdlShowall)
    (.addActionListener btnUpdate hdlUpdate)
    (.addActionListener btnDelete hdlDelete)
    (.addActionListener btnFind hdlFind)
    (.addActionListener btnLock hdlLock)
    (.addActionListener btnUnlock hdlUnlock)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame frameSizeX frameSizeY)
  )
)

(interface "Fase 1")