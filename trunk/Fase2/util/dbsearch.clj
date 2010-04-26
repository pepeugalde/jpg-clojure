(ns util.dbsearch)
(import '(javax.swing JTable)
        '(javax.swing.table DefaultTableModel)
        '(javax.swing.event TableModelListener)
)

;JAVA
;public void Search(JTable table){
;
;int i=0;
;//javax.swing.table.TableModel model = table.getModel();
;Enumeration enum = table.getColumnModel().getColumns();
;String phone = phoneField.getText();
;boolean found = false;
;if(!found){
;( (ClientDatabase) (enum.nextElement())).getName().startsWith(phone);
;table.setRowSelectionInterval(i, i);
;}
;else{
;JOptionPane.showMessageDialog(null, "No Match Found.", "Message", JOptionPane.PLAIN_MESSAGE);
;}
;}


(defn search
    "Returns the result of a searched value"
    [table col sstring]
    (let   [i       0
            enum    (.getColumns (.getColumnModel table))
            found   false]
            
        (if (not found)
             ;(ClientDatabase)
             (.startsWith (.getName (.nextElement enum)) search)
             (.setRowSelectionInterval table col i) 
        )
        (if (found)
          (print "lol")
          ;JOptionPane.showMessageDialog(null, "No Match Found.", "Message", JOptionPane.PLAIN_MESSAGE);
        )
 	 )
)
