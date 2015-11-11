(ns post-fowarder-rest.handler
  (:gen-class)
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.json :as mw-json]
            [ring.util.response :as rutil]
            [post-fowarder-rest.grep-logs :as qa-query]
            [post-fowarder-rest.parse :as qa-parse]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.adapter.jetty :refer (run-jetty)]))


(def lock-obj (Object.))

;; Why locking?
;; Resource this depends is fragile; block on more than one request at a time.
(defn query-and-response [req]
  (locking lock-obj (-> (:body req)
                        (qa-query/post-qa)
                        (qa-parse/parse))))

(defn invalid-date [date]
  (not (re-matches #"\d\d\d\d-\d\d-\d\d" date)))

(defn valid-environment [env]
  (contains? #{"nightly" "nightly2" "nightly3" "preprod"} env))

(defn validate-request [req]
  (let [json-vals (:body req)
        grepStr (:grepString json-vals)
        date (:date json-vals)
        env (:environment json-vals)
        errors (atom "")]
    (if (empty? grepStr)
      (swap! errors str "'grepString' must not be null or empty.\n"))
    (if (empty? date)
      (swap! errors str "'date' must not be null or empty.\n"))
    (if (and (not (empty? date)) (invalid-date date))
      (swap! errors str "'date' must be of the form 'YYYY-MM-DD' (i.e. '2016-04-03').\n"))
    (if (empty? env)
      (swap! errors str "'environment' must not be null or empty.\n"))
    (if (not (valid-environment env))
      (swap! errors str "'environment' must be one of the following 'nightly', 'nightly2', 'nightly3' or 'preprod'.\n"))
    (if (not-empty @errors)
      {:body @errors :status 400 :headers {"Content-Type" "text/plain"}}
      {})
    )
  )

(defroutes app-routes
           (GET "/status" req
             {:status 200 :body "" :headers {"Content-Type" "text/plain"}})
           (POST "/usagelog" req
             (let [errors (validate-request req)]
               (if (empty? errors)
                 (rutil/response (query-and-response req))
                 errors)
               )
             )
           (route/not-found "Not Found"))

(def app
  (-> (routes app-routes)
      (mw-json/wrap-json-body {:keywords? true})))

(defn -main [& args]
  (run-jetty app {:port 8085 :join? false }))


