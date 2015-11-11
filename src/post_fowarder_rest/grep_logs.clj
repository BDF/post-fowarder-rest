(ns post-fowarder-rest.grep-logs
  (:require [org.httpkit.client :as kit]))

(def host1 (System/getProperty "HOST1"))
(def host2 (System/getProperty "HOST2"))
(def host3 (System/getProperty "HOST3"))
(def host4 (System/getProperty "HOST4"))
(def host5 (System/getProperty "HOST5"))
(def host6 (System/getProperty "HOST6"))
(def host7 (System/getProperty "HOST7"))
(def post-form-to (System/getProperty "POST-TO"))


(def environ-to-post
  ; These are maps,  the key is :nightly
  {:nightly    {"grepHost_2" host2
              "grepHost_3" host3}
   :nigtly2  {"grepHost_4" host4
              "grepHost_5" host5}
   :nightly3 {"grepHost_6" host6
              "grepHost_7" host7}
   :preprod  {"grepHost_1" host1
              "grepHost_2" host2
              "grepHost_3" host3
              "grepHost_4" host4
              "grepHost_5" host5}}
  )

(defn build-form-params [environ date grepStr]
  {:form-params (merge {"grepString" grepStr
                        "dateString" date} ((keyword environ) environ-to-post))}
)

(defn post-qa [{:keys [grepString date environment] } ]
  (let [options (build-form-params environment date grepString)
        {:keys [body error]} @(kit/post post-form-to options)]
    (if error
      error
      body)))





