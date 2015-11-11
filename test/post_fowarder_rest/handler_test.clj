(ns post-fowarder-rest.handler-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [post-fowarder-rest.handler :refer :all]))

(deftest test-app
  (testing "POST qa-usage-grep route"
    (let [response (app (mock/request
                          :post "/usagelog" {:body         "{ \"grepString\": \"Greppy\", \"date\": \"2015-11-03\", \"environment\": \"nightly\" }"
                                             :content-type "application/json"}))]
      (is (= (:status response) 200))
      (prn response)))
  )

(deftest test-app2
  (testing "POST qa-usage-grep route"
    (let [m-request (mock/request :post "/usagelog")
          json-vals "{\"date\": \"2015-11-04\" }"
          mock-body (mock/body m-request json-vals)
          m-content (mock/content-type mock-body "application/json")
          response (app m-content)]
      (is (= (:status response) 200))
      (prn response)))
  )

(deftest test-app-no-route
  (testing "not-found route"
    (let [response (app (mock/request :get "/invalid"))]
      (is (= (:status response) 404))
      (is (= (:body response)) "Not Found"))))

(deftest test-invalid-environment
  (testing "these should all be valid"
    (is (not (valid-environment "nightly")))
    (is (not (valid-environment "nightly2")))
    (is (not (valid-environment "nightly3")))
    (is (not (valid-environment "preprod")))
    )
  (testing "should be invlaid"
    (is (valid-environment "bad-environ"))))

(deftest test-invalid-date
  (testing "return true for invalid date"
    (is (invalid-date "234"))
    (is (not (invalid-date "2010-23-24")))))

(deftest test-validate-request
  (testing "should catch invalid requests for qa util query"
    (let [valid {:body {:environment "preprod" :date "2015-04-03" :grepString "robot arms"}}]
      (is (empty? (validate-request valid))))))