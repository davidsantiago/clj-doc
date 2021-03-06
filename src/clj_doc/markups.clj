;;;;  markups.clj
;;;;
;;;;  Copyright (c)2010 Nicolas Buduroi. All rights reserved.
;;;;
;;;;  The use and distribution terms for this software are covered by
;;;;  the Eclipse Public License 1.0 which can be found in the file
;;;;  epl-v10.html at the root of this distribution. By using this
;;;;  software in any fashion, you are agreeing to be bound by the
;;;;  terms of this license.
;;;;
;;;;  You must not remove this notice, or any other, from this software.

(ns clj-doc.markups
  "This namespace contains all functions needed to build and find
  markups."
  (use [clojure.contrib seq-utils]
       clj-doc.utils))

(defstruct #^{:doc "Struct to contains markup element generators."}
  markup
  :page
  :title
  :namespace
  :var-name
  :var-doc)

(defn create-markup
  "Shortcut for struct-map markup."
  [& elements]
  (apply struct-map markup elements))

(defmacro defmarkup
  "Define a markup using create-markup so that it can be found later by
  the markups function."
  [name & elements]
  `(def ~(with-meta name
           (merge (meta name) {:type ::Markup}))
     (create-markup ~@elements)))

(defn markup?
  "Returns true if the given var is a markup."
  [var]
  (= (type var) ::Markup))

(defn find-markups
  "Returns a sequence of vars for all markups found in the current
  namespace if there's no arguments, else search in the given
  namespaces."
  [& nss]
  (apply require nss)
  (apply concat
    (map #(filter markup? (vals (ns-interns %)))
      (if (empty? nss) [*ns*] nss))))

(defn get-available-markups
  "Lists available markups."
  []
  (let [mks (apply find-markups
              (find-nss #"^clj-doc\.markups\..*"))]
    (apply hash-map
      (flatten (map #(vector (.sym %) (.get %)) mks)))))

(defmethod print-method
  ::Markup
  [o w]
  (.write w (str o)))
