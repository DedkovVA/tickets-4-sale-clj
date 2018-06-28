(ns t4s.test-core
  (:require 
    [clojure.test :refer :all]
    [t4s.core :as t4sc]
    [clj-time.core :as cjt]
    )
  )

(use 'clojure.test)

(def dt1 (t4sc/str-to-date "2018-12-25"))
(def dt2 (t4sc/str-to-date "2018-12-26"))
(deftest str-to-date-test
  (is (= 25 (cjt/day dt1)))
  (is (= 12 (cjt/month dt1)))
  (is (= 2018 (cjt/year dt1)))
  (is (= 0 (cjt/hour dt1)))
  (is (= 0 (cjt/minute dt1)))
  (is (= 0 (cjt/second dt1)))
  (is (= 0 (cjt/milli dt1)))
  (is (= 0 (cjt/milli dt1)))
  (is (= 1 (t4sc/diff-in-days dt1 dt2)))
  (is (= -1 (t4sc/diff-in-days dt2 dt1)))
  )
