
;; trying to use staxmate rather than straight stax
#_(with-open [fos (-> "junk.xml"
                      jio/output-stream)]
    (cyphy/write-cad-assembly-using-knowledge fos nil)
    (with-open [fis (-> "junk.xml"
                        jio/input-stream)]
      (cyphy/read-cad-assembly-using-knowledge fis nil)))
