(ns interface
(:use db)

)
(import '(javax.swing JFrame JPanel JButton JLabel JTable)
        '(javax.swing.table AbstractTableModel)
        '(java.awt.event ActionListener)
        '(java.awt BorderLayout Dimension FlowLayout)
)




(defn interface
  "nooooooooooooooooooooooooo"
  [title]
  (let [frame       (JFrame. title)
        panel       (JPanel. )  
        panel2       (JPanel. )  
        table       (JTable. 10 6)
        benjamin    (JButton. "Push me =)")
        btnShowall  (JButton. "Show All")
        btnUpdate   (JButton. "Update")
        btnDelete   (JButton. "Delete")
        btnFind     (JButton. "Find")
        btnLock     (JButton. "Lock Selected")
        btnUnlock   (JButton. "Unlock Selected")
        label       (JLabel. "Something" JLabel/CENTER)
        
        
        
        model (proxy[AbstractTableModel] [] 
        	(isCellEditable [row col] true)
        	(getRowCount [] 10)
        	(getColumnCount [] 10)
        	(getValueAt [row col] "")
        )
        	
  
        
        counter     (ref 0)
        hdlBenjamin     (proxy [ActionListener][]
                       (actionPerformed [event]
                       	       (dosync (alter counter inc))
                         (.setText label 
                                (str "Times pushed: " @counter))))
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
        
        
        
    ;
    (.setDefaultCloseOperation frame JFrame/EXIT_ON_CLOSE)
    ;
    (.setLayout panel (new FlowLayout))
    (.setLayout frame (new FlowLayout))
    
    (.setPreferredSize label (Dimension. 300 50))
    
    ;
    
    
    (.setModel table model) 
    (.set
    (.add panel2 table)
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