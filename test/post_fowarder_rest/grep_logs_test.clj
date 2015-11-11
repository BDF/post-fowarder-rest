(ns post-fowarder-rest.grep-logs-test
  (:require [clojure.test :refer :all]
            [post-fowarder-rest.grep-logs :as qa-logs]))

(deftest test-build-form-params
  (testing "expected form params build correctly"
    (is (= (qa-logs/build-form-params "nightly" "2015-05-03" "greppy str") {}))
    ))
