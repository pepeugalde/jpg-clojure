(ns interface
(:use db)

)
(import '(javax.swing JFrame JPanel JButton JLabel JTable)
        '(javax.swing.table AbstractTableModel)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout Dimension FlowLayout)
)




(defn interface
  "Displays the interface that will be uswd in the urlybird project"
  [title]
  (let [dict        (read-bin-file "db-1x2.db")
  
        frame       (JFrame. title)
        panel       (JPanel. )  
        panel2      (JPanel. )  
        table       (JTable. 10 6)
        benjamin    (JButton. "Push me =)")
        btnShowall  (JButton. "Show All")
        btnUpdate   (JButton. "Update")
        btnDelete   (JButton. "Delete")
        btnFind     (JButton. "Find")
        btnLock     (JButton. "Lock Selected")
        btnUnlock   (JButton. "Unlock Selected")
        label       (JLabel. "Something" JLabel/CENTER)
        
        model (proxy [AbstractTableModel] [] 
        	(isCellEditable [row col] true)
        	(getRowCount [] (count (get dict :records)))
        	(getColumnCount [] (get dict :num-fields))
        	(getValueAt [row col] (get (nth (get dict :records) row) (symbol (str ":" (first (nth (get dict :fields) col))))))
            ;(getColumnName [col] (.toUpperCase (str (first (nth (get dict :fields) col)))))
        )
        
        counter     (ref 0)
        hdlBenjamin     (proxy [ActionListener][]
                       (actionPerformed [event]
                       	 (dosync (alter counter inc))
                         (.setText label 
                                (str (get (nth (get dict :records) 0) (symbol ":rate"))))))
                                
        hdlShowall     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlUpdate     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlDelete     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlFind     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlLock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
        hdlUnlock     (proxy [ActionListener][]
                       (actionPerformed [event]
                         (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))]
    ;;FIN LET
        
    ;
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    ;
    (.setLayout panel (new FlowLayout))
    (.setLayout frame (new FlowLayout))
    
    (.setPreferredSize label (Dimension. 300 50))
    
    ;
    
    
    (.setModel table model) 
    (.add panel2 table)
    
    (.add panel label)
    
    (.add panel benjamin)
    (.add panel btnShowall)
    (.add panel btnUpdate)
    (.add panel btnDelete)
    (.add panel btnFind)
    (.add panel btnLock)
    (.add panel btnUnlock)
    (.add frame panel)
    (.add frame panel2)
    
    
    ;(.add frame BorderLayout/CENTER table)
    
    (.addActionListener benjamin hdlBenjamin)
    (.addActionListener btnShowall hdlShowall)
    (.addActionListener btnUpdate hdlUpdate)
    (.addActionListener btnDelete hdlDelete)
    (.addActionListener btnFind hdlFind)
    (.addActionListener btnLock hdlLock)
    (.addActionListener btnUnlock hdlUnlock)
    
    (.pack frame)
    (.setVisible frame true)
    (.setSize frame 800 600)
    (.setModel table 5 5)
    
  )
  
)

(interface "Fase 1")