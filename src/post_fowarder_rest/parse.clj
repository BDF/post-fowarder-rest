(ns post-fowarder-rest.parse)

(defn find-json [in-str]
  (re-seq (re-pattern "(\\{.*\\})") in-str))

(defn wrap-with-curly [str]
  (format "{%s}" str))

(defn label-each-with-json [seq]
  (map #(str (if (> %1 0) "," ) \" %1 \" %2 (first %3))
       (iterate inc 0)
       (repeat ":")
       seq)
  )

(defn convert-to-json [in-list]
  (->> in-list
       (remove nil? )
       (label-each-with-json )
       (clojure.string/join)
       (wrap-with-curly)
      )
  )

(defn parse [src-to-parse]
  (-> src-to-parse
      (find-json )
      (convert-to-json)
      )
  )
