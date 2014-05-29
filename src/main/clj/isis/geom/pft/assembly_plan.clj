
(ns isis.geom.pft.assembly-plan
  (:require (isis.geom.pft coincident-slice
                           helical-slice
                           in-line-slice-fixed
                           in-line-slice-moves
                           in-plane-slice-fixed
                           in-plane-slice-moves
                           offset-x-slice
                           offset-z-slice
                           offset-z-slice
                           parallel-z-slice)) )

(def pft {[:coincident :h-tdof :h-rdof] pft-h-h-coincident,

          [:coincident :0-tdof :0-rdof] pft-0-0-coincident,
          [:coincident :1-tdof :0-rdof] pft-1-0-coincident,
          [:coincident :2-tdof :0-rdof] pft-2-0-coincident,
          [:coincident :3-tdof :0-rdof] pft-3-0-coincident,

          [:coincident :0-tdof :1-rdof] pft-0-1-coincident,
          [:coincident :1-tdof :1-rdof] pft-1-1-coincident,
          [:coincident :2-tdof :1-rdof] pft-2-1-coincident,
          [:coincident :3-tdof :1-rdof] pft-3-1-coincident,

          [:coincident :0-tdof :2-rdof] pft-0-2-coincident,
          [:coincident :1-tdof :2-rdof] pft-1-2-coincident,
          [:coincident :2-tdof :2-rdof] pft-2-2-coincident,
          [:coincident :3-tdof :2-rdof] pft-3-2-coincident,

          [:coincident :0-tdof :3-rdof] pft-0-3-coincident,
          [:coincident :1-tdof :3-rdof] pft-1-3-coincident,
          [:coincident :2-tdof :3-rdof] pft-2-3-coincident,
          [:coincident :3-tdof :3-rdof] pft-3-3-coincident,

          [:in-line-fixed :h-tdof :h-rdof] pft-h-h-in-line-fixed,

          [:in-line-fixed :0-tdof :0-rdof] pft-0-0-in-line-fixed,
          [:in-line-fixed :1-tdof :0-rdof] pft-1-0-in-line-fixed,
          [:in-line-fixed :2-tdof :0-rdof] pft-2-0-in-line-fixed,
          [:in-line-fixed :3-tdof :0-rdof] pft-3-0-in-line-fixed,

          [:in-line-fixed :0-tdof :1-rdof] pft-0-1-in-line-fixed,
          [:in-line-fixed :1-tdof :1-rdof] pft-1-1-in-line-fixed,
          [:in-line-fixed :2-tdof :1-rdof] pft-2-1-in-line-fixed,
          [:in-line-fixed :3-tdof :1-rdof] pft-3-1-in-line-fixed,

          [:in-line-fixed :0-tdof :2-rdof] pft-0-2-in-line-fixed,
          [:in-line-fixed :1-tdof :2-rdof] pft-1-2-in-line-fixed,
          [:in-line-fixed :2-tdof :2-rdof] pft-2-2-in-line-fixed,
          [:in-line-fixed :3-tdof :2-rdof] pft-3-2-in-line-fixed,

          [:in-line-fixed :0-tdof :3-rdof] pft-0-3-in-line-fixed,
          [:in-line-fixed :1-tdof :3-rdof] pft-1-3-in-line-fixed,
          [:in-line-fixed :2-tdof :3-rdof] pft-2-3-in-line-fixed,
          [:in-line-fixed :3-tdof :3-rdof] pft-3-3-in-line-fixed,
          })

(defn pft-entry
  "Returns the plan fragment table entry with indicated key."
  [?tdof ?rdof ?type]
  (get pft [?type ?tdof ?rdof] false))

(defn legal-pft-entry?
  "Returns 'true' if the tuple specifies a legal entry in the plan fragment table."
  [?tdof ?rdof ?type]
  (if (contains? pft [?type ?tdof ?rdof])))

(defn pft-entry-new-status
  "Returns the new-status slot of the plan fragment table entry."
  [pft-entry]
  (get pft-entry :new-status))

